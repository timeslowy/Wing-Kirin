package by.timeslowly.wing_kirin.client.eventhandler;

import by.timeslowly.wing_kirin.WKClientHelper;
import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端专用事件处理器。
 * 仅在客户端加载，用于注册配置界面和初始化客户端辅助方法。
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(@NotNull FMLClientSetupEvent event) {
        // 初始化 Shift 键状态检查（客户端实现）
        WKClientHelper.SHIFT_DOWN = Screen::hasShiftDown;

        // 注册模组配置界面（单人游戏内可实时修改，多人游戏只读）
        event.enqueueWork(() -> ModList.get().getModContainerById(Wing_kirin.MOD_ID).ifPresent(container ->
                container.registerExtensionPoint(IConfigScreenFactory.class,
                (mc, parent) -> new ConfigurationScreen(container, parent))));
    }
}
