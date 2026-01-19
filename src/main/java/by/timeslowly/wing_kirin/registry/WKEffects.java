package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.DingShen_Effect;
import by.timeslowly.wing_kirin.common.effect.GreatZhengqi_Effect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, Wing_kirin.MOD_ID
    );

    public static final Holder<MobEffect> DingShen = MOB_EFFECTS.register("dingshen",
            () -> new DingShen_Effect(MobEffectCategory.HARMFUL, -1));

    public static final Holder<MobEffect> GreatZhengqi = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqi_Effect(MobEffectCategory.BENEFICIAL, -1));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
