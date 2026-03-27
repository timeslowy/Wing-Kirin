package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT = DeferredRegister.create(Registries.SOUND_EVENT, Wing_kirin.MOD_ID);

    // 注册「龙吼功」用声音事件（以便调用，资源包即可注册之）
    public static final DeferredHolder<SoundEvent, SoundEvent> THUNDEROUS_SHOUT =
            SOUND_EVENT.register("thunderous_shout", () -> SoundEvent.createVariableRangeEvent(
                            ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "thunderous_shout")));





    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
