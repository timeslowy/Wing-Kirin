package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.client.eventhandler.AfterimageRenderHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 残影半透明渲染 —— 26.1 版（镜像旧版设计）：
 * <p>
 * 当 {@link AfterimageRenderHandler#ghostAlpha} 激活（>= 0，即正在提交残影副本）时：
 * <ul>
 *   <li>强制 {@code getRenderType} 的 forceTransparent 参数为 true，使残影走半透明
 *       渲染管线（否则 cutout 渲染类型的 alpha test 不会产生平滑淡出）；</li>
 *   <li>把 {@code submitModel} 的顶点颜色 tintedColor 替换为当前残影透明度
 *       （原版 forceTransparent 使用固定 0.15 透明度，不满足逐残影衰减）。</li>
 * </ul>
 * 注入目标均为 26.1 提交式管线中 {@code LivingEntityRenderer.submit} 内的调用，
 * 与 DS 26.1.2 的 HunterHandler 式渲染不冲突（@ModifyArg 可共存）。
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityAfterimageMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
        extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    protected LivingEntityAfterimageMixin(final EntityRendererProvider.Context context) {
        super(context);
    }

    @ModifyArg(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/rendertype/RenderType;"
            ),
            index = 2
    )
    private boolean wingKirin$forceTransparent(boolean forceTransparent) {
        return AfterimageRenderHandler.ghostAlpha >= 0 || forceTransparent;
    }

    @ModifyArg(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    // 经 26.1.2.78/.88 patched jar 字节码核实：submitModel 声明在 SubmitNodeCollector
                    // 接口上（接口方法级类型参数 S 无上界，擦除后为 Ljava/lang/Object;）
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            ),
            index = 6
    )
    private int wingKirin$ghostTint(int tintedColor) {
        float alpha = AfterimageRenderHandler.ghostAlpha;
        if (alpha >= 0) {
            return 0xFFFFFF | (Math.round(alpha * 255.0F) << 24);
        }
        return tintedColor;
    }
}
