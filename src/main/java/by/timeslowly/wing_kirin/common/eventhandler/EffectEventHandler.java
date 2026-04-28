package by.timeslowly.wing_kirin.common.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class EffectEventHandler {
    /**
     * 检查受害者的定身状态效果
     * 定身时间过长会导致"肌肉松弛"，承受更多伤害
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        var DingShen = victim.getEffect(WKEffects.DING_SHEN);
        if (DingShen != null) {
            int duration = DingShen.getDuration();
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }
    }

    // 禁用交互
    @SubscribeEvent
    public static void onPlayerInteract(InputEvent.@NotNull InteractionKeyMappingTriggered event) {
        // 1. 获取当前的本地玩家
        Player player = Minecraft.getInstance().player;
        // 2. 安全检查：玩家为空则直接返回
        if (player == null) {
            return;
        }
        // 3. 判断是否拥有效果
        if (player.hasEffect(WKEffects.DING_SHEN)) {
            // 提示
            player.displayClientMessage(Component.translatable("actionbar.wing_kirin.ability.stasis_hex.disable_interaction"),true);
            event.setCanceled(true);
        }
    }

    // 显示快捷栏禁用视觉效果
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderHotbar(RenderGuiLayerEvent.@NotNull Post event) {
        // 检查当前渲染的元素是否为快捷栏 (HOTBAR)
        if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            // 1. 检查全局HUD是否可见 (响应F1键)
            if (Minecraft.getInstance().options.hideGui) {
                return; // F1模式下跳过渲染
            }
            // 检查玩家是否拥有“定身”效果
            if (player.hasEffect(WKEffects.DING_SHEN)) {
                GuiGraphics guiGraphics = event.getGuiGraphics();

                // 获取屏幕尺寸并计算主快捷栏位置
                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                int hotbarWidth = 182;
                int hotbarHeight = 22;
                int x = (screenWidth - hotbarWidth) / 2;
                int y = screenHeight - hotbarHeight;
                // 副手
                int offhandWidth = 22;
                int offhandHeight = 22;
                int offhandX = x - offhandWidth - 8; // 主快捷栏左侧，留出8像素间隔

                // 主快捷栏蒙版
                guiGraphics.fill(x, y, x + hotbarWidth, y + hotbarHeight, 0xB34D4D4D);
                // 副手槽位蒙版
                guiGraphics.fill(offhandX, y, offhandX + offhandWidth, y + offhandHeight, 0xB34D4D4D);
            }
        }
    }
}
