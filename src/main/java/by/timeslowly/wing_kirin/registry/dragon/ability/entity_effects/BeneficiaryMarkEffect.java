package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 仁者无敌·受惠标记（{@code wing_kirin:beneficiary_mark}，无字段效果）。
 * <p>
 * 加在技能的 area 目标选择（allies_and_self）中，对每个被治疗的实体：
 * <ol>
 *   <li>为施法者的受惠计数 +1（瞬态 WeakHashMap，由 {@link BeneficiaryRewardEffect} 结算时取走清零）；</li>
 *   <li>若受惠实体为翼麒麟龙玩家，赠予 5 秒抗性提升Ⅴ。</li>
 * </ol>
 * <p>
 * 移植说明：替代 1.21.1 的 {@code run_function -> beneficiary.mcfunction}。
 * 仁者无敌的宏函数（动态时长）在 1.20.1 不可用，经用户确认采用「Java 接管」方案：
 * 计数与结算改为本效果 + {@link BeneficiaryRewardEffect}，原递归搜索/存储/宏授予函数链全部废弃。
 * 1.20.1 的 DS species 体系与 1.21.1 相同（speciesId() 可直接取 ID）。
 */
public record BeneficiaryMarkEffect() implements AbilityEntityEffect {
    public static final BeneficiaryMarkEffect INSTANCE = new BeneficiaryMarkEffect();
    public static final MapCodec<BeneficiaryMarkEffect> CODEC = MapCodec.unit(INSTANCE);

    /** 施法者 -> 本施法周期内已标记的受惠实体数（瞬态；施法与结算同刻完成，WeakHashMap 随实体回收） */
    private static final Map<ServerPlayer, Integer> BENEFICIARY_COUNTS = new WeakHashMap<>();

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity target) {
        BENEFICIARY_COUNTS.merge(dragon, 1, Integer::sum);

        // 翼麒麟龙玩家受惠时额外获得 5 秒抗性提升Ⅴ（原 beneficiary.mcfunction 的静态逻辑；
        // 对施法者自身也会先给 5 秒，随后被结算效果的长时抗性覆盖，与 1.21.1 行为一致）
        if (target instanceof ServerPlayer healed
                && DragonStateProvider.isDragon(healed)
                && DragonSurvival.res("wing_kirin").equals(DragonStateProvider.getData(healed).speciesId())) {
            healed.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5 * 20, 4));
        }
    }

    /** 取走当前受惠计数并清零（供 {@link BeneficiaryRewardEffect} 结算调用） */
    public static int takeCount(final @NotNull ServerPlayer dragon) {
        Integer count = BENEFICIARY_COUNTS.remove(dragon);
        return count == null ? 0 : count;
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
