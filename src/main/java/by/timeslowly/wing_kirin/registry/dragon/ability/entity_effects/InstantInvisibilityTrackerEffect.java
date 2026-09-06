package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.common.codecs.LevelBasedValue;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.dragonsurvivalteam.dragonsurvival.util.DSColors;
import by.timeslowly.wing_kirin.common.eventhandler.abilities.InstantInvisibilityEventHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * 聚形散气·效果追踪（{@code wing_kirin:invisibility_tracker}）。
 * <p>
 * 加在技能的 self 目标选择中（须位于 damage_modification / modifier 效果之前，
 * 以便先行置位 remove_check 计分板）。施加时登记一个服务端追踪器：
 * <ol>
 *   <li>将所有者的 {@code wk.instant_invisibility.remove_check} 计分板置 1
 *       （JSON 中三个效果的 early_removal_condition 依赖此计分板）；</li>
 *   <li>由 {@link InstantInvisibilityEventHandler} 每刻驱动：倒计时、超距破除（清隐身 + 粒子音效）、
 *       死亡/离线清理、actionbar 距离提示；「破隐一击」由 LivingHurtEvent 检测。</li>
 * </ol>
 * <p>
 * 移植说明：替代 1.21.1 的 {@code run_function -> main.mcfunction} + {@code tick.mcfunction} +
 * {@code init_marker.mcfunction} + {@code destruct/distance_out.mcfunction} + distance_notice 函数链。
 * 原链使用宏（$(Owner_hex) 作计分板持有者/实体选择、with entity 传参）、UUID 转十六进制库、
 * {@code return fail}（1.20.5+）与 player_hurt_entity 进度（1.21 的 player 条件列表格式），
 * 均为 1.20.1 不可用特性，故按方案B 由 Java 接管；marker 标志实体由内存追踪器取代。
 * JSON 中 damage_modification / potion / block_vision 等效果保持原样；
 * 原 dragonsurvival:modifier 的破隐一击攻击加成（+50% 攻击伤害）改由本效果的
 * attack_damage_bonus 字段经属性修饰符实现，随追踪器破除/到期一并移除。
 * <p>
 * JSON 字段（与 effect_type 平级）：
 * <pre>
 * "per_level_duration": { "type": "minecraft:linear", "base": 200, "per_level_above_first": 200 }  // 必填，每级持续刻数
 * "radius":             25   // 可选，默认 25（超出此距离自动破除）
 * "attack_damage_bonus": { "type": "minecraft:linear", "base": 0.5, "per_level_above_first": 0 }  // 可选，默认恒定 0.5（+50%），
 *                      //    支持等级函数（linear/lookup/constant）；破除/到期时随追踪器一并移除
 * </pre>
 */
public record InstantInvisibilityTrackerEffect(LevelBasedValue perLevelDuration, int radius, LevelBasedValue attackDamageBonus) implements AbilityEntityEffect {
    public static final MapCodec<InstantInvisibilityTrackerEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("per_level_duration").forGetter(InstantInvisibilityTrackerEffect::perLevelDuration),
            Codec.INT.optionalFieldOf("radius", 25).forGetter(InstantInvisibilityTrackerEffect::radius),
            LevelBasedValue.CODEC.optionalFieldOf("attack_damage_bonus", LevelBasedValue.constant(0.5F)).forGetter(InstantInvisibilityTrackerEffect::attackDamageBonus)
    ).apply(instance, InstantInvisibilityTrackerEffect::new));

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity target) {
        // 结算只关心施法者本人（本效果应位于 self 目标选择中），target 参数忽略
        int ticks = (int) perLevelDuration.calculate(ability.level());
        float attackBonus = attackDamageBonus.calculate(ability.level());
        InstantInvisibilityEventHandler.startTracking(dragon, ticks, radius, attackBonus);
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }

    // 技能侧边栏描述（self 目标）：提及随 radius 字段可变的生效距离与按等级函数计算的「破隐一击」伤害加成数值
    @Override
    public @NotNull @Unmodifiable List<MutableComponent> getDescription(final Player dragon, final @NotNull DragonAbilityInstance ability) {
        int level = ability.level();
        Component effectiveRadius = DSColors.dynamicValue(radius);
        String bonusPercent = Math.round(attackDamageBonus.calculate(level) * 100.0F) + "%";
        Component bonus = DSColors.dynamicValue(bonusPercent);

        MutableComponent description = Component.translatable("wing_kirin.ability.instant_invisibility.tracker.description",
                effectiveRadius, bonus);

        return List.of(description);
    }
}
