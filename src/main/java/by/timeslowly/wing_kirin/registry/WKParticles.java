package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.client.particles.ClientParticleProviders;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 粒子类型注册（通用端，自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 Forge 无 Registries.PARTICLE_TYPE 常量，改用 ForgeRegistries.PARTICLE_TYPES。
 * 粒子工厂注册已移至 {@link ClientParticleProviders}（客户端专用）。
 */
public class WKParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(
            ForgeRegistries.PARTICLE_TYPES, WingKirin.MODID
    );

    // 「龙吼功」粒子
    public static final RegistryObject<SimpleParticleType> THUNDEROUS_SHOUT =
            PARTICLE_TYPES.register("thunderous_shout", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
