package by.timeslowly.wing_kirin.mixin;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityHurtMixin {

    /**
     * 1.20.1 无 DamageTypeTags.IS_PLAYER_ATTACK（1.20.5+ 才加入原版），
     * 以自定义标签 wing_kirin:player_attack 等价替代（内容 = minecraft:player_attack，
     * 即原版玩家近战攻击的 DamageType 注册表 ID，与 1.21.1 原版 is_player_attack 标签语义一致），
     * 数据包文件见 data/wing_kirin/tags/damage_type/player_attack.json。
     * 注意：DamageType 的 msgId（"player"）与注册表 ID（"player_attack"）不是一回事。
     */
    @Unique
    private static final TagKey<DamageType> WINGKIRIN$PLAYER_ATTACK = TagKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(WingKirin.MODID, "player_attack"));

    /**
     * 拦截受击无敌判定中对伤害来源标签的检查。
     * 若攻击者拥有 唯快不破 效果，则根据配置决定是否无视受击冷却。
     * 使用 @WrapOperation 替代 @Redirect，避免与其他模组变更同一
     * INVOKE 目标产生冲突——@WrapOperation 允许共存（1.20.1 的 hurt 内有 8 处
     * DamageSource#is(TagKey) 调用点，WrapOperation 会全部包装，运行时按标签过滤，
     * 与 1.21.1 版行为一致）。
     */
    @WrapOperation(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean wingkirin$redirectBypassCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_COOLDOWN) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED.get())) {
                    if (ConfigSyncHandler.unstoppableSpeedAllDamageTypes()) {
                        return true;
                    }
                    if (source.is(WINGKIRIN$PLAYER_ATTACK)) {
                        return true;
                    }
                }
            }
        }
        return original.call(source, tagKey);
    }

    /**
     * 拦截护甲减免中对伤害来源标签的检查。
     * 若攻击者拥有 唯快不破 效果，则直接返回 true 强制穿透所有附魔。
     * Hook 的是 getDamageAfterMagicAbsorb（内含 3 处 is(TagKey) 调用点，同样全包装按标签过滤）。
     */
    @WrapOperation(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean wingkirin$bypassEnchantmentsCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_ENCHANTMENTS) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED.get())) {
                    return true;
                }
            }
        }
        return original.call(source, tagKey);
    }

    /**
     * 拦截护甲计算中对伤害来源标签的检查。
     * 若攻击者拥有 唯快不破 效果且配置开启，则返回 true 穿透护甲减免。
     * Hook 的是 getDamageAfterArmorAbsorb（内仅 1 处 is(TagKey) 调用点）。
     */
    @WrapOperation(
            method = "getDamageAfterArmorAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"
            )
    )
    private boolean wingkirin$bypassArmorCheck(DamageSource source, TagKey<DamageType> tagKey, Operation<Boolean> original) {
        if (tagKey == DamageTypeTags.BYPASSES_ARMOR) {
            if (ConfigSyncHandler.unstoppableSpeedBypassArmor()) {
                if (source.getEntity() instanceof LivingEntity attacker) {
                    if (attacker.hasEffect(WKEffects.UNSTOPPABLE_SPEED.get())) {
                        return true;
                    }
                }
            }
        }
        return original.call(source, tagKey);
    }
}
