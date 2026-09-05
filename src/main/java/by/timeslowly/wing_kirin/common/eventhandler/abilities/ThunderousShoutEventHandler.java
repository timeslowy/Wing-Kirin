package by.timeslowly.wing_kirin.common.eventhandler.abilities;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.item.GoldenBellItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 「龙吼功」事件处理器（自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 Forge 无 LivingDamageEvent.Post（1.21 才拆分 Pre/Post），改用同处护甲减免之后触发的 LivingDamageEvent。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class ThunderousShoutEventHandler {
    // 记录每个攻击者上次扣除金钟耐久的世界刻
    private static final Map<LivingEntity, Integer> LAST_DAMAGE_TICK = new WeakHashMap<>();

    // TODO:配置未随本次移植：1.21.1 中此处由 WKServerConfig.shouldFastDurabilityHurt()
    //  （fastDurabilityHurt，默认 false，即默认不按实体快速消耗耐久）控制，当前硬编码为默认值 false。
    //  待服务端配置移植后，将本常量替换回 WKServerConfig.shouldFastDurabilityHurt() 即可恢复快速消耗开关。
    private static final boolean FAST_DURABILITY_HURT = false;

    /**
     * 监听实体受伤事件，处理使用「龙吼功」时金钟的双倍耐久损耗。
     * <p>
     * 当伤害来源为音波爆炸（SONIC_BOOM）且攻击者主手持有金钟时，
     * 对该金钟造成2点耐久损耗。通过记录游戏刻来确保同一刻内只扣除一次耐久，
     * 快速消耗耐久配置关闭时，避免因事件重复触发导致的过度损耗。
     * </p>
     *
     * @param event 实体受伤事件对象，包含伤害来源、伤害值等信息
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingDamageEvent event) {
        DamageSource damageSource = event.getSource();
        if (damageSource.is(DamageTypes.SONIC_BOOM)) {
            if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
                ItemStack mainHand = attacker.getMainHandItem();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof GoldenBellItem) {
                    if (FAST_DURABILITY_HURT) {
                        // 快速消耗模式：每次事件都扣除耐久
                        mainHand.hurtAndBreak(2, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    } else {
                        // 默认模式：同一游戏刻内仅扣除一次耐久，避免事件重复触发导致过度损耗
                        int currentTick = Math.toIntExact(attacker.level().getGameTime());
                        Integer lastTick = LAST_DAMAGE_TICK.get(attacker);
                        if (lastTick == null || lastTick != currentTick) {
                            mainHand.hurtAndBreak(2, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                            LAST_DAMAGE_TICK.put(attacker, currentTick);
                        }
                    }
                }
            }
        }
    }
}
