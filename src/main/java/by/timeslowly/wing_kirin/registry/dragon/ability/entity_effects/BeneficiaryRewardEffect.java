package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedValue;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import by.timeslowly.wing_kirin.registry.WKStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * 仁者无敌·受惠结算（{@code wing_kirin:beneficiary_reward}）。
 * <p>
 * 加在技能的 self 目标选择中（受惠标记完成之后执行），按受惠实体数给予施法者抗性提升Ⅴ：
 * 总时长（秒）= min(受惠数, max_beneficiaries) × per_beneficiary_duration（按技能等级计算）。
 * 同时在 actionbar 显示惠及数；单次 3 名以上盟友受惠则授予「仁者无敌」进度。
 * <p>
 * 移植说明：替代 1.21.1 的 {@code run_function -> search_beneficiary.mcfunction} 递归搜索链
 * 与 {@code apply_effect.mcfunction}（其中的宏行 {@code $effect give @s resistance $(...)} 在 1.20.1 不可用）。
 * Java 侧以 {@link MobEffectInstance} 直接授予精确时长，替代原「宏展开 + 一次性命令授予」，
 * 语义一致（1.21.1 亦为覆盖式授予：重复施法时新的时长覆盖旧的）。
 * 与 1.21.1 的差异：受惠数由计数器而非实体标签+递归循环获得（上限改为结算时截断，数值效果相同）；
 * 进度授予改为 Java（进度树尚未移植到 1.20.1，查找失败时静默跳过）。
 * <p>
 * JSON 字段（与 effect_type 平级）：
 * <pre>
 * "per_beneficiary_duration": { "type": "minecraft:linear", "base": 4, "per_level_above_first": 4 }  // 必填，每名受惠实体贡献的秒数（1~5 级对应 4/8/12/16/20）
 * "amplifier":                4    // 可选，默认 4（抗性提升Ⅴ）
 * "max_beneficiaries":        88   // 可选，默认 88（受惠数上限，与 1.21.1 搜索循环上限一致）
 * </pre>
 */
public record BeneficiaryRewardEffect(LevelBasedValue perBeneficiaryDuration, int amplifier, int maxBeneficiaries) implements AbilityEntityEffect {
    /** 「仁者无敌」进度（含父级链的进度树尚未移植到 1.20.1，运行时查找失败则跳过授予） */
    private static final ResourceLocation INVINCIBLE_BENEVOLENCE_ADVANCEMENT =
            new ResourceLocation("wing_kirin", "wing_kirin/invincible_benevolence");
    private static final String ADVANCEMENT_CRITERION = "Heal_at_least_3_Alies";

    public static final MapCodec<BeneficiaryRewardEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("per_beneficiary_duration").forGetter(BeneficiaryRewardEffect::perBeneficiaryDuration),
            Codec.INT.optionalFieldOf("amplifier", 4).forGetter(BeneficiaryRewardEffect::amplifier),
            Codec.INT.optionalFieldOf("max_beneficiaries", 88).forGetter(BeneficiaryRewardEffect::maxBeneficiaries)
    ).apply(instance, BeneficiaryRewardEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final @NotNull DragonAbilityInstance ability, final Entity target) {
        // 结算只关心施法者本人（本效果应位于 self 目标选择中），target 参数忽略
        int count = Math.min(BeneficiaryMarkEffect.takeCount(dragon), maxBeneficiaries);

        // TODO:「浩然正气」加成（1.21.1 中持有浩然正气时受惠数 ×1.2，见 caculate/great_zhengqi_bonus.mcfunction）。
        //  注意须在 max_beneficiaries 截断之后相乘（与 1.21.1 顺序一致）；实现时建议用谓词文件（effects 条件）判定，
        //  勿用 if data entity —— 1.20.1 的效果 NBT 键为 ActiveEffects 且效果 ID 为整数（与 1.20.5+ 的 active_effects 不同）。

        int totalSeconds = (int) (count * perBeneficiaryDuration.calculate(ability.level()));
        if (totalSeconds > 0) {
            dragon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, totalSeconds * 20, amplifier));
        }

        // actionbar 显示惠及数（1.21.1 为 title actionbar：翻译键 + 绿色数字）
        dragon.displayClientMessage(
                Component.translatable("actionbar.wing_kirin.ability.invincible_benevolence.beneficiary_amount")
                        .append(Component.literal(String.valueOf(count)).withStyle(ChatFormatting.GREEN)),
                true);

        // 单次使 3 名以上盟友受惠则授予「仁者无敌」进度
        Advancement advancement = dragon.server.getAdvancements().getAdvancement(INVINCIBLE_BENEVOLENCE_ADVANCEMENT);
        if (count >= 3 && advancement != null) {
            dragon.getAdvancements().award(advancement, ADVANCEMENT_CRITERION);
        }

        // 「仁者无敌惠及友方总数」统计（等价 1.21.1 search_beneficiary.mcfunction 的 wk-stats add；
        // 用截断后的受惠数一次累加，与 1.21.1 搜索循环上限 88 的统计语义一致）
        dragon.awardStat(WKStats.CuredAliesCount.get(), count);
    }

    // 技能侧边栏描述（self 目标：渲染为「#HEADER# + 本描述」）。数值高亮沿用 DSColors.dynamicValue，
    // 抗性等级用原版罗马数字键（enchantment.level.N，amplifier 4 → Ⅴ）
    @Override
    public @NotNull @Unmodifiable List<MutableComponent> getDescription(final Player dragon, final @NotNull DragonAbilityInstance ability) {
        int level = ability.level();
        Component seconds = DSColors.dynamicValue((int) perBeneficiaryDuration.calculate(level));
        Component romanLevel = DSColors.dynamicValue(Component.translatable("enchantment.level." + (amplifier + 1)));
        Component maxCount = DSColors.dynamicValue(maxBeneficiaries);

        MutableComponent description = Component.translatable("wing_kirin.ability.invincible_benevolence.reward.description",
                seconds, romanLevel, maxCount);

        return List.of(description);
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
