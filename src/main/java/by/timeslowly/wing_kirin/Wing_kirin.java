package by.timeslowly.wing_kirin;

import by.timeslowly.wing_kirin.registry.*;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// TODO:使得模组整体更规范化
@Mod(Wing_kirin.MOD_ID)
public class Wing_kirin {
    // 定义模组ID以供他处调用
    public static final String MOD_ID = "wing_kirin";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Wing_kirin(@NotNull IEventBus modEventBus, ModContainer modContainer) {
        // 注册模组加载的通用内容设置

        WKAttributes.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKDamageTypes.register(modEventBus);
        WKEffects.register(modEventBus);
        WKEnchantments.register(modEventBus);
        WKItems.register(modEventBus);

    }
}
