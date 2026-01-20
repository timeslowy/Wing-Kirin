package by.timeslowly.wing_kirin.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WingKirinTreat extends Item {
    public WingKirinTreat() {
        super(new Properties().
                rarity(Rarity.UNCOMMON)
        );
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, components, tooltipFlag);
        components.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_0"));
        components.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_1"));
    }
}
