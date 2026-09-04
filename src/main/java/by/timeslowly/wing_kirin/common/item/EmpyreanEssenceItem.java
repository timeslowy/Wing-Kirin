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
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 天仙玉露（自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 的 FoodProperties.Builder 无 usingConvertsTo，返还空瓶由 finishUsingItem 实现。
 */
public class EmpyreanEssenceItem extends Item {
    public EmpyreanEssenceItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.EPIC)
                .food(new FoodProperties.Builder()
                        .nutrition(16)
                        .saturationMod(2f)
                        .effect(() -> new MobEffectInstance
                                (MobEffects.REGENERATION, 400, 5,
                                        false, false), 1.0f)
                        .fast()
                        .alwaysEat()
                        .build()));
    }

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

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_essence.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_essence.description_1"));
    }

    // 吃完返还玻璃瓶（对应 1.21.1 的 usingConvertsTo(Items.GLASS_BOTTLE)）
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return result;
    }
}
