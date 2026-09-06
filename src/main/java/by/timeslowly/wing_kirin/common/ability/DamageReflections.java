package by.timeslowly.wing_kirin.common.ability;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 伤害反弹数据（仿 DS 的 OnAttackEffects 附件模式）。
 * <p>
 * 以技能实例 ID 为键保存各技能提供的反弹参数；由施法期间/被动技能周期性调用
 * {@link by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects.DamageReflectionEffect#apply} 刷新，因此无需序列化
 * （技能等级变化时也会随下一次刷新自动更新数值）。
 * <p>
 * 每条目记录写入时的游戏刻并设有有效期（{@link #REFLECTION_TTL_TICKS}）：
 * 主动技能施法结束、被动技能条件不满足后不再刷新，条目到期即失效，
 * 从而不依赖 DS 的 remove 回调（其仅对被动常驻 self 技能可靠）。
 * <p>
 * 附件替代说明：1.21.1 用 NeoForge 的实体 Data Attachment 承载本数据，1.20.1 无该机制
 * （DS 1.20.1 自带的 compat attachment 系统对本模组而言过重），改用弱键 WeakHashMap：
 * 实体被卸载/死亡回收后其数据随弱键自动回收，存活期间的过期条目由 {@link #active(long)} 清理。
 * 仅服务端读写，无需同步。
 */
public class DamageReflections {
    /**
     * 条目有效期（游戏刻，1 秒 = 20）。需大于技能配置的 trigger_rate 节拍间隔，
     * 否则刷新间隙条目会短暂失效导致反伤闪烁；技能结束后反伤最多残留该时长。
     */
    public static final long REFLECTION_TTL_TICKS = 100;

    /** 附件替代：弱键 Map，键为实体（仅服务端存取） */
    private static final Map<Entity, DamageReflections> DATA = new WeakHashMap<>();

    private final Map<String, Entry> entries = new HashMap<>();

    /** 单个技能提供的反弹参数（均已按当前技能等级计算并钳制为非负值），appliedTick 为写入时的游戏刻 */
    public record Entry(float percentage, int range, boolean useSameDamageType, long appliedTick) {}

    public static @NotNull DamageReflections getData(final @NotNull Entity entity) {
        return DATA.computeIfAbsent(entity, key -> new DamageReflections());
    }

    public void set(final String abilityId, final Entry entry) {
        entries.put(abilityId, entry);
    }

    public void remove(final String abilityId) {
        entries.remove(abilityId);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /**
     * 返回当前未过期的条目，并顺带清理已过期条目（技能结束后数据不会残留）。
     *
     * @param nowTick 当前游戏刻（{@code level().getGameTime()})
     */
    public @NotNull Collection<Entry> active(final long nowTick) {
        entries.entrySet().removeIf(entry -> nowTick - entry.getValue().appliedTick() > REFLECTION_TTL_TICKS);
        return entries.values();
    }
}
