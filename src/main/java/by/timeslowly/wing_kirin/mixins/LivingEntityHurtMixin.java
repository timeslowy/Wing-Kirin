package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {

    /**
     * 拦截无敌判定中对伤害来源 bypasses_cooldown 标签的检查。
     * 若攻击者拥有 唯快不破 效果，则强制返回 true，从而无视受击冷却。
     */

    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean redirectBypassCheck(DamageSource source, TagKey<DamageType> tagKey) {
        // 当检查的是 BYPASSES_COOLDOWN 标签，且攻击者拥有「唯快不破」效果时，返回 true
        if (tagKey == DamageTypeTags.BYPASSES_COOLDOWN) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                    return true;
                }
            }
        }
        // 其他情况保持原版逻辑
        return source.is(tagKey);
    }

    /**
     * 拦截护甲减免中对伤害来源 bypasses_enchantments 标签的检查。
     * 若攻击者拥有 唯快不破 效果，则强制返回 true，从而使攻击穿透附魔。
     */
    @Redirect(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean redirectArmorBypassCheck(DamageSource source, TagKey<DamageType> tagKey) {
        if (tagKey == DamageTypeTags.BYPASSES_ENCHANTMENTS) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                    return true;
                }
            }
        }
        return source.is(tagKey);
    }
}