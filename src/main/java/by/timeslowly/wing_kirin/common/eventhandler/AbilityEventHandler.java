package by.timeslowly.wing_kirin.common.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.GoldenBellItem;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class AbilityEventHandler {
    // 记录每个攻击者上次扣除金钟耐久的世界刻
    private static final Map<LivingEntity, Integer> LAST_DAMAGE_TICK = new WeakHashMap<>();
    /**
     * 监听实体受伤事件，处理使用「龙吼功」时金钟的双倍耐久损耗。
     * <p>
     * 当伤害来源为音波爆炸（SONIC_BOOM）且攻击者主手持有金钟时，
     * 对该金钟造成2点耐久损耗。通过记录游戏刻来确保同一刻内只扣除一次耐久，
     * 快速消耗耐久配置关闭时，避免因事件重复触发导致的过度损耗。
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
                    if (WKServerConfig.shouldFastDurabilityHurt()) {
                    // 快速消耗模式：每次事件都扣除耐久
                    mainHand.hurtAndBreak(2, attacker, LivingEntity.getSlotForHand(attacker.getUsedItemHand()));
                } else {
                    // 默认模式：同一游戏刻内仅扣除一次耐久，避免事件重复触发导致过度损耗
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
    }
}
