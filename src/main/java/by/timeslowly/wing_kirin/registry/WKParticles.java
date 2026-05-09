package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 粒子类型注册（通用端）。
 * 粒子工厂注册已移至 ClientParticleProviders（客户端专用）。
 */
public class WKParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Wing_kirin.MOD_ID);

    // 「龙吼功」粒子
    public static final Supplier<SimpleParticleType> THUNDEROUS_SHOUT =
            PARTICLE_TYPES.register("thunderous_shout", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
