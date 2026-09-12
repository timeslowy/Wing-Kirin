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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WingKirin.MODID)
public class WingKirin {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "wing_kirin";
    // Directly reference a slf4j logger
    // 与 1.21.1 分支一致改为 public，供 WKStatsCommand 等类记录日志
    public static final Logger LOGGER = LogUtils.getLogger();

    // 1.20.1 双端兼容（Forge 47.x 与 NeoForge 47.1.x，2026-09-12 字节码实证）：
    //   Forge 47.4.23 的 FMLModContainer 先试 (FMLJavaModLoadingContext)，失败才回落无参构造器；
    //   NeoForge 1.20.1（fancymodloader 47.2.2）先试无参，失败才按需注入 {IEventBus, ModContainer, FMLModContainer}。
    //   两套协议的**唯一交集是无参构造器** —— 带参构造器必然在其中一侧抛
    //   "Could not find mod constructor"，因此主类必须无参，事件总线改从静态入口获取。
    //   FMLJavaModLoadingContext.get() 两端实现均为 ModLoadingContext.get().extension()，
    //   且 FML 在 CONSTRUCT 之前已设置 active container（现有 registerConfig 亦依赖同一机制）。
    public WingKirin() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // 注册服务端配置（1.21.1 经构造器注入的 ModContainer 注册；1.20.1 Forge 用 ModLoadingContext 静态获取）
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, WKServerConfig.SPEC);
        // 注册客户端配置
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WKClientConfig.SPEC);
        // 注册（自 1.21.1 分支移植，顺序同 1.21.1 主类）
        WKAttributes.register(modEventBus);
        WKCreativeTabs.register(modEventBus);
        WKEffects.register(modEventBus);
        WKEnchantments.register(modEventBus);
        WKItems.register(modEventBus);
        WKParticles.register(modEventBus);
        WKSounds.register(modEventBus);
        WKStats.register(modEventBus);

        // 注册龙之技能自定义实体效果类型（DragonSurvival ability_entity_effect 注册表）
        WKAbilityEntityEffects.register(modEventBus);

        // 注册模组通用设置事件，用于注入自定义统计格式化器等（自 1.21.1 分支平移）
        modEventBus.addListener(WKStats::onCommonSetup);

        // 服务端配置 → 客户端同步通道（退出世界崩溃修复：客户端不再直接读 SERVER 配置）
        ConfigSyncHandler.init();
        MinecraftForge.EVENT_BUS.addListener(ConfigSyncHandler::onPlayerLogin);
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
