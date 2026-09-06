package by.timeslowly.wing_kirin.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 服务端配置（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * SERVER 类型配置：在单人游戏内可实时修改，多人游戏中由服务端同步且客户端只读。
 * 移植说明：NeoForge 的 ModConfigSpec 与 Forge 1.20.1 的 ForgeConfigSpec 的 Builder API 一致，
 * 仅类名不同；1.20.1 主类经 ModLoadingContext.get().registerConfig 注册（1.21.1 为 ModContainer 注入）。
 * <p>
 * TODO:其余配置项暂未随本次移植（对齐 1.21.1 分支 WKServerConfig），清单如下，待各自功能移植时补充：
 * <ul>
 *   <li>ding_shen 段：定身禁用主动技能 / 禁用被动技能 / 禁用交互 / 每刻随机关闭GUI概率 / 禁用视角旋转 /
 *       完全锁定位置 / 粉碎伤害阈值（占最大血量比例）/ 粉碎时为非翼麒麟龙玩家提供「不破不立」增益</li>
 *   <li>unstoppable_speed 段：唯快不破是否对所有伤害类型生效 / 是否穿透护甲</li>
 * </ul>
 * 注：magic_disabled.incurable 的应用逻辑已实现（WKEffects 的 MobEffectEvent.Added 订阅 +
 * 效果实例 curative items 清空），其剩余部分（定身/唯快不破的到期行为）见 WKEffects 的 TODO。
 */
public class WKServerConfig {

    public static final ForgeConfigSpec SPEC;

    /** 浩然正气是否使翼麒麟无视法力消耗 */
    public static final ForgeConfigSpec.BooleanValue GREAT_ZHENGQI_IGNORE_MANA_COST;

    /** 金钟是否会随音爆伤害每个实体而快速消耗耐久 */
    public static final ForgeConfigSpec.BooleanValue FAST_DURABILITY_HURT;

    /** 魔法禁用效果是否不可治愈 */
    public static final ForgeConfigSpec.BooleanValue MAGIC_DISABLED_INCURABLE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

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

        // 龙吼功 效果设置
        builder.comment("Thunderous Shout Settings")
                .translation("wing_kirin.config.thunderous_shout")
                .push("thunderous_shout");

        // 1.是否使金钟耐久被快速消耗
        FAST_DURABILITY_HURT = builder
                .comment("Whether the Golden Bell will fast hurt durability with every entity hurt.")
                .translation("wing_kirin.config.thunderous_shout.fastDurabilityHurt")
                .define("fastDurabilityHurt", false);

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

        builder.pop();

        SPEC = builder.build();
    }

    private WKServerConfig() {}

    /** 便捷方法：获取浩然正气是否使翼麒麟无视法力消耗 */
    public static boolean shouldGreatZhengqiIgnoreManaCost() {
        return GREAT_ZHENGQI_IGNORE_MANA_COST.get();
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
