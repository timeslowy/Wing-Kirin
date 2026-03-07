package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.DingShen_Effect;
import by.timeslowly.wing_kirin.common.effect.GreatZhengqi_Effect;
import by.timeslowly.wing_kirin.common.effect.MaceCrush_Effect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

// 注册药水效果
@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, Wing_kirin.MOD_ID
    );

    public static final Holder<MobEffect> DingShen = MOB_EFFECTS.register("ding_shen",
            () -> new DingShen_Effect(MobEffectCategory.HARMFUL, -256));

    public static final Holder<MobEffect> GreatZhengqi = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqi_Effect(MobEffectCategory.BENEFICIAL, -13261));

    public static final Holder<MobEffect> MaceCrush = MOB_EFFECTS.register("mace_crush",
            () -> new MaceCrush_Effect(MobEffectCategory.BENEFICIAL, 16506000));

    // 药水效果移除行为
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.@NotNull Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(event.getEntity(), effectInstance);
        }
    }
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.@NotNull Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(event.getEntity(), effectInstance);
        }
    }
    private static void expireEffects(LivingEntity entity, @NotNull MobEffectInstance effectInstance) {
        if (effectInstance.getEffect().value() instanceof DingShen_Effect) {
            DingShen_Effect.onEffectExpired(entity);
        }
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
