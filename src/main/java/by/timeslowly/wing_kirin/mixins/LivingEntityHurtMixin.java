package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {

    /**
     * 拦截无敌判定中对伤害来源 bypasses_cooldown 标签的检查。
     * 若攻击者拥有 唯快不破 效果，则根据配置决定是否无视受击冷却。
     * 使用 @WrapOperation 替代 @Redirect，避免与其他模组（如 c6c）变更同一
     * INVOKE 目标产生冲突——@WrapOperation 允许共存。
     */
    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean redirectBypassCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_COOLDOWN) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                    if (WKServerConfig.shouldUnstoppableSpeedApplyToAllDamageTypes()) {
                        return true;
                    }
                    if (source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                        return true;
                    }
                }
            }
        }
        return original.call(source, tagKey);
    }

    /**
     * 拦截护甲减免中对伤害来源 bypasses_enchantments 标签的检查。
     * 若攻击者拥有 唯快不破 效果，则直接返回 true 强制穿透所有附魔。
     * Hook 的是 getDamageAfterMagicAbsorb。
     */
    @WrapOperation(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean bypassEnchantmentsCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_ENCHANTMENTS) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                    return true;
                }
            }
        }
        return original.call(source, tagKey);
    }

    /**
     * 拦截护甲计算中对伤害来源 bypasses_armor 标签的检查。
     * 若攻击者拥有 唯快不破 效果且配置开启，则返回 true 穿透护甲减免。
     * Hook 的是 getDamageAfterArmorAbsorb。
     */
    @WrapOperation(
            method = "getDamageAfterArmorAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean bypassArmorCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_ARMOR) {
            if (WKServerConfig.shouldUnstoppableSpeedBypassArmor()) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                        return true;
                    }
                }
            }
        }
        return original.call(source, tagKey);
    }
}