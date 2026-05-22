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

    /** 定身效果是否禁用龙玩家被动技能 */
    public static final ModConfigSpec.BooleanValue DING_SHEN_DISABLE_PASSIVE_ABILITIES;

    /** 定身效果是否禁用玩家交互 */
    public static final ModConfigSpec.BooleanValue DING_SHEN_DISABLE_INTERACTION;

    /** 定身效果每刻随机关闭GUI的概率（0-100） */
    public static final ModConfigSpec.IntValue DING_SHEN_CLOSE_GUI_CHANCE;

    /** 浩然正气是否使翼麒麟无视法力消耗 */
    public static final ModConfigSpec.BooleanValue GREAT_ZHENGQI_IGNORE_MANA_COST;

    /** 唯快不破是否对所有伤害类型生效（默认关，仅近战攻击） */
    public static final ModConfigSpec.BooleanValue UNSTOPPABLE_SPEED_ALL_DAMAGE_TYPES;

    /** 唯快不破是否穿透护甲（默认关） */
    public static final ModConfigSpec.BooleanValue UNSTOPPABLE_SPEED_BYPASS_ARMOR;

    /** 金钟是否会随音爆伤害每个实体而快速消耗耐久 */
    public static final ModConfigSpec.BooleanValue FAST_DURABILITY_HURT;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        // 定身效果设置
        builder.comment("Stasis Hex (Ding Shen) Effect Settings")
               .translation("wing_kirin.config.ding_shen")
               .push("Stasis Hex");

        // 1.是否禁用龙玩家主动技能
        DING_SHEN_DISABLE_ABILITIES = builder
                .comment("Whether the Ding Shen effect disables dragon active abilities.")
                .translation("wing_kirin.config.ding_shen.disableAbilities")
                .define("disableAbilities", true);

        // 2.是否禁用龙玩家被动技能
        DING_SHEN_DISABLE_PASSIVE_ABILITIES = builder
                .comment("Whether the Ding Shen effect disables dragon passive abilities. Independent of the active abilities setting.")
                .translation("wing_kirin.config.ding_shen.disablePassiveAbilities")
                .define("disablePassiveAbilities", false);

        // 4.是否禁用玩家交互
        DING_SHEN_DISABLE_INTERACTION = builder
                .comment("Whether the Ding Shen effect disables player interaction.")
                .translation("wing_kirin.config.ding_shen.disableInteraction")
                .define("disableInteraction", true);

        // 5.随机关闭GUI概率
        DING_SHEN_CLOSE_GUI_CHANCE = builder
                .comment("The chance per tick to randomly close the player's container GUI when affected by Ding Shen. 0 = never, 100 = always. Does not close the game menu (pause screen).")
                .translation("wing_kirin.config.ding_shen.closeGuiChance")
                .defineInRange("closeGuiChance", 1, 0, 100);

        builder.pop();

        // 浩然正气效果设置
        builder.comment("Great Zhengqi Effect Settings")
               .translation("wing_kirin.config.great_zhengqi")
               .push("great_zhengqi");

        // 1.效果存续时是否使翼麒麟龙玩家施法不消耗法力
        GREAT_ZHENGQI_IGNORE_MANA_COST = builder
                .comment("Whether Wing Kirin can ignore mana cost under the Great Zhengqi effect.")
                .translation("wing_kirin.config.great_zhengqi.ignoreManaCost")
                .define("ignoreManaCost", true);

        builder.pop();

        // 唯快不破效果设置
        builder.comment("Unstoppable Speed Effect Settings")
               .translation("wing_kirin.config.unstoppable_speed")
               .push("unstoppable_speed");

        // 1. 效果存续时是否允许所有伤害类型无视受击冷却
        UNSTOPPABLE_SPEED_ALL_DAMAGE_TYPES = builder
                .comment("Whether the Unstoppable Speed effect allows ALL damage types to bypass invulnerability cooldown. If false, only player attack (melee) damage bypasses cooldown.")
                .translation("wing_kirin.config.unstoppable_speed.allDamageTypes")
                .define("allDamageTypes", false);

        // 2. 效果存续时是否穿透护甲
        UNSTOPPABLE_SPEED_BYPASS_ARMOR = builder
                .comment("Whether the Unstoppable Speed effect allows attacks to bypass armor entirely.")
                .translation("wing_kirin.config.unstoppable_speed.bypassArmor")
                .define("bypassArmor", false);

        builder.pop();

        // 龙吼功 效果设置
        builder.comment("Thunderous Shout Settings")
                .translation("wing_kirin.config.thunderous_shout_settings")
                .push("thunderous_shout");

        // 1.是否使金钟耐久被快速消耗
        FAST_DURABILITY_HURT = builder
                .comment("Whether the Golden Bell will fast hurt durability with every entity hurt.")
                .translation("wing_kirin.config.thunderous_shout.fastDurabilityHurt")
                .define("fastDurabilityHurt",false);

        SPEC = builder.build();
    }

    /** 便捷方法：获取定身是否禁用主动技能的配置值 */
    public static boolean shouldDingShenDisableAbilities() {
        return DING_SHEN_DISABLE_ABILITIES.get();
    }

    /** 便捷方法：获取定身是否禁用被动技能的配置值 */
    public static boolean shouldDingShenDisablePassiveAbilities() {
        return DING_SHEN_DISABLE_PASSIVE_ABILITIES.get();
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

    /** 便捷方法：获取唯快不破是否对所有伤害类型生效（否则仅近战攻击） */
    public static boolean shouldUnstoppableSpeedApplyToAllDamageTypes() {
        return UNSTOPPABLE_SPEED_ALL_DAMAGE_TYPES.get();
    }

    /** 便捷方法：获取唯快不破是否穿透护甲 */
    public static boolean shouldUnstoppableSpeedBypassArmor() {
        return UNSTOPPABLE_SPEED_BYPASS_ARMOR.get();
    }

    /** 便捷方法：获取是否要快速消耗金钟耐久 */
    public static boolean shouldFastDurabilityHurt() {
        return FAST_DURABILITY_HURT.get();
    }
}
