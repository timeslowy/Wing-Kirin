package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.config.WKServerConfig;
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

    // 本 Mixin 包含两个 @Redirect，均 Hook DamageSource.is(TagKey)，
    // 但分别作用于不同的调用方法（method = "hurt" 与 "getDamageAfterMagicAbsorb"），
    // 因此不会相互干扰。两个 Redirect 的 fallthrough 均为 source.is(tagKey)，
    // 仅在各自的调用上下文中按条件介入。

    /**
     * 拦截无敌判定中对伤害来源 bypasses_cooldown 标签的检查。
     * 若攻击者拥有 唯快不破 效果，则根据配置决定是否无视受击冷却：
     * - 配置开启时：所有伤害类型均可无视受击冷却
     * - 配置关闭时：仅近战攻击（is_player_attack）可无视受击冷却
     */
    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean redirectBypassCheck(DamageSource source, TagKey<DamageType> tagKey) {
        if (tagKey == DamageTypeTags.BYPASSES_COOLDOWN) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED)) {
                    if (WKServerConfig.shouldUnstoppableSpeedApplyToAllDamageTypes()) {
                        return true;
                    }
                    // 配置关闭时，仅近战攻击（is_player_attack）可无视受击冷却
                    if (source.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
                        return true;
                    }
                }
            }
        }
        return source.is(tagKey);
    }

    /**
     * 拦截护甲减免中对伤害来源 bypasses_enchantments 标签的检查。
     * <p>
     * 在 {@code LivingEntity.getDamageAfterMagicAbsorb()} 中，原版通过此标签
     * 判定伤害是否穿透附魔保护。若攻击者拥有 唯快不破 效果，则直接返回 true，
     * 强制该次伤害穿透所有附魔（包括保护、弹射物保护等）。
     * </p>
     * <p>
     * 与 {@link #redirectBypassCheck} 的区别：本方法 Hook 的是护甲计算阶段
     * （getDamageAfterMagicAbsorb），而非受击判定阶段（hurt），互不干扰。
     * </p>
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