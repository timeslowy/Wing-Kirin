package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT =
            DeferredRegister.create(Registries.SOUND_EVENT, Wing_kirin.MOD_ID);
    // TODO：注册自定义声音








    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
