package by.timeslowly.wing_kirin.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EmpyreanWineItem extends Item {
    // 设置堆叠数、饥饿值和饱和度
    public EmpyreanWineItem(Identifier id) {
        // 26.1 起 Item.Properties 必须携带注册 id；食用需要显式的 Consumable 组件
        // （0.8 秒快速饮用 = 原 fast() 标记），玻璃瓶转换回到 Item.Properties 上；
        // 食用效果改用 26.1 原生的 ConsumeEffect（ApplyStatusEffectsConsumeEffect）声明式注册
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(16)
                .rarity(Rarity.UNCOMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(2f)
                        .alwaysEdible()
                        .build(),
                        Consumable.builder()
                                .consumeSeconds(0.8F)
                                .animation(ItemUseAnimation.DRINK)
                                .sound(SoundEvents.GENERIC_DRINK)
                                .onConsume(new ApplyStatusEffectsConsumeEffect(
                                        List.of(new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, false)), 1.0F))
                                .build())
                .usingConvertsTo(Items.GLASS_BOTTLE));
    }

    // 使其附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        tooltipComponents.accept(Component.translatable("item.wing_kirin.empyrean_wine.description_0"));
        tooltipComponents.accept(Component.translatable("item.wing_kirin.empyrean_wine.description_1"));
    }

}
