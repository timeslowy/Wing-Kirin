package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// 注册物品
public class WKItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            Wing_kirin.MOD_ID
    );
    // 金风玉露 使用流程
    public static final DeferredItem<Item> EmpyreanWineItem = ITEMS.register("empyrean_wine", EmpyreanWine::new);
    // 金钟
    public static final DeferredItem<Item> GoldenBellItem = ITEMS.register("golden_bell", GoldenBell::new);
    // 奶露之合
    public static final DeferredItem<Item> EmpyreanMilkwineItem = ITEMS.register("empyrean_milkwine", EmpyreanMilkwine::new);
    // 翼麒麟的佳肴
    public static final DeferredItem<Item> WingKirinTreatItem = ITEMS.register("wing_kirin_treat", WingKirinTreat::new);
    // 通仙心
    public static final DeferredItem<Item> WingKirinHeartItem = ITEMS.register("wing_kirin_upgrade", WingKirinHeart::new);

    // 远古翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinFullIconItem = ITEMS.register("wing_kirin_full_icon",
            () -> new Item(new Item.Properties()));
    // 新生翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinIconItem = ITEMS.register("wing_kirin_icon",
            () -> new Item(new Item.Properties()));
    // 浩燃正气效果图标（空物品）
    public static final DeferredItem<Item> GreatZhengqiIconItem = ITEMS.register("great_zhengqi_icon",
            () -> new Item(new Item.Properties()));
    // 麒麟之翼图标（空物品）
    public static final DeferredItem<Item> FlyHigherIconItem = ITEMS.register("fly_higher_icon",
            () -> new Item(new Item.Properties()));
    // 定身药水效果图标（空物品）
    public static final DeferredItem<Item> DingshenIconItem = ITEMS.register("dingshen_icon",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
