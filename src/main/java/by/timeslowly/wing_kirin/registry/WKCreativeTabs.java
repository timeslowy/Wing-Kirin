package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 创造模式物品栏（自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 Forge 的 Output 无 accept(Supplier) 重载，改用 .get() 传入。
 */
public class WKCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            // 全限定引用主类常量：本类字段名 WingKirin（同 1.21.1 命名）会遮蔽主类名
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, by.timeslowly.wing_kirin.WingKirin.MODID);
    // 在创造模式标签页注册物品，且按顺序
    public static final RegistryObject<CreativeModeTab> WingKirin = CREATIVE_MODE_TAB.register("wing_kirin",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(WKItems.WingKirinFullIconItem.get()))
                    .title(Component.translatable("item_group.wing_kirin.wing_kirin"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(WKItems.EmpyreanWine.get());
                        output.accept(WKItems.EmpyreanMilkwine.get());
                        output.accept(WKItems.GoldenBell.get());
                        output.accept(WKItems.WingKirinHeart.get());
                        output.accept(WKItems.WingKirinTreat.get());
                        output.accept(WKItems.EmpyreanEssence.get());
                    })).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
