package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.GoldenBellItem;
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

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEventHandler {
    // 记录每个攻击者上次扣除金钟耐久的世界刻
    private static final Map<LivingEntity, Integer> LAST_DAMAGE_TICK = new WeakHashMap<>();
    /**
     * 监听实体受伤事件，处理使用「龙吼功」时金钟的双倍耐久损耗。
     * <p>
     * 当伤害来源为音波爆炸（SONIC_BOOM）且攻击者主手持有金钟时，
     * 对该金钟造成2点耐久损耗。通过记录游戏刻来确保同一刻内只扣除一次耐久，
     * 避免因事件重复触发导致的过度损耗。
     * </p>
     *
     * @param event 实体受伤事件的后置事件对象，包含伤害来源、伤害值等信息
     */
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.@NotNull Post event) {
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.SONIC_BOOM)) {
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
                ItemStack mainHand = attacker.getMainHandItem();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof GoldenBellItem) {
                    int currentTick = Math.toIntExact(attacker.level().getGameTime());
                    Integer lastTick = LAST_DAMAGE_TICK.get(attacker);
                    if (lastTick == null || lastTick != currentTick) {
                        mainHand.hurtAndBreak(2, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
                        LAST_DAMAGE_TICK.put(attacker, currentTick);
                    }
                }
            }
        }
    }

    /**
     * 监听实体受到伤害事件，计算并应用多种伤害倍率修正。
     * <p>
     * 处理以下伤害修正逻辑：
     * <ol>
     *   <li>受害者定身效果：被定身超过50秒（1000 ticks）或无限时长时，承受1.5倍伤害（"肌肉松弛"效果）</li>
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
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
    
        /*
         * 检查受害者的定身状态效果
         * 定身时间过长会导致"肌肉松弛"，承受更多伤害
         */
        var DingShen = victim.getEffect(WKEffects.DING_SHEN);
        if (DingShen != null) {
            int duration = DingShen.getDuration();
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }
    
        /*
         * 检查攻击者的各类伤害加成属性
         * 包括音波伤害倍率和重锤猛击倍率
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
