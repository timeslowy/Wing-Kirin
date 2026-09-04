package by.timeslowly.wing_kirin.common.item;

import by.timeslowly.wing_kirin.client.ClientHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 奶露之合（自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 适配：FoodProperties.Builder 无 usingConvertsTo，返还空瓶由 finishUsingItem 实现；
 * MobEffects 为 MobEffect 类型（非 Holder），hasEffect/removeEffect 直接接受 MobEffect。
 */
public class EmpyreanMilkwineItem extends Item {
    // 设置堆叠数、饥饿值和稀有度
    public EmpyreanMilkwineItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationMod(2f)
                        .fast()
                        .alwaysEat()
                        .build()));
    }
    // 负面效果列表（1.20.1：MobEffects 常量即 MobEffect 类型）
    private static final List<MobEffect> NEGATIVE_EFFECTS = List.of(
            MobEffects.POISON,          // 中毒
            MobEffects.WEAKNESS,        // 虚弱
            MobEffects.WITHER,          // 凋零
            MobEffects.DIG_SLOWDOWN,    // 挖掘疲劳
            MobEffects.CONFUSION,       // 反胃
            MobEffects.BLINDNESS,       // 失明
            MobEffects.DARKNESS,        // 黑暗
            MobEffects.MOVEMENT_SLOWDOWN// 缓慢
    );

    // 使用饮用动画
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    // 使其附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_milkwine.description_0"));
        // Shift描述
        if (ClientHelper.SHIFT_DOWN.getAsBoolean())
            tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_milkwine.shift_down"));
        else
            tooltipComponents.add(Component.translatable("item.wing_kirin.shift_up"));

    }
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {

        // 调用父类方法处理基础食用效果
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        // 检查是否是玩家
        if (livingEntity instanceof Player player) {
            // 遍历所有负面效果
            for (MobEffect effect : NEGATIVE_EFFECTS) {
                // 如果玩家有该负面效果，则移除
                if (player.hasEffect(effect)) {
                    player.removeEffect(effect);
                }
            }
        }

        // 吃完返还玻璃瓶（对应 1.21.1 的 usingConvertsTo(Items.GLASS_BOTTLE)）
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return result;
    }

}
