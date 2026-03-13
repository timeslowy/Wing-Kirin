package by.timeslowly.wing_kirin;

import by.timeslowly.wing_kirin.registry.WKAttributes;
import by.timeslowly.wing_kirin.registry.WKCreativeTabs;
import by.timeslowly.wing_kirin.registry.WKEffects;
import by.timeslowly.wing_kirin.registry.WKItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// TODO:使得模组整体更规范化
@Mod(Wing_kirin.MOD_ID)
public class Wing_kirin {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "wing_kirin";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Wing_kirin(@NotNull IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for mod loading

        WKItems.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKEffects.register(modEventBus);
        WKAttributes.register(modEventBus);

    }
}
