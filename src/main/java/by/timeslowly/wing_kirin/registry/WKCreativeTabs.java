package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WKCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Wing_kirin.MOD_ID);
    // 在创造模式标签页注册物品，且按顺序
    public static final Supplier<CreativeModeTab> WingKirin = CREATIVE_MODE_TAB.register("wing_kirin",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(WKItems.WingKirinFullIconItem.get()))
                    .title(Component.translatable("item_group.wing_kirin.wing_kirin"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(WKItems.EmpyreanWineItem);
                        output.accept(WKItems.EmpyreanMilkwineItem);
                        output.accept(WKItems.GoldenBellItem);
                        output.accept(WKItems.WingKirinHeartItem);
                        output.accept(WKItems.WingKirinTreatItem);
                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
