package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.GoldenBell;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEventHandler {
    /**
     * 监听实体受伤事件，处理：
     * 1.使用「龙吼功」时金钟的双倍损害
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.@NotNull Post event) {
        DamageSource damageSource = event.getSource();
        // 金钟双倍损害机制
        if (damageSource.is(DamageTypes.SONIC_BOOM)) {
            // 获取攻击者
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
                // 调用GoldenBell的静态方法
                GoldenBell.onSonicBoomDamage(attacker);
            }
        }
    }

    /**
     * 监听伤害事件，处理：
     * 1. 受害者被定身超过50秒时的“脆骨”效果
     * 2. 攻击者「从天而降」药水效果（下落猛击时根据效果倍率增伤）-属性实现
     * 3.持有金钟对「龙吼功」伤害加成-属性实现
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;

        // ----- 1. 受害者定身效果 -----
        var DingShen = victim.getEffect(WKEffects.DingShen);
        if (DingShen != null) {
            int duration = DingShen.getDuration();
            // 剩余时长 > 50秒 (1000 ticks) 或无限时长，则被定身实体所受伤害乘1.5倍——"肌肉松弛"
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }

        // ----- 2. 攻击者相关效果 -----
        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        if (attackerEntity instanceof LivingEntity attacker) {
            // 2.1 属性——音波伤害倍率「聚音激能」相关（原来是一个被动）
            if (source.is(DamageTypes.SONIC_BOOM)) {
                // 应用音爆伤害倍率属性
                AttributeInstance sonicAttr = attacker.getAttribute(WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER);
                if (sonicAttr != null) {
                    multiplier *= (float) sonicAttr.getValue();
                }
            }
            // 2.2 属性——重锤伤害倍率（从天而降药水效果）
            ItemStack weapon = attacker.getWeaponItem();
            if (weapon.getItem() instanceof MaceItem) {
                // 检查是否下落猛击
                if (attacker.fallDistance > 1.5f && !attacker.isFallFlying()) {
                    AttributeInstance smashAttr = attacker.getAttribute(WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER);
                    if (smashAttr != null) {
                        multiplier *= (float) smashAttr.getValue();
                    }
                }
            }
        }

        // 应用最终倍率
        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }
    }

}
