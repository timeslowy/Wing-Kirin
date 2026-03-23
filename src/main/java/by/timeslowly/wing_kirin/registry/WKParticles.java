package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPE =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Wing_kirin.MOD_ID);
    // TODO：我有一个注册自定义粒子的梦想








    public static void register(IEventBus eventBus) {
        PARTICLE_TYPE.register(eventBus);
    }
}
