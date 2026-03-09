package by.timeslowly.wing_kirin;

import by.timeslowly.wing_kirin.registry.WKAttributes;
import by.timeslowly.wing_kirin.registry.WKCreativeTabs;
import by.timeslowly.wing_kirin.registry.WKEffects;
import by.timeslowly.wing_kirin.registry.WKItems;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Wing_kirin.MOD_ID)
public class Wing_kirin {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "wing_kirin";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Wing_kirin(@NotNull IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        WKItems.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKEffects.register(modEventBus);
        WKAttributes.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Wing_kirin) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::modifyEntityAttributes);

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    private void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER);
        event.add(EntityType.PLAYER, WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
