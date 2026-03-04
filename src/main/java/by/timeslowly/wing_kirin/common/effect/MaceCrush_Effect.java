package by.timeslowly.wing_kirin.common.effect;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

// 从天而降药水效果
public class MaceCrush_Effect extends MobEffect {
    public MaceCrush_Effect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(DSAttributes.ARMOR_IGNORE_CHANCE,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_1"), 0.1,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    /**
     * 获取实体当前拥有的 MaceCrush 效果等级对应的下落距离倍率。
     * 如果实体没有此效果，返回 1.0f（无放大）。
     *
     * @param entity       要检查的实体
     * @param effectHolder 该效果的 Holder（可通过注册表获取）
     * @return 倍率值
     */
    public static float getMultiplier(LivingEntity entity, Holder<MobEffect> effectHolder) {
        MobEffectInstance instance = entity.getEffect(effectHolder);
        if (instance != null) {
            // amplifier 从 0 开始，等级 I 对应 amplifier = 0，加成倍率 = 等级 * 0.5
            return instance.getAmplifier() * 0.5f + 1.5f;
        }
        return 1.0f;
    }
}

