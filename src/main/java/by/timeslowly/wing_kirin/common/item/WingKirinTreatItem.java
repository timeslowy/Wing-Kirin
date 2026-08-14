package by.timeslowly.wing_kirin.common.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WingKirinTreatItem extends Item {
    public WingKirinTreatItem(Identifier id) {
        // 26.1 起 Item.Properties 必须携带注册 id
        super(new Properties().
                setId(ResourceKey.create(Registries.ITEM, id)).
                rarity(Rarity.RARE)
        );
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
        tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_treat.description_0"));
        tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_treat.description_1"));
    }
}
