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

import static by.timeslowly.wing_kirin.registry.WKEffects.MaceCrush;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEventHandler {
    // 金钟损害机制
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
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
     * 监听伤害事件，精确判断并增强重锤下落猛击的伤害，用于从天而降药水效果。
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();

        // 1. 攻击者必须是活着的实体
        if (!(attackerEntity instanceof LivingEntity attacker)) {
            return;
        }

        // 2. 检查攻击者是否持有重锤 (使用更通用的判断方式)
        ItemStack weaponStack = attacker.getWeaponItem(); // 获取当前使用的武器
        if (!(weaponStack.getItem() instanceof MaceItem)) {
            return; // 不是重锤，直接返回
        }

        // 3. 检查是否处于下落猛击状态 (参照重锤物品的代码)
        if (!(attacker.fallDistance > 1.5f && !attacker.isFallFlying())) {
            return; // 下落距离不足，不是猛击
        }

        // 通过所有检查，确认这是一次重锤下落猛击！
        // 获取药水效果倍率并放大伤害
        float multiplier = MaceCrush_Effect.getMultiplier(attacker, MaceCrush);
        if (multiplier != 1.0f) {
            float originalDamage = event.getAmount();
            float newDamage = originalDamage * multiplier;
            event.setAmount(newDamage);
        }
    }

}
