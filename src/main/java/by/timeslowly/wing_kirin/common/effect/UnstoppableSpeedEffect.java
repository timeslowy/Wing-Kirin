package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class UnstoppableSpeedEffect extends MobEffect {
    /**
     * 修改受击冷却的逻辑见： {@link by.timeslowly.wing_kirin.mixins.LivingEntityHurtMixin}
     */
    public UnstoppableSpeedEffect(MobEffectCategory category, int color) {

        super(category, color);
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_1"), 0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_2"), 0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    // 效果结束导致虚弱与缓慢，根据效果等级应用
    public static void onEffectExpired(@NotNull LivingEntity entity, int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, amplifier, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, amplifier, false, true, true));
    }

    // 使其无法被常规手段消去
    @Override
    public void fillEffectCures(final @NotNull Set<EffectCure> cures, @NotNull final MobEffectInstance effectInstance) {
        cures.clear();
    }

}