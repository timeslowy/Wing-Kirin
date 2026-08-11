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

    /** 定身效果是否禁用玩家视角旋转 */
    public static final ModConfigSpec.BooleanValue DING_SHEN_DISABLE_LOOK_ROTATION;

    /** 定身效果是否完全锁定实体位置（禁止任何移动、传送与维度切换） */
    public static final ModConfigSpec.BooleanValue DING_SHEN_LOCK_POSITION;

    /** 粉碎定身时是否为非翼麒麟龙玩家提供「不破不立」增益（生命恢复Ⅱ10秒）；关闭时仅翼麒麟可获得 */
    public static final ModConfigSpec.BooleanValue SHATTER_BUFF_FOR_NON_WING_KIRIN;

    /** 定身粉碎阈值：单次受到的伤害超过最大血量的该比例时，定身效果被"粉碎"（0-1，默认0.3） */
    public static final ModConfigSpec.DoubleValue DING_SHEN_SHATTER_RATIO;

    /** 浩然正气是否使翼麒麟无视法力消耗 */
    public static final ModConfigSpec.BooleanValue GREAT_ZHENGQI_IGNORE_MANA_COST;

    /** 唯快不破是否对所有伤害类型生效（默认关，仅近战攻击） */
    public static final ModConfigSpec.BooleanValue UNSTOPPABLE_SPEED_ALL_DAMAGE_TYPES;

    /** 唯快不破是否穿透护甲（默认关） */
    public static final ModConfigSpec.BooleanValue UNSTOPPABLE_SPEED_BYPASS_ARMOR;

    /** 金钟是否会随音爆伤害每个实体而快速消耗耐久 */
    public static final ModConfigSpec.BooleanValue FAST_DURABILITY_HURT;

    /** 魔法禁用效果是否不可治愈 */
    public static final ModConfigSpec.BooleanValue MAGIC_DISABLED_INCURABLE;

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

        // 6.是否禁用玩家视角旋转
        DING_SHEN_DISABLE_LOOK_ROTATION = builder
                .comment("Whether the Ding Shen effect disables the player's ability to rotate their view (camera/look rotation).")
                .translation("wing_kirin.config.ding_shen.disableLookRotation")
                .define("disableLookRotation", false);

        // 7.是否完全锁定实体位置
        DING_SHEN_LOCK_POSITION = builder
                .comment("Whether the Ding Shen effect completely locks the entity's position, preventing ALL forms of movement, teleportation, and dimension changes. Off by default. Use with caution.")
                .translation("wing_kirin.config.ding_shen.lockPosition")
                .define("lockPosition", false);

        // 8.粉碎定身时是否为非翼麒麟龙玩家提供「不破不立」增益
        SHATTER_BUFF_FOR_NON_WING_KIRIN = builder
                .comment("Whether non-Wing Kirin dragon players also receive the 'Break to Build' shatter buff when they shatter a frozen entity. When disabled, only Wing Kirin dragon players receive it.")
                .translation("wing_kirin.config.ding_shen.shatterBuffForNonWingKirin")
                .define("shatterBuffForNonWingKirin", false);

        // 9.粉碎伤害阈值（单次伤害占最大血量的比例，0-1）
        DING_SHEN_SHATTER_RATIO = builder
                .comment("The ratio of max health a single damage hit must exceed for the Ding Shen effect to be shattered (0.0 - 1.0, e.g. 0.3 = 30%). 0.0 means any damage shatters it.")
                .translation("wing_kirin.config.ding_shen.shatterThreshold")
                .defineInRange("shatterThreshold", 0.3, 0.0, 1.0);

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

        builder.pop();

        // 魔法禁用效果设置（来自龙之生存）
        builder.comment("Magic Disabled Effect Settings (from DragonSurvival)")
               .translation("wing_kirin.config.magic_disabled")
               .push("magic_disabled");

        // 1.魔法禁用效果是否不可治愈
        MAGIC_DISABLED_INCURABLE = builder
                .comment("Whether the Magic Disabled effect is incurable (cannot be removed by milk, honey bottles, or other curative items).")
                .translation("wing_kirin.config.magic_disabled.incurable")
                .define("incurable", true);

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

    /** 便捷方法：获取定身是否禁用玩家视角旋转 */
    public static boolean shouldDingShenDisableLookRotation() {
        return DING_SHEN_DISABLE_LOOK_ROTATION.get();
    }

    /** 便捷方法：获取定身是否完全锁定实体位置 */
    public static boolean shouldDingShenLockPosition() {
        return DING_SHEN_LOCK_POSITION.get();
    }

    /** 便捷方法：获取粉碎定身时是否为非翼麒麟龙玩家提供「不破不立」增益 */
    public static boolean shouldProvideShatterBuffToNonWingKirinDragons() {
        return SHATTER_BUFF_FOR_NON_WING_KIRIN.get();
    }

    /** 便捷方法：获取定身粉碎的伤害阈值（占最大血量的比例，0-1） */
    public static double getDingShenShatterRatio() {
        return DING_SHEN_SHATTER_RATIO.get();
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

    /** 便捷方法：获取魔法禁用效果是否不可治愈 */
    public static boolean shouldMagicDisabledBeIncurable() {
        return MAGIC_DISABLED_INCURABLE.get();
    }
}
