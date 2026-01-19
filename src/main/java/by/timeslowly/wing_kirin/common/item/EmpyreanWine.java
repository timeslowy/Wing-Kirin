package by.timeslowly.wing_kirin.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmpyreanWine extends Item {
    // 设置堆叠数、饥饿值和饱和度
    public EmpyreanWine() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.RARE)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(2f)
                        .alwaysEdible()
                        .build()));
    }
    // 使用饮用动画
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }
    // 设置食用时间
    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 24;
    }
    // 使其附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_wine.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_wine.description_1"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        // 应用效果
        if (entity instanceof LivingEntity && level.isClientSide())
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, false));

        // 处理食物效果 and 减少物品数量
        stack = super.finishUsingItem(stack, level, entity);

        // 返还玻璃瓶
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }

        return stack;
    }
}
