package by.timeslowly.wing_kirin.client.eventhandler;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.config.WKClientConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 疾速残影（斯安威斯坦式虚影）—— 26.1 版重写，纯客户端实现。
 * <p>
 * 26.1 的渲染管线改为提交式（RenderState + SubmitNodeCollector），旧版的立即模式
 * render() 入口已不存在。新版实现：
 * <ul>
 *   <li>快照捕获与老化逻辑与旧版一致（ClientTickEvent 中按间隔记录位置/朝向）；</li>
 *   <li>渲染经由 {@link SubmitCustomGeometryEvent} 拿到 {@link SubmitNodeCollector}；
 *      普通玩家分支提取 {@link AvatarRenderState} 后走 {@code AvatarRenderer.submit}
 *      完整路径（旋转/镜像/缩放由原版处理），透明度由
 *       {@link by.timeslowly.wing_kirin.mixins.LivingEntityAfterimageMixin} 依据
 *       {@link #ghostAlpha} 强制半透明并替换顶点色；</li>
 *   <li>龙玩家分支提取 {@link GeoRenderState} 后走 {@code DragonRenderer.submit}
 *      （身体朝向偏移/旋转由 DS 的 setupRender 内部处理），透明度由
 *      {@link by.timeslowly.wing_kirin.mixins.DragonRendererGhostMixin} 强制半透明，
 *      顶点色通过覆写 RENDER_COLOR ticket 承载；</li>
 *   <li>姿态冻结：龙动画的控制器状态机按实例共享、无法回退，状态级时间冻结无效。
 *      因此由 mixin 在本体龙的真实通道中每帧捕获最终骨骼姿态存入
 *      {@link #lastDragonPoses}，快照引用之；残影通道以 BoneUpdater 把冻结姿态
 *      写回 BoneSnapshot（普通玩家分支冻结行走动画相位）；</li>
 *   <li>光照按残影所在位置打包（{@link LightCoordsUtil#pack}，26.1 起 LightTexture.pack 移除）。</li>
 * </ul>
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID, value = Dist.CLIENT)
public class AfterimageRenderHandler {
    /** 残影最大透明度（新生成的残影） */
    private static final float MAX_ALPHA = 0.55F;
    /** 残影从生成到完全消失所需的 tick 数（1 秒） */
    private static final int FADE_TICKS = 20;
    /** 快照捕获间隔（tick），降低生成密度 */
    private static final int CAPTURE_INTERVAL = 3;
    /** 水平移动速度平方阈值（约 0.02 格/tick），低于该速度不产生残影 */
    private static final double MIN_MOVE_SPEED_SQR = 0.0004D;
    /** 第一人称下残影渲染起始点与玩家当前位置的最小水平距离（格） */
    private static final double FIRST_PERSON_MIN_DISTANCE_SQR = 1.5D * 1.5D;
    /** 第一人称下残影整体沿快照朝向向后偏移的距离（格） */
    private static final double FIRST_PERSON_POSE_OFFSET = 2.0D;

    /**
     * 当前残影透明度（0~1），-1 表示非残影渲染 —— 供两个残影 mixin 读取。
     * 仅在渲染线程内、于残影渲染前后严格配对赋值/复位，无并发问题。
     */
    public static float ghostAlpha = -1F;

    /**
     * 当前残影的冻结骨骼姿态（龙分支）—— 由 {@link by.timeslowly.wing_kirin.mixins.DragonRendererGhostMixin}
     * 在残影通道读取并写回 BoneSnapshot；与 ghostAlpha 同步赋值/复位。
     */
    public static @Nullable Map<String, GhostBonePose> ghostPoses = null;

    /**
     * 本体龙每帧渲染的最终骨骼姿态（键为 DS 的 renderCacheId）——
     * 由 mixin 在本体龙通道中更新，快照创建时读取。
     * 仅渲染线程访问。
     */
    public static final Map<Long, Map<String, GhostBonePose>> lastDragonPoses = new HashMap<>();

    /** 单个骨骼的冻结姿态（BoneSnapshot 的 scale/translation/rotation 三组变换） */
    public record GhostBonePose(float scaleX, float scaleY, float scaleZ,
                                float translateX, float translateY, float translateZ,
                                float rotX, float rotY, float rotZ) {

        public static GhostBonePose from(@NotNull BoneSnapshot snapshot) {
            return new GhostBonePose(
                    snapshot.getScaleX(), snapshot.getScaleY(), snapshot.getScaleZ(),
                    snapshot.getTranslateX(), snapshot.getTranslateY(), snapshot.getTranslateZ(),
                    snapshot.getRotX(), snapshot.getRotY(), snapshot.getRotZ());
        }

        public void applyTo(@NotNull BoneSnapshot snapshot) {
            snapshot.setScale(scaleX, scaleY, scaleZ);
            snapshot.setTranslation(translateX, translateY, translateZ);
            snapshot.setRotation(rotX, rotY, rotZ);
        }
    }

    /** 客户端 tick 计数器（残影年龄的基准） */
    private static int tickCounter = 0;

    /**
     * 单个残影快照：位置 + 头朝向（视向）+ 身体朝向 + 俯仰 + 出生 tick +
     * 出生时的行走动画相位（玩家）+ 冻结骨骼姿态（龙）。
     */
    private record GhostSnapshot(double x, double y, double z, float headYaw, float bodyYaw, float pitch, int bornTick,
                                 float walkPos, float walkSpeed,
                                 @Nullable Map<String, GhostBonePose> dragonPoses) {}

    /** 单个玩家的残影轨迹：队首为最旧快照，队尾为最新快照 */
    private static final class AfterimageTrail {
        private final ArrayDeque<GhostSnapshot> snapshots = new ArrayDeque<>();
    }

    private static final Map<UUID, AfterimageTrail> TRAILS = new HashMap<>();

    /** 是否在地面行走/疾跑（残影仅在该状态下产生；飞行/游泳/下落/潜行等不产生） */
    private static boolean isWalkingOnGround(@NotNull Player player) {
        if (!player.onGround() || player.isCrouching()) {
            return false;
        }
        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        return dx * dx + dz * dz > MIN_MOVE_SPEED_SQR;
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            TRAILS.clear();
            return;
        }
        tickCounter++;

        boolean shouldCapture = tickCounter % CAPTURE_INTERVAL == 0;

        // 记录本 tick 需要产生残影的玩家（持有效果 + 地面行走/疾跑 + 存活）
        Map<UUID, Player> capturing = new HashMap<>();
        for (Player player : mc.level.players()) {
            if (player.isAlive() && player.hasEffect(WKEffects.UNSTOPPABLE_SPEED) && isWalkingOnGround(player)) {
                capturing.put(player.getUUID(), player);
            }
        }

        // 已有轨迹：快照按年龄自然老化；捕获 tick 追加新快照
        TRAILS.entrySet().removeIf(entry -> {
            AfterimageTrail trail = entry.getValue();
            trail.snapshots.removeIf(s -> tickCounter - s.bornTick >= FADE_TICKS);
            if (shouldCapture) {
                Player player = capturing.get(entry.getKey());
                if (player != null) {
                    trail.snapshots.addLast(createSnapshot(player));
                }
            }
            return trail.snapshots.isEmpty();
        });

        // 本 tick 新获得效果/移动状态的玩家：创建轨迹（仅捕获 tick）
        if (shouldCapture) {
            for (Map.Entry<UUID, Player> entry : capturing.entrySet()) {
                if (TRAILS.containsKey(entry.getKey())) {
                    continue;
                }
                AfterimageTrail trail = new AfterimageTrail();
                trail.snapshots.addLast(createSnapshot(entry.getValue()));
                TRAILS.put(entry.getKey(), trail);
            }
        }
    }

    private static @NotNull GhostSnapshot createSnapshot(@NotNull Player player) {
        Vec3 pos = player.position();
        boolean isDragon = DragonStateProvider.isDragon(player);
        // 龙玩家的身体/头部朝向由 DS 的 MovementData 决定（headYaw/headPitch 是龙模型的
        // 平滑跟随值，直接取玩家视向会导致残影头部朝向与本体不一致），普通玩家由实体字段决定
        MovementData movement = isDragon ? MovementData.getData(player) : null;
        float bodyYaw = isDragon ? (float) movement.bodyYaw : player.yBodyRot;
        float headYaw = isDragon ? (float) movement.headYaw : player.getYRot();
        float pitch = isDragon ? (float) movement.headPitch : player.getXRot();
        float walkPos = player.walkAnimation.position(1.0F);
        float walkSpeed = player.walkAnimation.speed(1.0F);

        // 龙玩家：引用 mixin 在本体龙通道捕获的最终骨骼姿态（上一帧渲染结果，近似当前姿态）
        Map<String, GhostBonePose> dragonPoses = null;
        if (isDragon) {
            DragonEntity dragon = ClientDragonRenderer.getDragon(player);
            if (dragon != null) {
                Map<String, GhostBonePose> current = lastDragonPoses.get(Integer.toUnsignedLong(dragon.getId()));
                if (current != null && !current.isEmpty()) {
                    dragonPoses = Map.copyOf(current);
                }
            }
        }

        return new GhostSnapshot(pos.x, pos.y, pos.z, headYaw, bodyYaw, pitch, tickCounter,
                walkPos, walkSpeed, dragonPoses);
    }

    /**
     * 26.1 的官方自定义几何提交点：在粒子提交与不透明提交渲染之间触发，
     * 提交的半透明节点会由收集器自动排入半透明通道。
     */
    @SubscribeEvent
    public static void onSubmitCustomGeometry(final SubmitCustomGeometryEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || TRAILS.isEmpty()) {
            return;
        }

        SubmitNodeCollector collector = event.getSubmitNodeCollector();
        com.mojang.blaze3d.vertex.PoseStack poseStack = event.getPoseStack();
        CameraRenderState camera = event.getLevelRenderState().cameraRenderState;
        Vec3 cameraPos = camera.pos;
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        boolean firstPersonLocal = mc.options.getCameraType().isFirstPerson();
        boolean showFirstPerson = WKClientConfig.shouldShowAfterimagesInFirstPerson();
        double renderDistance = mc.options.getEffectiveRenderDistance() * 16.0;
        double renderDistanceSqr = renderDistance * renderDistance;

        for (Map.Entry<UUID, AfterimageTrail> entry : TRAILS.entrySet()) {
            Player player = mc.level.getPlayerByUUID(entry.getKey());
            AfterimageTrail trail = entry.getValue();
            if (player == null || !player.isAlive()) {
                continue;
            }
            if (player.distanceToSqr(cameraPos) > renderDistanceSqr) {
                continue;
            }
            boolean isDragon = DragonStateProvider.isDragon(player);

            for (GhostSnapshot snapshot : trail.snapshots) {
                int age = tickCounter - snapshot.bornTick();
                if (age < 0) {
                    continue;
                }
                // 第一人称下本地玩家的残影：配置关闭则跳过；开启时与玩家当前位置的
                // 水平距离不足时不渲染（相机位于模型头部，近处的残影会遮挡视线）
                boolean offsetPose = false;
                if (firstPersonLocal && player == mc.player) {
                    if (!showFirstPerson) {
                        continue;
                    }
                    double dx = snapshot.x() - player.getX();
                    double dz = snapshot.z() - player.getZ();
                    if (dx * dx + dz * dz < FIRST_PERSON_MIN_DISTANCE_SQR) {
                        continue;
                    }
                    offsetPose = true;
                }
                float alpha = MAX_ALPHA * (1.0F - age / (float) FADE_TICKS);
                if (alpha > 0.01F) {
                    if (isDragon) {
                        submitDragonGhost(mc, player, snapshot, alpha, partialTick, collector, poseStack, camera, cameraPos, offsetPose);
                    } else {
                        submitPlayerGhost(mc, player, snapshot, alpha, partialTick, collector, poseStack, camera, cameraPos, offsetPose);
                    }
                }
            }
        }
    }

    /** 普通玩家残影：提取 RenderState 后走渲染器完整提交路径（旋转/镜像/缩放由原版处理） */
    private static void submitPlayerGhost(@NotNull Minecraft mc, @NotNull Player player, @NotNull GhostSnapshot snapshot,
                                          float alpha, float partialTick, @NotNull SubmitNodeCollector collector,
                                          @NotNull com.mojang.blaze3d.vertex.PoseStack poseStack,
                                          @NotNull CameraRenderState camera, @NotNull Vec3 cameraPos, boolean offsetPose) {
        EntityRenderer<?, ?> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
        // 原始类型匹配：AvatarRenderer 的泛型约束（Avatar & ClientAvatarEntity）无法在通配符下证明
        if (!(renderer instanceof AvatarRenderer avatarRenderer) || !(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }

        AvatarRenderState state = avatarRenderer.createRenderState();
        // 残影渲染的旋转取自实体自身字段：临时覆写为快照朝向（身体朝身体朝向、头部朝头朝向），提取后恢复
        float oldBodyYaw = player.yBodyRot;
        float oldBodyYawO = player.yBodyRotO;
        float oldHeadYaw = player.yHeadRot;
        float oldHeadYawO = player.yHeadRotO;
        float oldXRot = player.getXRot();
        float oldXRotO = player.xRotO;
        try {
            player.yBodyRot = player.yBodyRotO = snapshot.bodyYaw();
            player.yHeadRot = player.yHeadRotO = snapshot.headYaw();
            player.setXRot(snapshot.pitch());
            player.xRotO = snapshot.pitch();
            avatarRenderer.extractRenderState(clientPlayer, state, partialTick);
        } finally {
            player.yBodyRot = oldBodyYaw;
            player.yBodyRotO = oldBodyYawO;
            player.yHeadRot = oldHeadYaw;
            player.yHeadRotO = oldHeadYawO;
            player.setXRot(oldXRot);
            player.xRotO = oldXRotO;
        }

        // 冻结动画：写回快照出生时刻的行走相位，残影不随玩家继续摆动
        state.walkAnimationPos = snapshot.walkPos();
        state.walkAnimationSpeed = snapshot.walkSpeed();
        // 光照按残影所在位置打包
        BlockPos ghostPos = BlockPos.containing(snapshot.x(), snapshot.y(), snapshot.z());
        state.lightCoords = LightCoordsUtil.pack(
                player.level().getBrightness(LightLayer.BLOCK, ghostPos),
                player.level().getBrightness(LightLayer.SKY, ghostPos));

        poseStack.pushPose();
        // 第一人称本地玩家：残影整体沿快照朝向向后偏移（模型前缘不越过玩家，避免遮挡视线）
        double offsetX = 0;
        double offsetZ = 0;
        if (offsetPose) {
            float yawRad = snapshot.bodyYaw() * Mth.DEG_TO_RAD;
            offsetX = Mth.sin(yawRad) * FIRST_PERSON_POSE_OFFSET;
            offsetZ = -Mth.cos(yawRad) * FIRST_PERSON_POSE_OFFSET;
        }
        poseStack.translate(snapshot.x() - cameraPos.x + offsetX, snapshot.y() - cameraPos.y, snapshot.z() - cameraPos.z + offsetZ);

        // 透明度由 LivingEntityAfterimageMixin 依据 ghostAlpha 强制半透明并替换顶点色
        try {
            ghostAlpha = alpha;
            avatarRenderer.submit(state, poseStack, collector, camera);
        } finally {
            ghostAlpha = -1F;
        }
        poseStack.popPose();
    }

    /** 龙玩家残影：提取 GeoRenderState 后走 DragonRenderer 提交路径（身体朝向偏移/旋转由 DS 内部处理） */
    private static void submitDragonGhost(@NotNull Minecraft mc, @NotNull Player player, @NotNull GhostSnapshot snapshot,
                                          float alpha, float partialTick, @NotNull SubmitNodeCollector collector,
                                          @NotNull com.mojang.blaze3d.vertex.PoseStack poseStack,
                                          @NotNull CameraRenderState camera, @NotNull Vec3 cameraPos, boolean offsetPose) {
        DragonEntity dragon = getOrCreateDragonEntity(player);
        if (dragon == null) {
            return;
        }
        EntityRenderer<?, ?> renderer = mc.getEntityRenderDispatcher().getRenderer(dragon);
        if (!(renderer instanceof DragonRenderer dragonRenderer)) {
            return;
        }

        // 残影朝向取自快照：临时覆写 MovementData（DragonRenderData.live 读取）；
        // geckolib 的无参 createRenderState() 有意返回 null，
        // 用 final 的 (entity, partialTick) 变体一步完成创建+提取（覆写值在提取期间生效）
        MovementData movement = MovementData.getData(player);
        double oldBodyYaw = movement.bodyYaw;
        double oldHeadYaw = movement.headYaw;
        double oldHeadPitch = movement.headPitch;
        movement.bodyYaw = snapshot.bodyYaw();
        movement.headYaw = snapshot.headYaw();
        movement.headPitch = snapshot.pitch();
        EntityRenderState state = dragonRenderer.createRenderState(dragon, partialTick);
        movement.bodyYaw = oldBodyYaw;
        movement.headYaw = oldHeadYaw;
        movement.headPitch = oldHeadPitch;

        GeoRenderState geoState = (GeoRenderState) state;
        // 半透明渲染色（RENDER_COLOR ticket）
        geoState.addGeckolibData(DataTickets.RENDER_COLOR, 0xFFFFFF | (Math.round(alpha * 255.0F) << 24));
        // 光照按残影所在位置打包
        BlockPos ghostPos = BlockPos.containing(snapshot.x(), snapshot.y(), snapshot.z());
        ((LivingEntityRenderState) state).lightCoords = LightCoordsUtil.pack(
                player.level().getBrightness(LightLayer.BLOCK, ghostPos),
                player.level().getBrightness(LightLayer.SKY, ghostPos));

        poseStack.pushPose();
        // 第一人称本地玩家：残影整体沿快照朝向向后偏移
        double offsetX = 0;
        double offsetZ = 0;
        if (offsetPose) {
            float yawRad = snapshot.bodyYaw() * Mth.DEG_TO_RAD;
            offsetX = Mth.sin(yawRad) * FIRST_PERSON_POSE_OFFSET;
            offsetZ = -Mth.cos(yawRad) * FIRST_PERSON_POSE_OFFSET;
        }
        poseStack.translate(snapshot.x() - cameraPos.x + offsetX, snapshot.y() - cameraPos.y, snapshot.z() - cameraPos.z + offsetZ);

        // 透明度由 DragonRendererGhostMixin 依据 ghostAlpha 强制半透明，顶点色由 RENDER_COLOR ticket 承载；
        // 冻结姿态由 mixin 从 ghostPoses 写回 BoneSnapshot
        try {
            ghostAlpha = alpha;
            ghostPoses = snapshot.dragonPoses();
            dragonRenderer.submit(state, poseStack, collector, camera);
        } finally {
            ghostAlpha = -1F;
            ghostPoses = null;
        }
        poseStack.popPose();
    }

    /**
     * 获取观察者客户端上某玩家的龙实体：优先取 DS 已创建的实例（正常渲染路径），
     * 不存在时兜底创建（视野外/未被渲染过的远程龙玩家也能渲染残影；
     * 该实体不进世界、不参与 tick，仅用于残影渲染，由 DS 的 EntityLeaveLevelEvent 清理）
     */
    private static @Nullable DragonEntity getOrCreateDragonEntity(Player player) {
        DragonEntity dragon = ClientDragonRenderer.getDragon(player);
        if (dragon != null) {
            return dragon;
        }
        return ClientDragonRenderer.getOrCreateDragon(player);
    }
}
