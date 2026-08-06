package by.timeslowly.wing_kirin.mixins;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * 访问 GeckoLib {@link GeoEntityRenderer} 的受保护字段 {@code animatable}。
 * <p>
 * 残影渲染直接调用 {@code actuallyRender}（绕过了会设置并清空 {@code animatable} 的
 * {@code render()} 入口），而 GeckoLib 骨骼渲染管线（renderRecursively → getRenderOffset 等）
 * 依赖该字段 —— 不补上会 NPE（DragonRenderer.getModelOffset）。渲染结束后由
 * {@code doPostRenderCleanup()} 复位，与 {@code render()} 的正常收尾一致。
 */
@Mixin(value = GeoEntityRenderer.class, remap = false)
public interface GeoEntityRendererAccessor {
    @Accessor("animatable")
    void wingKirin$setAnimatable(Entity animatable);
}
