package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * 治愈免疫机制（26.1 版）：牛奶、蜂蜜瓶等清空全部效果的治愈物品
 * 经由 {@link ClearAllStatusEffectsConsumeEffect#apply} → removeAllEffects() 实现。
 * <p>
 * 存在治愈免疫效果（{@link WKEffects#isCureImmune}）时，改为逐个移除其余效果并跳过原始调用，
 * 免疫效果得以保留——等价于旧版 fillEffectCures 清空的语义；无免疫效果时走原版逻辑。
 */
@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public abstract class ClearAllStatusEffectsConsumeEffectMixin {

    @Inject(
            method = "apply(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wing_kirin$keepCureImmuneEffects(final Level level, final ItemStack stack, final @NonNull LivingEntity entity,
                                                  final CallbackInfoReturnable<Boolean> cir) {
        // 客户端侧与原版一致直接返回 false（removeAllEffects 内部同样有该守卫），不拦截
        if (entity.level().isClientSide()) {
            return;
        }

        boolean hasImmuneEffect = false;
        for (Holder<MobEffect> effect : entity.getActiveEffectsMap().keySet()) {
            if (WKEffects.isCureImmune(effect)) {
                hasImmuneEffect = true;
                break;
            }
        }

        if (!hasImmuneEffect) {
            return;
        }

        // 仅移除非免疫效果；逐个 removeEffect 保持原版的事件触发行为（收尾结算已由 WKEffects 延迟处理）
        boolean anyRemoved = false;
        for (Holder<MobEffect> effect : List.copyOf(entity.getActiveEffectsMap().keySet())) {
            if (WKEffects.isCureImmune(effect)) {
                continue;
            }
            anyRemoved |= entity.removeEffect(effect);
        }
        cir.setReturnValue(anyRemoved);
    }
}
