package by.timeslowly.wing_kirin.common.effect;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

// 从天而降药水效果
public class MaceCrushEffect extends MobEffect {
    /**
     * 修改重锤猛击伤害的逻辑见： {@link by.timeslowly.wing_kirin.common.eventhandler.AttributeEventHandler}
     */
    public MaceCrushEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(DSAttributes.ARMOR_IGNORE_CHANCE,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_1"), 0.1,
                AttributeModifier.Operation.ADD_VALUE
        );
        // 主要属性：下落猛击倍率
        this.addAttributeModifier(WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_2"), 1.0,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        this.addAttributeModifier(Attributes.GRAVITY,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_3"), 0.03,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    // 「不可被常规手段治愈」：旧版通过 NeoForge EffectCure（fillEffectCures 清空）实现，
    // 26.1 该机制被整体移除后改由治愈物品的 ConsumeEffect Mixin 拦截实现，
    // 判定逻辑见 {@link by.timeslowly.wing_kirin.registry.WKEffects#isCureImmune}。
}

