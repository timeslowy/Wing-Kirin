package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber
public class PackFinders {
    public PackFinders() {
    }

    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "data/wing_kirin/datapacks/low_unlock_condition"),
                PackType.SERVER_DATA,
                Component.literal("Wing Kirin - Low Unlock Condition"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
    }
}
