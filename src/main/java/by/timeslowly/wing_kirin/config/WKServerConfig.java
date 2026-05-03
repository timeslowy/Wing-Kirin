package by.timeslowly.wing_kirin.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务端配置 - 定身效果相关设置
 * <p>
 * SERVER 类型配置：在单人游戏内可实时修改，多人游戏中由服务端同步且客户端只读
 */
public class WKServerConfig {

    public static final ModConfigSpec SPEC;

    /** 定身效果是否禁用龙玩家主动技能 */
    public static final ModConfigSpec.BooleanValue DING_SHEN_DISABLE_ABILITIES;

    /** 定身效果是否禁用玩家交互 */
    public static final ModConfigSpec.BooleanValue DING_SHEN_DISABLE_INTERACTION;

    /** 定身效果每刻随机关闭GUI的概率（0-100） */
    public static final ModConfigSpec.IntValue DING_SHEN_CLOSE_GUI_CHANCE;

    /** 浩然正气是否使翼麒麟无视法力消耗 */
    public static final ModConfigSpec.BooleanValue GREAT_ZHENGQI_IGNORE_MANA_COST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Stasis Hex (Ding Shen) Effect Settings")
               .translation("wing_kirin.config.ding_shen")
               .push("Stasis Hex");

        DING_SHEN_DISABLE_ABILITIES = builder
                .comment("Whether the Ding Shen effect disables dragon active abilities.")
                .translation("wing_kirin.config.ding_shen.disableAbilities")
                .define("disableAbilities", true);

        DING_SHEN_DISABLE_INTERACTION = builder
                .comment("Whether the Ding Shen effect disables player interaction.")
                .translation("wing_kirin.config.ding_shen.disableInteraction")
                .define("disableInteraction", true);

        DING_SHEN_CLOSE_GUI_CHANCE = builder
                .comment("The chance per tick to randomly close the player's container GUI when affected by Ding Shen. 0 = never, 100 = always. Does not close the game menu (pause screen).")
                .translation("wing_kirin.config.ding_shen.closeGuiChance")
                .defineInRange("closeGuiChance", 1, 0, 100);

        builder.pop();

        builder.comment("Great Zhengqi Effect Settings")
               .translation("wing_kirin.config.great_zhengqi")
               .push("great_zhengqi");

        GREAT_ZHENGQI_IGNORE_MANA_COST = builder
                .comment("Whether Wing Kirin can ignore mana cost under the Great Zhengqi effect.")
                .translation("wing_kirin.config.great_zhengqi.ignoreManaCost")
                .define("ignoreManaCost", true);

        builder.pop();

        SPEC = builder.build();
    }

    /** 便捷方法：获取定身是否禁用主动技能的配置值 */
    public static boolean shouldDingShenDisableAbilities() {
        return DING_SHEN_DISABLE_ABILITIES.get();
    }

    /** 便捷方法：获取定身是否禁用玩家交互的配置值 */
    public static boolean shouldDingShenDisableInteraction() {
        return DING_SHEN_DISABLE_INTERACTION.get();
    }

    /** 便捷方法：获取定身随机关闭GUI的概率（0-100） */
    public static int getDingShenCloseGuiChance() {
        return DING_SHEN_CLOSE_GUI_CHANCE.get();
    }

    /** 便捷方法：获取浩然正气是否使翼麒麟无视法力消耗 */
    public static boolean shouldGreatZhengqiIgnoreManaCost() {
        return GREAT_ZHENGQI_IGNORE_MANA_COST.get();
    }
}
