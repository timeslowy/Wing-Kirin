package by.timeslowly.wing_kirin.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// 浩然正气药水效果
public class GreatZhengqiEffect extends MobEffect {
    /**
     * 修改法力消耗的逻辑见： {@link by.timeslowly.wing_kirin.mixins.ManaHandlerMixin}
     */
    public GreatZhengqiEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
