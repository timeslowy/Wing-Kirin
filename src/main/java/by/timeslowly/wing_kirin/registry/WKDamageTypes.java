package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES =
            DeferredRegister.create(Registries.DAMAGE_TYPE, Wing_kirin.MOD_ID);

    // 伤害类型注册名,已在数据包内注册，未来可调用
    public static final DeferredHolder<DamageType, DamageType> BREATH_DISAPPEAR =
            DAMAGE_TYPES.register("breath_disappear", () -> new DamageType("wing_kirin.breath_disappear", 0.1F));

    public static final DeferredHolder<DamageType, DamageType> COUNTER_SHOCK =
            DAMAGE_TYPES.register("counter_shock", () -> new DamageType("wing_kirin.counter_shock", 0.1F));

    public static final DeferredHolder<DamageType, DamageType> RAINSTORM_ARROW =
            DAMAGE_TYPES.register("rainstorm_arrow", () -> new DamageType("wing_kirin.rainstorm_arrow", 0.1F));

    public static final DeferredHolder<DamageType, DamageType> TRACE_DING =
            DAMAGE_TYPES.register("trace_ding", () -> new DamageType("wing_kirin.trace_ding", 0.1F));

    public static final DeferredHolder<DamageType, DamageType> WIND_DEVASTATE =
            DAMAGE_TYPES.register("wind_devastate", () -> new DamageType("wing_kirin.wind_devastate", 0.1F));


    public static void register(IEventBus eventBus) {
        DAMAGE_TYPES.register(eventBus);
    }
}