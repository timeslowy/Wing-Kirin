package by.timeslowly.wing_kirin;

import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(Wing_kirin.MOD_ID)
public class Wing_kirin {
    // 定义模组ID以供他处调用
    public static final String MOD_ID = "wing_kirin";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogManager.getLogger("Wing Kirin");

    public Wing_kirin(@NotNull IEventBus modEventBus, @NotNull ModContainer container) {
        // 注册服务端配置
        container.registerConfig(ModConfig.Type.SERVER, WKServerConfig.SPEC);

        // 注册模组配置界面已移至 ClientSetup（客户端专用事件处理器），以避免服务端加载客户端类

        // 注册模组加载的通用内容设置

        WKAttributes.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKDamageTypes.register(modEventBus);
        WKEffects.register(modEventBus);
        WKEnchantments.register(modEventBus);
        WKItems.register(modEventBus);
        WKParticles.register(modEventBus);
        WKSounds.register(modEventBus);
        WKStats.register(modEventBus);

        // 注册模组通用设置事件，用于注入自定义统计格式化器等
        modEventBus.addListener(WKStats::onCommonSetup);
    }

}
