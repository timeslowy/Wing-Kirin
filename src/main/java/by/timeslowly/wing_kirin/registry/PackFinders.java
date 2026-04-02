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
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class PackFinders {
    public PackFinders() {
    }

    @SubscribeEvent
    public static void addPackFinders(@NotNull AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "data/wing_kirin/datapacks/low_unlock_condition"),
                PackType.SERVER_DATA,
                Component.translatable("datapack.wing_kirin.low_unlock_condition"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "assets/wing_kirin/resourcepacks/wing_kirin_ui"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("resoucepack.wing_kirin.wing_kirin_ui"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "data/wing_kirin/datapacks/low_upgrade_requirement"),
                PackType.SERVER_DATA,
                Component.translatable("datapack.wing_kirin.low_upgrade_requirement"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "data/wing_kirin/datapacks/unlock_acquired_abilities"),
                PackType.SERVER_DATA,
                Component.translatable("datapack.wing_kirin.unlock_acquired_abilities"),
                PackSource.FEATURE,
                false,
                Pack.Position.TOP
        );
    }
}
