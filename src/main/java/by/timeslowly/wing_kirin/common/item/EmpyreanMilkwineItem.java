package by.timeslowly.wing_kirin.common.item;

import by.timeslowly.wing_kirin.client.ClientHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class EmpyreanMilkwineItem extends Item {
    // 设置堆叠数、饥饿值和稀有度
    public EmpyreanMilkwineItem(Identifier id) {
        // 26.1 起 Item.Properties 必须携带注册 id；食用需要显式的 Consumable 组件
        // （0.8 秒快速饮用 = 原 fast() 标记），玻璃瓶转换回到 Item.Properties 上；
        // 移除负面效果改用 26.1 原生的 RemoveStatusEffectsConsumeEffect 声明式注册
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
                                .onConsume(new RemoveStatusEffectsConsumeEffect(
                                        HolderSet.direct(NEGATIVE_EFFECTS.toArray(new Holder[0]))))
                                .build())
                .usingConvertsTo(Items.GLASS_BOTTLE));
    }
    // 负面效果列表（26.1 起部分效果常量更名）
    private static final List<Holder<MobEffect>> NEGATIVE_EFFECTS = List.of(
            MobEffects.POISON,          // 中毒
            MobEffects.WEAKNESS,        // 虚弱
            MobEffects.WITHER,          // 凋零
            MobEffects.MINING_FATIGUE,  // 挖掘疲劳
            MobEffects.NAUSEA,          // 反胃
            MobEffects.BLINDNESS,       // 失明
            MobEffects.DARKNESS,        // 黑暗
            MobEffects.SLOWNESS         // 缓慢
    );

    // 使其附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        tooltipComponents.accept(Component.translatable("item.wing_kirin.empyrean_milkwine.description_0"));
        // Shift描述
        if (ClientHelper.SHIFT_DOWN.getAsBoolean())
            tooltipComponents.accept(Component.translatable("item.wing_kirin.empyrean_milkwine.shift_down"));
        else
            tooltipComponents.accept(Component.translatable("item.wing_kirin.shift_up"));

    }

}
