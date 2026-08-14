package by.timeslowly.wing_kirin.common.item;

import by.timeslowly.wing_kirin.client.ClientHelper;
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

public class WingKirinHeartItem extends Item {
    public WingKirinHeartItem(Identifier id) {
        // 26.1 起 Item.Properties 必须携带注册 id；使用冷却改用原生 useCooldown（原 finishUsingItem 手动 addCooldown）
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .rarity(Rarity.EPIC)
                .useCooldown(20F)
        );
    }
    // 附魔发光
    @Override
    public boolean isFoil(@NotNull ItemStack itemstack) {
        return true;
    }
    // 添加描述
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipComponents, tooltipFlag);
        tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_0"));
        tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_1"));
        tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_upgrade.description_2"));
        // Shift描述
        if (ClientHelper.SHIFT_DOWN.getAsBoolean())
            tooltipComponents.accept(Component.translatable("item.wing_kirin.wing_kirin_upgrade.shift_down"));
        else
            tooltipComponents.accept(Component.translatable("item.wing_kirin.shift_up"));
    }
}
