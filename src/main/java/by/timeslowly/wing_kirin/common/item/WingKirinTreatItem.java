package by.timeslowly.wing_kirin.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 翼麒麟的佳肴（自 1.21.1 NeoForge 分支移植，仅 tooltip 签名适配 1.20.1）。
 */
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
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_0"));
        tooltipComponents.add(Component.translatable("item.wing_kirin.wing_kirin_treat.description_1"));
    }
}
