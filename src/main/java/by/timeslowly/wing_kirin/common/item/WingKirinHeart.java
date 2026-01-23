package by.timeslowly.wing_kirin.common.item;

import by.timeslowly.wing_kirin.registry.WKItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WingKirinHeart extends Item {
    public WingKirinHeart() {
        super(new Item.Properties()
                .rarity(Rarity.RARE)
        );
    }
    // 附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack itemstack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_1"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_2"));
        // Shift描述
        if (Screen.hasShiftDown())
            tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_upgrade.shift_down"));
        else
            tooltipComponents.add(Component.translatable("item.wing_kirin.shift_up"));
    }
    // 添加冷却
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        stack = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player _player)
            _player.getCooldowns().addCooldown(WKItems.WingKirinHeartItem.get(), 40);
        return stack;
    }
}
