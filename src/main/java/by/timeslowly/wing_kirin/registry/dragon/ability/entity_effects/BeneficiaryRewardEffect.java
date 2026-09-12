package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedValue;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import by.timeslowly.wing_kirin.registry.WKEffects;
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
 * 若施法者持有「浩然正气」效果，受惠数先 ×1.2（向下取整）再参与结算，与 1.21.1
 * {@code caculate/great_zhengqi_bonus.mcfunction} 一致。
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

        // 「浩然正气」加成：持有该效果时受惠数 ×1.2，整数向下取整（等价 1.21.1
        // great_zhengqi_bonus.mcfunction 的「先 ×12 再 ÷10」计分板整数运算）。
        // 1.21.1 在授时之前完成加成，时长、actionbar 显示与 3 人进度判定均使用加成后数量。
        int effectiveCount = dragon.hasEffect(WKEffects.GREAT_ZHENGQI.get()) ? count * 12 / 10 : count;

        int totalSeconds = (int) (effectiveCount * perBeneficiaryDuration.calculate(ability.level()));
        if (totalSeconds > 0) {
            dragon.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, totalSeconds * 20, amplifier));
        }

        // actionbar 显示惠及数（1.21.1 为 title actionbar：翻译键 + 绿色数字）
        dragon.displayClientMessage(
                Component.translatable("actionbar.wing_kirin.ability.invincible_benevolence.beneficiary_amount")
                        .append(Component.literal(String.valueOf(effectiveCount)).withStyle(ChatFormatting.GREEN)),
                true);

        // 单次使 3 名以上盟友受惠则授予「仁者无敌」进度
        Advancement advancement = dragon.server.getAdvancements().getAdvancement(INVINCIBLE_BENEVOLENCE_ADVANCEMENT);
        if (effectiveCount >= 3 && advancement != null) {
            dragon.getAdvancements().award(advancement, ADVANCEMENT_CRITERION);
        }

        // 「仁者无敌惠及友方总数」统计（等价 1.21.1 search_beneficiary.mcfunction 的 wk-stats add；
        // 该计数在 1.21.1 由搜索循环按实体逐个累加，不受浩然正气加成影响，故用未加成的原始受惠数；
        // 上限沿用 max_beneficiaries 截断，与 1.21.1 搜索循环上限 88 的统计语义一致）
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
