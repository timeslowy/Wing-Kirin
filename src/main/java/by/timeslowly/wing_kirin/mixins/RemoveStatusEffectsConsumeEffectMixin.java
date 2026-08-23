package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 治愈免疫机制（26.1 版）的另一半：数据包可声明的定向移除效果（如「奶露之合」的
 * RemoveStatusEffectsConsumeEffect）经由此类逐个调用 removeEffect 实现。
 * <p>
 * 请求移除的效果中含治愈免疫效果（{@link WKEffects#isCureImmune}）时，改为逐个移除
 * 其余被请求的效果并跳过原始调用；未涉及免疫效果时走原版逻辑。
 */
@Mixin(RemoveStatusEffectsConsumeEffect.class)
public abstract class RemoveStatusEffectsConsumeEffectMixin {

    @Shadow
    @Final
    private HolderSet<MobEffect> effects;

    @Inject(
            method = "apply(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void wing_kirin$keepCureImmuneEffects(final Level level, final ItemStack stack, final LivingEntity entity,
                                                  final CallbackInfoReturnable<Boolean> cir) {
        boolean immuneEffectRequested = false;
        for (Holder<MobEffect> effect : this.effects) {
            if (WKEffects.isCureImmune(effect)) {
                immuneEffectRequested = true;
                break;
            }
        }

        if (!immuneEffectRequested) {
            return;
        }

        // 与原版一致逐个移除，仅跳过免疫效果（removeEffect 对不存在的效果返回 false，语义不变）
        boolean anyRemoved = false;
        for (Holder<MobEffect> effect : this.effects) {
            if (WKEffects.isCureImmune(effect)) {
                continue;
            }
            anyRemoved |= entity.removeEffect(effect);
        }
        cir.setReturnValue(anyRemoved);
    }
}
