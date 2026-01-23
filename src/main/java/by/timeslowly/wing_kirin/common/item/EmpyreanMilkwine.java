package by.timeslowly.wing_kirin.common.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmpyreanMilkwine extends Item {
    // 设置堆叠数、饥饿值和稀有度
    public EmpyreanMilkwine() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.RARE)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(2f)
                        .alwaysEdible()
                        .build()));
    }
    // 负面效果列表：中毒、虚弱、凋零、挖掘疲劳、反胃、失明和黑暗
    // 使用 Holder<MobEffect> 类型
    private static final Holder<MobEffect>[] NEGATIVE_EFFECTS = new Holder[]{
            MobEffects.POISON,          // 中毒
            MobEffects.WEAKNESS,        // 虚弱
            MobEffects.WITHER,          // 凋零
            MobEffects.DIG_SLOWDOWN,    // 挖掘疲劳
            MobEffects.CONFUSION,       // 反胃
            MobEffects.BLINDNESS,       // 失明
            MobEffects.DARKNESS         // 黑暗
    };

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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_milkwine.description_0"));
        // Shift描述
        if (Screen.hasShiftDown())
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
            for (Holder<MobEffect> effect : NEGATIVE_EFFECTS) {
                // 如果玩家有该负面效果，则移除
                if (player.hasEffect(effect)) {
                    player.removeEffect(effect);
                }
            }
        }
        // 返还玻璃瓶
        if (livingEntity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return result;
    }

}
