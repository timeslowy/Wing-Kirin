package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.MaceCrush_Effect;
import by.timeslowly.wing_kirin.common.item.GoldenBell;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

import static by.timeslowly.wing_kirin.registry.WKEffects.MaceCrush;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEventHandler {
    // 金钟损害机制
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.@NotNull Post event) {
        DamageSource damageSource = event.getSource();
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
     * 2. 攻击者「从天而降」药水效果（下落猛击时根据效果倍率增伤）
     * 3.持有金钟对「龙吼功」伤害加成
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
            // 剩余时长 > 50秒 (1000 ticks) 或无限时长，则被定身实体所受伤害乘1.5倍
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }

        // ----- 2. 攻击者相关效果 -----
        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        // 2.1 「聚音激能」相关（原来是一个被动）
        if (attackerEntity instanceof LivingEntity attacker) {
            if (source.is(DamageTypes.SONIC_BOOM)) {
                ItemStack mainHand = attacker.getMainHandItem();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof GoldenBell) {
                    multiplier *= 2.0f;  // 伤害翻倍
                }
            }
            // 2.2 从天而降药水效果
            ItemStack weapon = attacker.getWeaponItem();
            if (weapon.getItem() instanceof MaceItem) {
                // 检查是否下落猛击
                if (attacker.fallDistance > 1.5f && !attacker.isFallFlying()) {
                    float maceMultiplier = MaceCrush_Effect.getMultiplier(attacker, MaceCrush);
                    if (maceMultiplier != 1.0f) {
                        multiplier *= maceMultiplier;
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
