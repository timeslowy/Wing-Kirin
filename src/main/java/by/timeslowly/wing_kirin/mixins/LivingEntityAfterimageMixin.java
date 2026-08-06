package by.timeslowly.wing_kirin.mixins;

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
 * 残影半透明渲染 —— 镜像 Dragon Survival 的 {@code LivingEntityRendererMixin}（HunterHandler 模式）：
 * <p>
 * 当 {@link AfterimageRenderHandler#ghostAlpha} 激活（>= 0，即正在渲染残影副本）时：
 * <ul>
 *   <li>强制 {@code getRenderType} 的 translucent 参数为 true，使残影走半透明渲染管线 ——
 *       否则 cutout 渲染类型的 alpha test 不会产生平滑淡出（alpha < 1/255 直接整体剔除）；</li>
 *   <li>把 {@code model.renderToBuffer} 的顶点颜色 alpha 替换为当前残影透明度。</li>
 * </ul>
 * 注入点与 DS 1.21.1 版 mixin 完全一致，已在该版本验证可用。
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"),
            index = 4)
    private int wingKirin$modifyAlpha(int color) {
        float alpha = AfterimageRenderHandler.ghostAlpha;
        if (alpha >= 0) {
            return 0xFFFFFF | (Math.round(alpha * 255.0F) << 24);
        }
        return color;
    }
}
