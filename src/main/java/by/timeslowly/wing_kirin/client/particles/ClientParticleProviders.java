package by.timeslowly.wing_kirin.client.particles;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.registry.WKParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端专用粒子工厂注册（自 1.21.1 NeoForge 分支移植）。
 * 从 WKParticles 中分离以避免服务端加载客户端类。
 * 注：1.20.1 Forge 的 @EventBusSubscriber 需显式指定 MOD 总线与 Dist.CLIENT。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientParticleProviders {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(WKParticles.THUNDEROUS_SHOUT.get(), ThunderousShoutParticles.Provider::new);
    }
}
