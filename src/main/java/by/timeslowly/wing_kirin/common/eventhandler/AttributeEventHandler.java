package by.timeslowly.wing_kirin.common.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class AttributeEventHandler {
    /**
     * 监听实体受到伤害事件，计算并应用多种伤害倍率修正。
     * <p>
     * 处理以下伤害修正逻辑：
     * <ol>
     *   <li>攻击者音波伤害倍率：使用「龙吼功」时，根据SONIC_BOOM_DAMAGE_MULTIPLIER属性调整伤害</li>
     *   <li>攻击者重锤猛击倍率：使用重锤进行下落攻击时，根据MACE_SMASH_DAMAGE_MULTIPLIER属性调整伤害</li>
     * </ol>
     * 所有倍率以乘法方式叠加，最终应用到伤害值上。
     * </p>
     *
     * @param event 实体 incoming 伤害事件对象，包含伤害来源、伤害值、受害者和攻击者等信息
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        /*
          检查攻击者的各类伤害加成属性
          包括音波伤害倍率和重锤猛击倍率
         */
        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        if (attackerEntity instanceof LivingEntity attacker) {
            if (source.is(DamageTypes.SONIC_BOOM)) {
                AttributeInstance sonicAttr = attacker.getAttribute(WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER);
                if (sonicAttr != null) {
                    multiplier *= (float) sonicAttr.getValue();
                }
            }

            ItemStack weapon = attacker.getWeaponItem();
            if (weapon.getItem() instanceof MaceItem) {
                if (attacker.fallDistance > 1.5f && !attacker.isFallFlying()) {
                    AttributeInstance smashAttr = attacker.getAttribute(WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER);
                    if (smashAttr != null) {
                        multiplier *= (float) smashAttr.getValue();
                    }
                }
            }
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }
    }
}
