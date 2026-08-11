package by.timeslowly.wing_kirin.client.eventhandler;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MovementData;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.config.WKClientConfig;
import by.timeslowly.wing_kirin.mixins.GeoEntityRendererAccessor;
import by.timeslowly.wing_kirin.mixins.LivingEntityAfterimageMixin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 疾速残影（斯安威斯坦式虚影）—— 纯客户端实现，服务端无任何改动。
 * <p>
 * 玩家持有 UnstoppableSpeed 效果且<b>在地面行走/疾跑</b>时，每 {@link #CAPTURE_INTERVAL}
 * tick 记录一次位置/朝向快照（头朝向与身体朝向分离记录，飞行等其他动作不产生残影）；
 * 每帧在实体渲染完成后（RenderLevelStageEvent.AFTER_ENTITIES）按快照位置，以随年龄递减的
 * 透明度渲染该玩家的模型副本（残影），由实渐虚直至消失。停止移动或效果结束后，存量残影
 * 按年龄自然老化淡出，无需显式清理。
 * <p>
 * 渲染分支：
 * <ul>
 *   <li>普通玩家：走原版 {@link LivingEntityRenderer} 管线；临时覆写 yBodyRot/yHeadRot/xRot
 *       为快照值（身体朝身体朝向、头部朝头朝向），透明度由 {@link LivingEntityAfterimageMixin}
 *       强制半透明渲染 + 修改顶点颜色 alpha 实现（参考 Dragon Survival 的
 *       LivingEntityRendererMixin / HunterHandler 模式）。</li>
 *   <li>龙玩家：GeckoLib 管线，直接调 {@code DragonRenderer.actuallyRender} 并以 ARGB 颜色
 *       传入 alpha（参考 DragonGlowLayerRenderer 模式）。残影姿势**冻结**：每帧渲染循环前
 *       捕获当前骨骼姿态，写入最新快照；渲染每个残影前把其快照姿态写回骨骼树
 *       （isReRender=true 下 renderRecursively 不重算骨骼，写回即生效），渲染后恢复当前姿态。
 *       全程不触碰 GeckoLib 动画控制器状态机（额外驱动会导致控制器停在 TRANSITIONING/STOPPED
 *       状态而让骨骼冻结在绑定姿势，即 T 姿势）；身体朝向通过临时覆写 MovementData.bodyYaw
 *       控制（DragonRenderer.setupRender 读取），渲染后恢复。渲染期间手动补上 GeckoLib
 *       渲染器内部 this.animatable 字段（由 render() 设置、doPostRenderCleanup() 清空），
 *       结束后复位。</li>
 * </ul>
 * 第一人称视角下本地玩家自己的残影由客户端配置
 * {@link WKClientConfig#shouldShowAfterimagesInFirstPerson()} 控制：关闭时不渲染；
 * 开启时残影起始点向后移动（跳过离玩家最近的新残影），因为龙模型头部组件在第一人称下
 * 被隐藏、相机位于头部位置，最近的残影会遮挡视线。
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
    // 残影渲染最远距离 = 当前渲染距离（区块）× 16（与实体模型渲染距离一致，模型能渲染则残影也渲染），
    // 在 onRenderLevelStage 中按 mc.options.getEffectiveRenderDistance() 动态计算
    /**
     * 第一人称下残影渲染起始点与玩家当前位置的最小水平距离（格）。
     * 用空间距离而非年龄判定：疾跑急停后玩家站定、旧快照年龄继续增长，
     * 年龄条件会让急停前位于玩家近旁的快照"追上来"出现在眼前；距离条件则始终
     * 只渲染离玩家足够远的残影（残影轨迹从身后开始）。
     */
    private static final double FIRST_PERSON_MIN_DISTANCE_SQR = 1.5D * 1.5D;
    /**
     * 第一人称下残影整体沿快照朝向向后偏移的距离（格）。
     * 残影位置判定只针对快照位置（脚部中心），而模型（尤其龙模型）体长数格、
     * 头部前伸会穿过玩家位置伸入眼前视野 —— 整体向后偏移使模型前缘不越过玩家，
     * 直线疾跑时残影也不再"闪现"在眼前。
     */
    private static final double FIRST_PERSON_POSE_OFFSET = 2.0D;

    /**
     * 当前残影透明度（0~1），-1 表示非残影渲染 —— 供 {@link LivingEntityAfterimageMixin} 读取。
     * 仅在渲染线程内、于残影渲染前后严格配对赋值/复位，无并发问题。
     */
    public static float ghostAlpha = -1F;

    /** 客户端 tick 计数器（残影年龄的基准） */
    private static int tickCounter = 0;

    /**
     * 单个残影快照：位置 + 头朝向（视向）+ 身体朝向 + 俯仰 + 出生 tick + 冻结的龙骨骼姿态。
     * 头/身朝向分离记录，保证残影的头部与身体姿态与玩家在该时刻的实际姿态一致；
     * pose 为该快照诞生后第一帧渲染时捕获的龙模型骨骼姿态（null 表示尚未捕获/非龙玩家）。
     */
    private record GhostSnapshot(double x, double y, double z, float headYaw, float bodyYaw, float pitch, int bornTick, DragonPose pose) {
        @Contract("_ -> new")
        @NotNull GhostSnapshot withPose(DragonPose pose) {
            return new GhostSnapshot(x, y, z, headYaw, bodyYaw, pitch, bornTick, pose);
        }
    }

    /**
     * 龙模型骨骼姿态快照：按捕获时的骨骼遍历顺序存储全部骨骼的旋转/位移/缩放。
     * 渲染残影前写入对应骨骼（把残影冻结在快照时刻的动画姿势），渲染后恢复当前姿态。
     * GeoBone 引用是模型缓存的稳定实例，捕获与回写使用同一遍历顺序。
     */
    private static final class DragonPose {
        private final GeoBone[] bones;
        private final float[] rotX;
        private final float[] rotY;
        private final float[] rotZ;
        private final float[] posX;
        private final float[] posY;
        private final float[] posZ;
        private final float[] scaleX;
        private final float[] scaleY;
        private final float[] scaleZ;

        DragonPose(@NotNull BakedGeoModel model) {
            List<GeoBone> collected = new ArrayList<>();
            collect(model.topLevelBones(), collected);
            this.bones = collected.toArray(new GeoBone[0]);
            int n = this.bones.length;
            this.rotX = new float[n];
            this.rotY = new float[n];
            this.rotZ = new float[n];
            this.posX = new float[n];
            this.posY = new float[n];
            this.posZ = new float[n];
            this.scaleX = new float[n];
            this.scaleY = new float[n];
            this.scaleZ = new float[n];
            for (int i = 0; i < n; i++) {
                GeoBone bone = this.bones[i];
                this.rotX[i] = bone.getRotX();
                this.rotY[i] = bone.getRotY();
                this.rotZ[i] = bone.getRotZ();
                this.posX[i] = bone.getPosX();
                this.posY[i] = bone.getPosY();
                this.posZ[i] = bone.getPosZ();
                this.scaleX[i] = bone.getScaleX();
                this.scaleY[i] = bone.getScaleY();
                this.scaleZ[i] = bone.getScaleZ();
            }
        }

        /** 将该姿态快照写回骨骼树 */
        void apply() {
            for (int i = 0; i < this.bones.length; i++) {
                GeoBone bone = this.bones[i];
                bone.setRotX(this.rotX[i]);
                bone.setRotY(this.rotY[i]);
                bone.setRotZ(this.rotZ[i]);
                bone.setPosX(this.posX[i]);
                bone.setPosY(this.posY[i]);
                bone.setPosZ(this.posZ[i]);
                bone.setScaleX(this.scaleX[i]);
                bone.setScaleY(this.scaleY[i]);
                bone.setScaleZ(this.scaleZ[i]);
            }
        }

        private static void collect(@NotNull List<GeoBone> bones, List<GeoBone> out) {
            for (GeoBone bone : bones) {
                out.add(bone);
                collect(bone.getChildBones(), out);
            }
        }
    }

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
        // 用位置增量判定移动：远程玩家的 deltaMovement 依赖服务端移动包同步、在客户端不可靠，
        // 而位置（xo/x）对远程玩家由位置包每 tick 同步、对本地玩家由本地运动计算，两者均可靠
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

        // 已有轨迹：快照按年龄自然老化（无资格捕获时同样老化，残影淡出）；捕获 tick 追加新快照
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
        // 龙玩家的身体朝向由 DS 的 MovementData.bodyYaw 决定，普通玩家由 yBodyRot 决定
        float bodyYaw = DragonStateProvider.isDragon(player)
                ? (float) MovementData.getData(player).bodyYaw
                : player.yBodyRot;
        return new GhostSnapshot(pos.x, pos.y, pos.z, player.getYRot(), bodyYaw, player.getXRot(), tickCounter, null);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(final @NotNull RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || TRAILS.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();
        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        float partialTick = mc.getTimer().getGameTimeDeltaPartialTick(false);
        // 残影渲染距离 = 当前渲染距离（区块）× 16 格，与实体模型渲染距离一致
        double renderDistance = mc.options.getEffectiveRenderDistance() * 16.0;
        double renderDistanceSqr = renderDistance * renderDistance;
        // 第一人称下本地玩家的残影：由客户端配置控制（配置关闭则完全不渲染）
        boolean firstPersonLocal = mc.options.getCameraType().isFirstPerson();
        boolean showFirstPerson = WKClientConfig.shouldShowAfterimagesInFirstPerson();

        for (Map.Entry<UUID, AfterimageTrail> entry : TRAILS.entrySet()) {
            Player player = mc.level.getPlayerByUUID(entry.getKey());
            AfterimageTrail trail = entry.getValue();
            if (player == null || !player.isAlive()) {
                continue;
            }
            if (player.distanceToSqr(camera) > renderDistanceSqr) {
                continue;
            }
            boolean isDragon = DragonStateProvider.isDragon(player);

            // 龙分支：捕获当前骨骼姿态（每帧一次），并为最新快照补上它诞生时刻的姿态，
            // 使残影冻结在那一刻的动画姿势（普通玩家分支的 ModelPart 姿态会被 setupAnim 重算，不适用）
            DragonPose currentPose = isDragon ? captureCurrentDragonPose(mc, player) : null;
            if (currentPose != null) {
                GhostSnapshot newest = trail.snapshots.peekLast();
                if (newest != null && newest.pose() == null) {
                    trail.snapshots.removeLast();
                    trail.snapshots.addLast(newest.withPose(currentPose));
                }
            }

            // 从最旧到最新渲染；最新残影最后绘制（最不透明，覆盖在旧的之上）
            for (GhostSnapshot snapshot : trail.snapshots) {
                int age = tickCounter - snapshot.bornTick();
                if (age < 0) {
                    continue;
                }
                // 第一人称下本地玩家的残影：配置关闭则跳过；配置开启时与玩家当前位置的
                // 水平距离不足时不渲染（相机位于模型头部，近处的残影会遮挡视线；
                // 距离条件用空间而非年龄判定，急停后近处的旧快照不会出现在眼前）
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
                    renderGhost(player, snapshot, alpha, isDragon, partialTick, poseStack, bufferSource, camera, currentPose, offsetPose);
                }
            }
        }
    }

    private static void renderGhost(@NotNull Player player, @NotNull GhostSnapshot snapshot, float alpha, boolean isDragon,
                                    float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, @NotNull Vec3 camera,
                                    DragonPose currentPose, boolean offsetPose) {
        Minecraft mc = Minecraft.getInstance();
        poseStack.pushPose();
        // 第一人称本地玩家：残影整体沿快照朝向向后偏移（模型前缘不越过玩家，避免遮挡视线）
        double offsetX = 0;
        double offsetZ = 0;
        if (offsetPose) {
            float yawRad = snapshot.bodyYaw() * Mth.DEG_TO_RAD;
            offsetX = Mth.sin(yawRad) * FIRST_PERSON_POSE_OFFSET;
            offsetZ = -Mth.cos(yawRad) * FIRST_PERSON_POSE_OFFSET;
        }
        poseStack.translate(snapshot.x() - camera.x() + offsetX, snapshot.y() - camera.y(), snapshot.z() - camera.z() + offsetZ);

        // 残影位置的方块/天空光照
        BlockPos blockPos = BlockPos.containing(snapshot.x(), snapshot.y(), snapshot.z());
        int light = LightTexture.pack(
                player.level().getBrightness(LightLayer.BLOCK, blockPos),
                player.level().getBrightness(LightLayer.SKY, blockPos));

        try {
            ghostAlpha = alpha;
            if (isDragon) {
                renderDragonGhost(mc, player, snapshot, partialTick, poseStack, bufferSource, light, currentPose);
            } else {
                renderPlayerGhost(mc, player, snapshot, partialTick, poseStack, bufferSource, light);
            }
        } finally {
            ghostAlpha = -1F;
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

    /** 捕获龙模型当前骨骼姿态（渲染循环开始时的姿态，作为残影写回后的恢复基准） */
    private static @Nullable DragonPose captureCurrentDragonPose(Minecraft mc, Player player) {
        DragonEntity dragon = getOrCreateDragonEntity(player);
        if (dragon == null) {
            return null;
        }
        EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(dragon);
        if (!(renderer instanceof DragonRenderer dragonRenderer)) {
            return null;
        }
        BakedGeoModel model = dragonRenderer.getGeoModel()
                .getBakedModel(dragonRenderer.getGeoModel().getModelResource(dragon, dragonRenderer));
        return new DragonPose(model);
    }

    /** 普通玩家残影：原版管线，透明度由 {@link LivingEntityAfterimageMixin} 处理 */
    private static void renderPlayerGhost(@NotNull Minecraft mc, Player player, GhostSnapshot snapshot, float partialTick,
                                          PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
        if (!(renderer instanceof PlayerRenderer playerRenderer) || !(player instanceof AbstractClientPlayer clientPlayer)) {
            return;
        }
        // 残影渲染的旋转取自实体自身字段：临时覆写为快照朝向（身体朝身体朝向、头部朝头朝向），渲染后恢复
        float bodyYaw = snapshot.bodyYaw();
        float headYaw = snapshot.headYaw();
        float pitch = snapshot.pitch();
        float oldBodyYaw = player.yBodyRot;
        float oldBodyYawO = player.yBodyRotO;
        float oldHeadYaw = player.yHeadRot;
        float oldHeadYawO = player.yHeadRotO;
        float oldXRot = player.getXRot();
        float oldXRotO = player.xRotO;
        try {
            player.yBodyRot = player.yBodyRotO = bodyYaw;
            player.yHeadRot = player.yHeadRotO = headYaw;
            player.setXRot(pitch);
            player.xRotO = pitch;
            playerRenderer.render(clientPlayer, headYaw, partialTick, poseStack, bufferSource, light);
        } finally {
            player.yBodyRot = oldBodyYaw;
            player.yBodyRotO = oldBodyYawO;
            player.yHeadRot = oldHeadYaw;
            player.yHeadRotO = oldHeadYawO;
            player.setXRot(oldXRot);
            player.xRotO = oldXRotO;
        }
    }

    /** 龙玩家残影：GeckoLib 管线，以 ARGB 颜色传入 alpha（参考 DragonGlowLayerRenderer 模式） */
    private static void renderDragonGhost(Minecraft mc, Player player, GhostSnapshot snapshot, float partialTick,
                                          PoseStack poseStack, MultiBufferSource bufferSource, int light,
                                          DragonPose currentPose) {
        DragonEntity dragon = getOrCreateDragonEntity(player);
        if (dragon == null) {
            return;
        }
        EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(dragon);
        if (!(renderer instanceof DragonRenderer dragonRenderer)) {
            return;
        }

        // 残影身体朝向 = 快照时的 DS 身体朝向（DragonRenderer.setupRender 读取该值），渲染后恢复
        MovementData movement = MovementData.getData(player);
        double oldBodyYaw = movement.bodyYaw;
        movement.bodyYaw = snapshot.bodyYaw();

        // 冻结姿势：把该快照诞生时刻捕获的骨骼姿态写回骨骼树；尚未捕获时用当前姿态
        DragonPose pose = snapshot.pose() != null ? snapshot.pose() : currentPose;
        if (pose != null) {
            pose.apply();
        }

        // GeckoLib 骨骼管线（renderRecursively → getRenderOffset）依赖渲染器内部 this.animatable，
        // 它由 render() 设置并在 doPostRenderCleanup() 中清空 —— 直接调 actuallyRender 必须手动补上。
        ((GeoEntityRendererAccessor) dragonRenderer).wingKirin$setAnimatable(dragon);
        try {
            ResourceLocation texture = dragonRenderer.getTextureLocation(dragon);
            RenderType renderType = RenderType.entityTranslucent(texture);
            BakedGeoModel model = dragonRenderer.getGeoModel()
                    .getBakedModel(dragonRenderer.getGeoModel().getModelResource(dragon, dragonRenderer));
            int colour = 0xFFFFFF | (Math.round(ghostAlpha * 255.0F) << 24);
            // isReRender=true：骨骼使用写回的快照姿态渲染（isReRender 下 renderRecursively 不重算
            // 骨骼），不触发 GeckoLib 动画控制器状态机（额外驱动会让控制器停在
            // TRANSITIONING/STOPPED，骨骼冻结在绑定姿势即 T 姿势），只重绘本体模型并应用半透明颜色
            dragonRenderer.actuallyRender(poseStack, dragon, model, renderType, bufferSource,
                    bufferSource.getBuffer(renderType), true, partialTick, light, OverlayTexture.NO_OVERLAY, colour);
        } finally {
            // 恢复为渲染循环开始时的姿态，避免影响本帧后续残影与下一帧真实渲染
            if (pose != null && currentPose != null && pose != currentPose) {
                currentPose.apply();
            }
            movement.bodyYaw = oldBodyYaw;
            dragonRenderer.doPostRenderCleanup();
        }
    }
}
