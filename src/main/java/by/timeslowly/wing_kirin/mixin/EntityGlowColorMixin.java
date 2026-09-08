package by.timeslowly.wing_kirin.mixin;

import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 定身金色描边（自 1.21.1 平移，getTeamColor 两版一致）。
 * 仅客户端生效：已登记于 mixins.json 的 client 数组
 * （Forge 惯例不在 Mixin 上用 @OnlyIn，改用 client 数组隔离）。
 */
@Mixin(Entity.class)
public abstract class EntityGlowColorMixin {

    // 定义你想要的目标颜色（0xFBDC92）
    @Unique
    private static final int WING_KIRIN_GLOW_COLOR = 16506002; // 金色

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof LivingEntity living) {
            if (living.hasEffect(WKEffects.DING_SHEN.get())) { // 如果具有定身效果
                cir.setReturnValue(WING_KIRIN_GLOW_COLOR); // 返回金色 0xFBDC92
            }
        }
    }
}
