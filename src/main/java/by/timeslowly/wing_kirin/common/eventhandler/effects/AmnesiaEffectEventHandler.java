package by.timeslowly.wing_kirin.common.eventhandler.effects;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.effect.AmnesiaEffect;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 「失忆」期间的"无法产生新仇恨"拦截器（自 1.21.1 NeoForge 分支移植）：
 * 无论目标设置来自 {@code Mob#setTarget}（MOB_TARGET，目标目标 AI）
 * 还是脑记忆行为 StartAttacking（BEHAVIOR_TARGET，基于 Brain 的生物），
 * 只要生物处于失忆状态，就将待设置目标改写为 null（相当于取消本次目标变更）。
 * <p>
 * 额外监听 MobEffectEvent.Added：1.20.1 的 MobEffect 无 onEffectStarted 钩子，
 * 由该事件实现"效果初应用时立即清除当前仇恨目标"。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class AmnesiaEffectEventHandler {

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(WKEffects.AMNESIA.get())) {
            event.setNewTarget(null);
        }
    }

    // 1.21.1 MobEffect#onEffectStarted 的 1.20.1 等价实现：效果初应用时立即清仇恨
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }
        if (event.getEffectInstance().getEffect() == WKEffects.AMNESIA.get()) {
            AmnesiaEffect.forgetTargets(entity);
        }
    }
}
