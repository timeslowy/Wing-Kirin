package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

public class UnstoppableSpeedEffect extends MobEffect {
    /**
     * 修改受击冷却的逻辑见： {@link by.timeslowly.wing_kirin.mixins.LivingEntityHurtMixin}
     */
    public UnstoppableSpeedEffect(MobEffectCategory category, int color) {

        super(category, color);
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_1"), 0.8,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_2"), 0.8,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    // 效果结束导致虚弱与缓慢，根据效果等级应用。
    // 注意：26.1 起不可在 MobEffectEvent.Remove/Expired 回调内同步调用（会修改正在迭代的
    // 活跃效果表导致 ConcurrentModificationException），由 WKEffects 延迟到 tick 末尾统一调用。
    public static void onEffectExpired(@NotNull LivingEntity entity, int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, amplifier, false, true, true));
        // 26.1 起 MOVEMENT_SLOWDOWN 更名为 SLOWNESS
        entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, amplifier, false, true, true));
    }

    // 「不可被常规手段治愈」：旧版通过 NeoForge EffectCure（fillEffectCures 清空）实现，
    // 26.1 该机制被整体移除后改由治愈物品的 ConsumeEffect Mixin 拦截实现，
    // 判定逻辑见 {@link by.timeslowly.wing_kirin.registry.WKEffects#isCureImmune}。
}