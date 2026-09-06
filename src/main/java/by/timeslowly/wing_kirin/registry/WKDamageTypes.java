package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

/**
 * 自定义伤害类型的注册表键（自 1.21.1 分支移植，改用 ResourceKey 常量）。
 * <p>
 * damage_type 是纯数据包注册表（1.20.1 同 1.21），JSON 已位于 data/wing_kirin/damage_type/；
 * 1.21.1 中用 DeferredRegister/DeferredHolder 的写法无法绑定到数据包注册表（见
 * DamageReflectionEventHandler 注释），故此处仅保留 {@link ResourceKey} 供代码引用。
 * 使用时经 {@code level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(...)}
 * 从世界的数据包注册表解析 Holder。
 */
public class WKDamageTypes {
    // 震伤（不坏金身反震）
    public static final ResourceKey<DamageType> COUNTER_SHOCK = key("counter_shock");

    public static final ResourceKey<DamageType> BREATH_DISAPPEAR = key("breath_disappear");

    public static final ResourceKey<DamageType> RAINSTORM_ARROW = key("rainstorm_arrow");

    public static final ResourceKey<DamageType> TRACE_DING = key("trace_ding");

    public static final ResourceKey<DamageType> WIND_DEVASTATE = key("wind_devastate");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(WingKirin.MODID, name));
    }
}
