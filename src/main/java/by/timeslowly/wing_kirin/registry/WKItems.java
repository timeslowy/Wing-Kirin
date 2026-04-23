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
    // 金风玉露
    public static final DeferredItem<Item> EmpyreanWine = ITEMS.register("empyrean_wine", EmpyreanWineItem::new);
    // 金钟
    public static final DeferredItem<Item> GoldenBell = ITEMS.register("golden_bell", GoldenBellItem::new);
    // 奶露之合
    public static final DeferredItem<Item> EmpyreanMilkwine = ITEMS.register("empyrean_milkwine", EmpyreanMilkwineItem::new);
    // 翼麒麟的佳肴
    public static final DeferredItem<Item> WingKirinTreat = ITEMS.register("wing_kirin_treat", WingKirinTreatItem::new);
    // 通仙心
    public static final DeferredItem<Item> WingKirinHeart = ITEMS.register("wing_kirin_upgrade", WingKirinHeartItem::new);
    // （没想好叫什么）
    public static final  DeferredItem<Item> EmpyreanEssence = ITEMS.register("empyrean_essence", EmpyreanEssenceItem::new);

    // 远古翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinFullIconItem = ITEMS.register("wing_kirin_full_icon",
            () -> new Item(new Item.Properties()));
    // 新生翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinIconItem = ITEMS.register("wing_kirin_icon",
            () -> new Item(new Item.Properties()));
    // 浩然正气效果图标（空物品）
    public static final DeferredItem<Item> GreatZhengqiIconItem = ITEMS.register("great_zhengqi_icon",
            () -> new Item(new Item.Properties()));
    // 麒麟之翼图标（空物品）
    public static final DeferredItem<Item> FlyHigherIconItem = ITEMS.register("fly_higher_icon",
            () -> new Item(new Item.Properties()));
    // 定身药水效果图标（空物品）
    public static final DeferredItem<Item> DingshenIconItem = ITEMS.register("dingshen_icon",
            () -> new Item(new Item.Properties()));
    // 「唯快不破」进度图标（空图标）
    public static final DeferredItem<Item> UnstoppableSpeedIcon = ITEMS.register("unstoppable_speed_icon",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
