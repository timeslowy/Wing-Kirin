package by.timeslowly.wing_kirin.common.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 「失忆」期间的"无法产生新仇恨"拦截器：
 * 无论目标设置来自 {@code Mob#setTarget}（MOB_TARGET，目标目标 AI）
 * 还是脑记忆行为 StartAttacking（BEHAVIOR_TARGET，基于 Brain 的生物），
 * 只要生物处于失忆状态，就将待设置目标改写为 null（相当于取消本次目标变更）。
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class AmnesiaEffectEventHandler {

    @SubscribeEvent
    public static void onChangeTarget(@NotNull LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(WKEffects.AMNESIA)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }
}
