package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.render.entity.dragon.DragonRenderer;
import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import by.timeslowly.wing_kirin.client.eventhandler.AfterimageRenderHandler;
import com.geckolib.animation.state.BoneSnapshot;
import com.geckolib.cache.model.GeoBone;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

/**
 * 龙玩家残影的渲染通道适配：
 * <p>
 * 1. 残影激活时把渲染类型替换为半透明混合管线（cutout 的 alpha 测试不支持半透明）；
 * 2. 残影通道的骨骼姿态冻结：动画控制器状态机按实例共享、无法回退，状态级时间冻结无效，
 *    因此在本体龙的真实通道里以 BoneUpdater 捕获每帧最终骨骼姿态（{@link AfterimageRenderHandler#lastDragonPoses}），
 *    残影通道再以 BoneUpdater 把快照姿态写回 BoneSnapshot；
 * 3. 第一人称下 DS 会隐藏本体龙的颈部/头部（避免遮挡相机），残影通道追加反隐藏。
 * <p>
 * 注意：不能覆写 preRenderPass / getRenderType——mixin 中的 super 指向 GeoEntityRenderer，
 * 会顶掉 DS 原方法（第一人称藏颈/藏翼/呼吸骨监听/猎人透明分支）。
 */
@Mixin(value = DragonRenderer.class, remap = false)
public abstract class DragonRendererGhostMixin<R extends LivingEntityRenderState & com.geckolib.renderer.base.GeoRenderState>
        extends GeoEntityRenderer<DragonEntity, R> {

    protected DragonRendererGhostMixin(final EntityRendererProvider.Context context, final GeoModel<DragonEntity> model) {
        super(context, model);
    }

    @Inject(method = "getRenderType", at = @At("RETURN"), cancellable = true, remap = false)
    private void wingKirin$ghostRenderType(LivingEntityRenderState renderState, Identifier texture, CallbackInfoReturnable<RenderType> cir) {
        if (AfterimageRenderHandler.ghostAlpha >= 0) {
            cir.setReturnValue(RenderTypes.itemTranslucent(texture));
        }
    }

    @Inject(method = "preRenderPass", at = @At("TAIL"), remap = false)
    private void wingKirin$ghostBoneHandling(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks, CallbackInfo ci) {
        DragonRenderer.DragonRenderData renderData =
                renderPassInfo.getGeckolibData(DragonRenderer.DRAGON_RENDER_DATA);

        if (AfterimageRenderHandler.ghostAlpha >= 0) {
            // —— 残影通道 ——
            // 捕获到局部变量：更新器在提交阶段之后执行时静态字段可能已复位
            Map<String, AfterimageRenderHandler.GhostBonePose> frozenPoses = AfterimageRenderHandler.ghostPoses;
            if (frozenPoses != null) {
                // 更新器按注册顺序执行：本更新器（TAIL 追加）在动画应用之后运行，最终覆写全部骨骼姿态
                renderPassInfo.addBoneUpdater((info, snapshots) -> {
                    frozenPoses.forEach((name, pose) ->
                            snapshots.get(name).ifPresent(pose::applyTo));
                    // 第一人称反隐藏：覆盖 DS 设置的颈部/头部隐藏标记
                    snapshots.get("Neck").ifPresent(bone -> {
                        bone.skipRender(false);
                        bone.skipChildrenRender(false);
                    });
                });
            }
        } else if (renderData != null && !renderData.inUI()) {
            // —— 本体龙通道 ——
            // 每帧捕获最终骨骼姿态（动画应用之后），供快照/残影冻结使用。
            // BoneSnapshots 无批量接口，按 DS 同款模式经 boneLookup 枚举骨骼名
            long cacheId = renderData.renderCacheId();
            renderPassInfo.addBoneUpdater((info, snapshots) -> {
                @SuppressWarnings("unchecked")
                Map<String, GeoBone> bones = (Map<String, GeoBone>) (Map) info.model().boneLookup().get();
                Map<String, AfterimageRenderHandler.GhostBonePose> poses = new HashMap<>(bones.size());
                bones.keySet().forEach(name ->
                        snapshots.get(name).ifPresent(snapshot ->
                                poses.put(name, AfterimageRenderHandler.GhostBonePose.from(snapshot))));
                AfterimageRenderHandler.lastDragonPoses.put(cacheId, poses);
            });
        }
    }
}
