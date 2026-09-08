package by.timeslowly.wing_kirin;

import com.mojang.logging.LogUtils;
import by.timeslowly.wing_kirin.client.ClientHelper;
import by.timeslowly.wing_kirin.config.WKClientConfig;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKCreativeTabs;
import by.timeslowly.wing_kirin.registry.*;
import by.timeslowly.wing_kirin.registry.dragon.ability.WKAbilityEntityEffects;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WingKirin.MODID)
public class WingKirin {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "wing_kirin";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO：至少药水效果、物品、创造物品栏、寻包、附件替代、配置

    // Forge 47.4.23 的 @Mod 构造器注入仅支持 FMLJavaModLoadingContext（不支持 IEventBus）
    public WingKirin(@NotNull FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        // 注册服务端配置（1.21.1 经构造器注入的 ModContainer 注册；1.20.1 Forge 用 ModLoadingContext 静态获取）
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, WKServerConfig.SPEC);
        // 注册客户端配置（唯快不破第一人称残影开关；残影渲染本体待后续移植批次接入）
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WKClientConfig.SPEC);
        // 注册（自 1.21.1 分支移植，顺序同 1.21.1 主类）
        WKAttributes.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKEffects.register(modEventBus);
        WKEnchantments.register(modEventBus);
        WKItems.register(modEventBus);
        WKParticles.register(modEventBus);
        WKSounds.register(modEventBus);

        // 注册龙之技能自定义实体效果类型（DragonSurvival ability_entity_effect 注册表）
        WKAbilityEntityEffects.register(modEventBus);

        // 服务端配置 → 客户端同步通道（退出世界崩溃修复：客户端不再直接读 SERVER 配置）
        ConfigSyncHandler.init();
        MinecraftForge.EVENT_BUS.addListener(ConfigSyncHandler::onPlayerLogin);
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
    }


    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // 初始化 Shift 键状态检查（客户端实现，供物品 tooltip 的 Shift 描述使用）
            ClientHelper.SHIFT_DOWN = Screen::hasShiftDown;
        }
    }
}
