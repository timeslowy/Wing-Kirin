package by.timeslowly.wing_kirin.common.effect;

import by.dragonsurvivalteam.dragonsurvival.registry.DSAttributes;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

// 从天而降药水效果
public class MaceCrushEffect extends MobEffect {
    /**
     * 修改重锤猛击伤害的逻辑见： {@link by.timeslowly.wing_kirin.common.eventhandler.AttributeEventHandler}
     */
    public MaceCrushEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(DSAttributes.ARMOR_IGNORE_CHANCE,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_1"), 0.2,
                AttributeModifier.Operation.ADD_VALUE
        );
        // 主要属性：下落猛击倍率
        this.addAttributeModifier(WKAttributes.MACE_SMASH_DAMAGE_MULTIPLIER,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.mace_crush_2"), 0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
    }

    // 使其无法被常规手段消去
    @Override
    public void fillEffectCures(final @NotNull Set<EffectCure> cures, @NotNull final MobEffectInstance effectInstance) {
        cures.clear();
    }
}

