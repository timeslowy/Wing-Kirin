package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.*;
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

    public static final Holder<MobEffect> DING_SHEN = MOB_EFFECTS.register("ding_shen",
            () -> new DingShenEffect(MobEffectCategory.HARMFUL, 16506002));

    public static final Holder<MobEffect> GREAT_ZHENGQI = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqiEffect(MobEffectCategory.BENEFICIAL, 16506002));

    public static final Holder<MobEffect> MACE_CRUSH = MOB_EFFECTS.register("mace_crush",
            () -> new MaceCrushEffect(MobEffectCategory.BENEFICIAL, 16506000));

    public static final Holder<MobEffect> UNSTOPPABLE_SPEED = MOB_EFFECTS.register("unstoppable_speed",
            () -> new UnstoppableSpeedEffect(MobEffectCategory.BENEFICIAL, 3190479));

    public static final Holder<MobEffect> AMNESIA = MOB_EFFECTS.register("amnesia",
            () -> new AmnesiaEffect(MobEffectCategory.HARMFUL, 0x9FB3C8));


    // 药水效果移除行为
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.@NotNull Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            // 26.1 起效果治愈（EffectCure）机制被整体移除，getCure 不存在；
            // magic_disabled 不可治愈配置（shouldMagicDisabledBeIncurable）随之失效
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
        if (effectInstance.getEffect().value() instanceof DingShenEffect) {
            DingShenEffect.onEffectExpired(entity);
        }
        if (effectInstance.getEffect().value() instanceof UnstoppableSpeedEffect) {
            UnstoppableSpeedEffect.onEffectExpired(entity, effectInstance.getAmplifier());
        }
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
