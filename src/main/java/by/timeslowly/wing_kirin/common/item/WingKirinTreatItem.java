package by.timeslowly.wing_kirin.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WingKirinTreatItem extends Item {
    public WingKirinTreatItem() {
        super(new Properties().
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_1"));
    }
}
