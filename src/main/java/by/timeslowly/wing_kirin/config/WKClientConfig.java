package by.timeslowly.wing_kirin.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 客户端配置 - 渲染/视觉相关设置（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * CLIENT 类型配置：仅影响本地客户端的渲染表现，不参与服务端同步。
 * <p>
 * 注：唯快不破残影渲染（1.21.1 的 client.eventhandler.AfterimageRenderHandler 及其两个渲染
 * mixin）尚未随本次移植（GeckoLib 4.8.4 渲染管线与 1.21.1 差异较大，需单独批次处理），
 * 本配置项先行就位，待残影移植时接入。
 */
public class WKClientConfig {

    public static final ForgeConfigSpec SPEC;

    /** 唯快不破第一人称视角下是否渲染残影（默认开，残影起始点后移以避免遮挡视线） */
    public static final ForgeConfigSpec.BooleanValue UNSTOPPABLE_SPEED_FIRST_PERSON_AFTERIMAGES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // 唯快不破效果设置
        builder.comment("Unstoppable Speed Effect Settings")
               .translation("wing_kirin.config.unstoppable_speed")
               .push("unstoppable_speed");

        // 1. 第一人称视角下是否渲染残影（开启时残影起始点向后移动，避免遮挡视线）
        UNSTOPPABLE_SPEED_FIRST_PERSON_AFTERIMAGES = builder
                .comment("Whether afterimages from the Unstoppable Speed effect are visible in first person view. If enabled, the afterimage trail starts slightly behind the player so it does not block the view.")
                .translation("wing_kirin.config.unstoppable_speed.firstPersonAfterimages")
                .define("firstPersonAfterimages", true);

        builder.pop();

        SPEC = builder.build();
    }

    private WKClientConfig() {}

    /** 便捷方法：获取唯快不破第一人称视角下是否渲染残影 */
    public static boolean shouldShowAfterimagesInFirstPerson() {
        return UNSTOPPABLE_SPEED_FIRST_PERSON_AFTERIMAGES.get();
    }
}
