package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 「唯快不破」（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * 1.20.1 适配：
 * <ul>
 *   <li>addAttributeModifier 在 1.20.1 接收 String 且<b>要求为 UUID 字符串</b>
 *       （内部 UUID.fromString，1.20.5+ 才改为 ResourceLocation 命名 ID），操作数枚举名为
 *       MULTIPLY_TOTAL（1.21.1 的 ADD_MULTIPLIED_TOTAL）。此处以 1.21.1 的同一 RL 字符串
 *       经 nameUUIDFromBytes 派生固定 UUID——同一 ID 字符串两分支派生出相同 UUID，
 *       属性修饰符存档跨版本兼容；</li>
 *   <li>1.20.1 无 NeoForge 的 EffectCure/fillEffectCures 体系，「无法被常规手段消去」改由
 *       {@link by.timeslowly.wing_kirin.registry.WKEffects} 的 MobEffectEvent.Added 订阅在施加时
 *       清空效果实例的 curative items 实现（与 magic_disabled.incurable 已移植的模式一致，
 *       只影响牛奶/蜂蜜等治愈路径，命令、模组移除与自然到期不受影响）。</li>
 * </ul>
 * <p>
 * 修改受击冷却/护甲/附魔穿透的逻辑见： {@link by.timeslowly.wing_kirin.mixin.LivingEntityHurtMixin}
 */
public class UnstoppableSpeedEffect extends MobEffect {

    public UnstoppableSpeedEffect(MobEffectCategory category, int color) {
        super(category, color);
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                uuidFromId("effect.unstoppable_speed_1"), 0.8,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                uuidFromId("effect.unstoppable_speed_2"), 0.8,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    /** 以 1.21.1 的修饰符 RL（wing_kirin:<id>）派生固定 UUID，两分支同串同 UUID */
    private static String uuidFromId(@NotNull String id) {
        return UUID.nameUUIDFromBytes((WingKirin.MODID + ":" + id).getBytes()).toString();
    }

    // 效果结束导致虚弱与缓慢，根据效果等级应用
    public static void onEffectExpired(@NotNull LivingEntity entity, int amplifier) {
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, amplifier, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, amplifier, false, true, true));
    }
}
