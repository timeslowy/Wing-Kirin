package by.timeslowly.wing_kirin.mixin;

import by.timeslowly.wing_kirin.client.eventhandler.AfterimageRenderHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 残影半透明渲染 —— 镜像 Dragon Survival 的 {@code LivingEntityRendererMixin}（HunterHandler 模式，
 * 自 1.21.1 NeoForge 分支移植）：
 * <p>
 * 当 {@link AfterimageRenderHandler#ghostAlpha} 激活（>= 0，即正在渲染残影副本）时：
 * <ul>
 *   <li>强制 {@code getRenderType} 的 translucent 参数为 true，使残影走半透明渲染管线 ——
 *       否则 cutout 渲染类型的 alpha test 不会产生平滑淡出（alpha < 1/255 直接整体剔除）；</li>
 *   <li>把 {@code model.renderToBuffer} 的顶点颜色 alpha 替换为当前残影透明度。</li>
 * </ul>
 * <p>
 * 1.20.1 适配：原版 1.20.1 的 {@code EntityModel.renderToBuffer} 为
 * {@code (PoseStack, VertexConsumer, int packedLight, int packedOverlay, float r, float g, float b, float a)}
 * 八参签名（1.21 起合并为 {@code (…, III)} 三 int 颜色），故 alpha 注入点为第 8 参（index=7 的 float），
 * 注入逻辑与 1.21.1 版语义一致。
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityAfterimageMixin<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    protected LivingEntityAfterimageMixin(final EntityRendererProvider.Context context) {
        super(context);
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/world/entity/LivingEntity;ZZZ)Lnet/minecraft/client/renderer/RenderType;"),
            index = 2)
    private boolean wingKirin$forceTranslucent(boolean isTranslucent) {
        return AfterimageRenderHandler.ghostAlpha >= 0 || isTranslucent;
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"),
            index = 7)
    private float wingKirin$modifyAlpha(float alpha) {
        float ghostAlpha = AfterimageRenderHandler.ghostAlpha;
        if (ghostAlpha >= 0) {
            return ghostAlpha;
        }
        return alpha;
    }
}
