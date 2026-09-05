package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 声音事件注册（自 1.21.1 NeoForge 分支移植）。
 * 声音资源由 assets/wing_kirin/sounds.json 与 sounds/ 提供。
 */
public class WKSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, WingKirin.MODID
    );

    // 注册「龙吼功」用声音事件（以便调用，资源包即可注册之）
    public static final RegistryObject<SoundEvent> THUNDEROUS_SHOUT = SOUND_EVENTS.register("thunderous_shout",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(WingKirin.MODID, "thunderous_shout")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
