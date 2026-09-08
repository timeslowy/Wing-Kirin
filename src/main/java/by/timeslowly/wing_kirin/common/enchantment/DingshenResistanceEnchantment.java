package by.timeslowly.wing_kirin.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 定身抗性（自 1.21.1 数据包定义移植为 Java 实现）。
 * <p>
 * 原数据包定义（data/wing_kirin/enchantment/dingshen_resistance.json）：
 * <ul>
 *   <li>weight 10（对应 {@link Rarity#COMMON}）、max_level 5</li>
 *   <li>min_cost：base 1 / per_level_above_first 5；max_cost：base 10 / per_level_above_first 5</li>
 *   <li>anvil_cost 8（1.21 新增配置项，1.20.1 无对应机制，铁砧费用由稀有度推导）</li>
 *   <li>slots [armor]；supported_items = #minecraft:enchantable/armor</li>
 *   <li>属性效果：每级 +0.2 wing_kirin:dingshen_effect_resistance（add_value）</li>
 * </ul>
 * 1.20.1 无附魔属性效果组件，属性施加由
 * {@link by.timeslowly.wing_kirin.common.eventhandler.effects.DingshenEffectEventHandler} 的
 * LivingEquipmentChangeEvent / EntityJoinLevelEvent 订阅实现；
 * 属性减免定身时长的逻辑见 {@link by.timeslowly.wing_kirin.mixin.LivingEntityEffectMixin}。
 */
public class DingshenResistanceEnchantment extends Enchantment {
    public DingshenResistanceEnchantment() {
        // ARMOR 类别 + 四护甲槽，对应 supported_items #minecraft:enchantable/armor
        super(Rarity.COMMON, EnchantmentCategory.ARMOR,
                new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    // 1.21 数据包定义：min_cost base 1 / per_level_above_first 5
    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 5;
    }

    // 1.21 数据包定义：max_cost base 10 / per_level_above_first 5
    @Override
    public int getMaxCost(int level) {
        return 10 + (level - 1) * 5;
    }
}
