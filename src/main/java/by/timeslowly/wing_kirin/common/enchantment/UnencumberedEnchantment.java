package by.timeslowly.wing_kirin.common.enchantment;

import by.timeslowly.wing_kirin.common.item.GoldenBellItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

/**
 * 轻钟上阵（自 1.21.1 数据包定义移植为 Java 实现）。
 * <p>
 * 1.21 起附魔改为数据包 JSON 定义（data/wing_kirin/enchantment/unencumbered.json），1.20.1 需用代码注册并覆写相应方法。
 * 原数据包定义：
 * <ul>
 *   <li>weight 10（对应 {@link Rarity#COMMON}）、max_level 4</li>
 *   <li>min_cost：base 1 / per_level_above_first 5；max_cost：base 10 / per_level_above_first 5</li>
 *   <li>anvil_cost 8（1.21 新增配置项，1.20.1 无对应机制，铁砧费用由稀有度推导）</li>
 *   <li>slots [mainhand]；supported_items / primary_items = wing_kirin:golden_bell（单物品引用）</li>
 * </ul>
 * 属性效果（移速/龙飞行速度/重力的逐级恢复）见 {@link GoldenBellItem#getAttributeModifiers}。
 */
public class UnencumberedEnchantment extends Enchantment {
    public UnencumberedEnchantment() {
        // category 仅作占位：适用性判断已被下方 canApplyAtEnchantingTable 完全接管（BREAKABLE 泛指有耐久物品）
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMaxLevel() {
        return 4;
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

    // supported_items = 金钟（单物品）。附魔台与铁砧两条路径最终都会走到本方法
    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return stack.getItem() instanceof GoldenBellItem;
    }
}
