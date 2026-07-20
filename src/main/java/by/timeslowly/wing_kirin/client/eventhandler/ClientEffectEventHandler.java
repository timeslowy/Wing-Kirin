package by.timeslowly.wing_kirin.client.eventhandler;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID, value = Dist.CLIENT)
public class ClientEffectEventHandler {

    // 快捷栏蒙版尺寸常量
    private static final int HOTBAR_WIDTH = 182;
    private static final int HOTBAR_HEIGHT = 22;
    private static final int OFFHAND_WIDTH = 22;
    private static final int OFFHAND_GAP = 8;

    // 屏幕尺寸缓存，仅在窗口大小变化时重新计算布局位置
    private static int cachedScreenWidth = -1;
    private static int cachedScreenHeight = -1;
    private static int cachedHotbarX;
    private static int cachedHotbarY;
    private static int cachedOffhandX;

    // 定身效果金色遮罩常量
    private static final int DINGSHEN_VIGNETTE_RGB = 0xFBDC92;
    private static final int DINGSHEN_VIGNETTE_MAX_ALPHA = 0xBB;
    private static final int DINGSHEN_VIGNETTE_MAX_STEPS = 24;

    // 遮罩尺寸缓存，仅在窗口大小变化时重新计算
    private static int vignetteCachedScreenWidth = -1;
    private static int vignetteCachedScreenHeight = -1;
    private static int vignetteCachedEdgeWidth;
    private static int vignetteCachedFillOpaque;
    private static int vignetteCachedFillTransparent;

    // 定身金色遮罩渲染
    @SubscribeEvent
    public static void onRenderDingShenVignette(RenderGuiLayerEvent.@NotNull Post event) {
        if (!event.getName().equals(VanillaGuiLayers.CAMERA_OVERLAYS)) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (Minecraft.getInstance().options.hideGui) return;
        if (!player.hasEffect(WKEffects.DING_SHEN)) return;

        GuiGraphics g = event.getGuiGraphics();
        int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        if (w != vignetteCachedScreenWidth || h != vignetteCachedScreenHeight) {
            vignetteCachedScreenWidth = w;
            vignetteCachedScreenHeight = h;
            vignetteCachedEdgeWidth = Math.max(w, h) / 20;
            vignetteCachedFillOpaque = (DINGSHEN_VIGNETTE_MAX_ALPHA << 24) | DINGSHEN_VIGNETTE_RGB;
            vignetteCachedFillTransparent = DINGSHEN_VIGNETTE_RGB;
        }

        int edge = vignetteCachedEdgeWidth;
        if (edge <= 0) return;

        drawTopEdge(g, w, edge);
        drawBottomEdge(g, w, h, edge);
        drawLeftEdge(g, h, edge);
        drawRightEdge(g, w, h, edge);
    }

    private static void drawTopEdge(@NotNull GuiGraphics g, int screenW, int edge) {
        g.fillGradient(0, 0, screenW, edge, 0, vignetteCachedFillOpaque, vignetteCachedFillTransparent);
    }

    private static void drawBottomEdge(@NotNull GuiGraphics g, int screenW, int screenH, int edge) {
        g.fillGradient(0, screenH - edge, screenW, screenH, 0,
                vignetteCachedFillTransparent, vignetteCachedFillOpaque);
    }

    private static void drawLeftEdge(GuiGraphics g, int screenH, int edge) {
        int steps = Math.min(DINGSHEN_VIGNETTE_MAX_STEPS, edge);
        int last = Math.max(steps - 1, 1);
        for (int i = 0; i < steps; i++) {
            int alpha = DINGSHEN_VIGNETTE_MAX_ALPHA * (steps - 1 - i) / last;
            int color = (alpha << 24) | DINGSHEN_VIGNETTE_RGB;
            int x1 = i * edge / steps;
            int x2 = (i + 1) * edge / steps;
            if (x2 > x1) g.fill(x1, 0, x2, screenH, color);
        }
    }

    private static void drawRightEdge(GuiGraphics g, int screenW, int screenH, int edge) {
        int steps = Math.min(DINGSHEN_VIGNETTE_MAX_STEPS, edge);
        int last = Math.max(steps - 1, 1);
        for (int i = 0; i < steps; i++) {
            int alpha = DINGSHEN_VIGNETTE_MAX_ALPHA * i / last;
            int color = (alpha << 24) | DINGSHEN_VIGNETTE_RGB;
            int x1 = screenW - (steps - i) * edge / steps;
            int x2 = screenW - (steps - 1 - i) * edge / steps;
            if (x2 > x1) g.fill(x1, 0, x2, screenH, color);
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
        // 3. 判断是否拥有效果且配置启用禁用交互
        if (WKServerConfig.shouldDingShenDisableInteraction() && player.hasEffect(WKEffects.DING_SHEN)) {
            // 提示
            player.displayClientMessage(Component.translatable("actionbar.wing_kirin.ability.stasis_hex.disable_interaction"), true);
            event.setCanceled(true);
        }
    }

    // 显示快捷栏禁用视觉效果
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
            // 检查玩家是否拥有"定身"效果且配置启用禁用交互
            if (WKServerConfig.shouldDingShenDisableInteraction() && player.hasEffect(WKEffects.DING_SHEN)) {
                GuiGraphics guiGraphics = event.getGuiGraphics();

                int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

                // 仅在窗口尺寸变化时重新计算位置（避免每帧重复运算）
                if (screenWidth != cachedScreenWidth || screenHeight != cachedScreenHeight) {
                    cachedScreenWidth = screenWidth;
                    cachedScreenHeight = screenHeight;
                    cachedHotbarX = (screenWidth - HOTBAR_WIDTH) / 2;
                    cachedHotbarY = screenHeight - HOTBAR_HEIGHT;
                    cachedOffhandX = cachedHotbarX - OFFHAND_WIDTH - OFFHAND_GAP;
                }

                // 主快捷栏蒙版
                guiGraphics.fill(cachedHotbarX, cachedHotbarY, cachedHotbarX + HOTBAR_WIDTH, cachedHotbarY + HOTBAR_HEIGHT, 0xB34D4D4D);
                // 副手槽位蒙版
                guiGraphics.fill(cachedOffhandX, cachedHotbarY, cachedOffhandX + OFFHAND_WIDTH, cachedHotbarY + HOTBAR_HEIGHT, 0xB34D4D4D);
            }
        }
    }

}
