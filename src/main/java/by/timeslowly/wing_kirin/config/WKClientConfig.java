package by.timeslowly.wing_kirin.config;

import net.minecraftforge.common.ForgeConfigSpec;

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
