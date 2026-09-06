package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * 换位·就绪标记（{@code wing_kirin:transposition_mark}，无字段效果）。
 * <p>
 * 加在技能的 self 目标选择中：记录施法者当前坐标作为换位起点（瞬态 WeakHashMap），
 * 由 {@link TranspositionSwapEffect} 在视线目标上结算并清除此标记。
 * <p>
 * 移植说明：替代 1.21.1 的 {@code run_function -> ready.mcfunction}（命令存储存坐标 + 实体标签）。
 * ready.mcfunction 本身全静态可直接用，但换位的坐标由 Java 内存直取更精确（swap 的 apply 参数
 * 直接给出施法者），故标记一并 Java 化；命令存储与标签不再需要。
 */
public record TranspositionMarkEffect() implements AbilityEntityEffect {
    public static final TranspositionMarkEffect INSTANCE = new TranspositionMarkEffect();
    public static final MapCodec<TranspositionMarkEffect> CODEC = MapCodec.unit(INSTANCE);

    /** 施法者 -> 换位起点坐标（瞬态；swap 结算时取走，重复施法自然覆盖） */
    private static final Map<ServerPlayer, Vec3> READY_POSITIONS = new WeakHashMap<>();

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity target) {
        READY_POSITIONS.put(dragon, dragon.position());
    }

    /** 取走当前就绪坐标并清除标记（供 {@link TranspositionSwapEffect} 结算调用；无标记时返回 null） */
    public static Vec3 takeReady(final @NotNull ServerPlayer dragon) {
        return READY_POSITIONS.remove(dragon);
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
