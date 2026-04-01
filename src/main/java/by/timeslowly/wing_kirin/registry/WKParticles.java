package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.client.particles.ThunderousShoutParticles;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@EventBusSubscriber(Dist.CLIENT)
public class WKParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, Wing_kirin.MOD_ID);

    // 「龙吼功」粒子
    public static final Supplier<SimpleParticleType> THUNDEROUS_SHOUT =
            PARTICLE_TYPES.register("thunderous_shout", () -> new SimpleParticleType(true));


    // 注册什么“粒子工厂”提供贴图
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(@NotNull RegisterParticleProvidersEvent event){
        event.registerSpriteSet(WKParticles.THUNDEROUS_SHOUT.get(), ThunderousShoutParticles.Provider::new);
    }


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
