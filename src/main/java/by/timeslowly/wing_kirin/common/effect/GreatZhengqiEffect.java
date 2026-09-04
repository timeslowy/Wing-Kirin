package by.timeslowly.wing_kirin.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * 浩然正气药水效果（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * 1.21.1 中"翼麒麟龙玩家无视法力消耗"的行为由 {@code ManaHandlerMixin} 注入
 * DS 的 ManaHandler 实现；该 mixin 依赖 WKServerConfig 配置项，
 * 1.20.1 分支暂未移植 mixin，故本效果当前为无行为的占位效果本体。
 */
public class GreatZhengqiEffect extends MobEffect {
    public GreatZhengqiEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
