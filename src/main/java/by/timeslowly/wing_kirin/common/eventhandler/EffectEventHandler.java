package by.timeslowly.wing_kirin.common.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class EffectEventHandler {
    /**
     * 检查受害者的定身状态效果
     * 定身时间过长会导致"肌肉松弛"，承受更多伤害
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        var DingShen = victim.getEffect(WKEffects.DING_SHEN);
        if (DingShen != null) {
            int duration = DingShen.getDuration();
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }
    }
}
