package by.timeslowly.wing_kirin.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EmpyreanWineItem extends Item {
    // 设置堆叠数、饥饿值和饱和度
    public EmpyreanWineItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .fast()
                        .saturationModifier(2f)
                        .effect(() -> new MobEffectInstance
                                (MobEffects.REGENERATION, 200, 1,
                                        false, false), 1.0f)
                        .alwaysEdible()
                        .usingConvertsTo(Items.GLASS_BOTTLE)
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
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_wine.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.empyrean_wine.description_1"));
    }

}
