package by.timeslowly.wing_kirin;

import com.mojang.logging.LogUtils;
import by.timeslowly.wing_kirin.client.ClientHelper;
import by.timeslowly.wing_kirin.registry.WKCreativeTabs;
import by.timeslowly.wing_kirin.registry.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
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

    // TODO：至少属性、药水效果、物品、创造物品栏、声音、粒子、寻包、附件替代、配置

    // Forge 47.4.23 的 @Mod 构造器注入仅支持 FMLJavaModLoadingContext（不支持 IEventBus）
    public WingKirin(@NotNull FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        // 注册（自 1.21.1 分支移植，顺序同 1.21.1 主类）
        WKCreativeTabs.register(modEventBus);
        WKEffects.register(modEventBus);
        WKItems.register(modEventBus);
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
