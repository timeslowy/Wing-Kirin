package by.timeslowly.wing_kirin.client.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.client.particles.ThunderousShoutParticles;
import by.timeslowly.wing_kirin.registry.WKParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端专用粒子工厂注册。
 * 从 WKParticles 中分离以避免服务端加载客户端类。
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID, value = Dist.CLIENT)
public class ClientParticleProviders {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(WKParticles.THUNDEROUS_SHOUT.get(), ThunderousShoutParticles.Provider::new);
    }
}
