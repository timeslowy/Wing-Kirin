package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// 注册物品
public class WKItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wing_kirin.MOD_ID);
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
    // 天仙玉露
    public static final  DeferredItem<Item> EmpyreanEssence = ITEMS.register("empyrean_essence", EmpyreanEssenceItem::new);

    // 26.1 起图标类空物品直接用官方 registerSimpleItem（自动设置注册 id）
    // 远古翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinFullIconItem = ITEMS.registerSimpleItem("wing_kirin_full_icon");
    // 新生翼麒麟头像（空物品）
    public static final DeferredItem<Item> WingKirinIconItem = ITEMS.registerSimpleItem("wing_kirin_icon");
    // 浩然正气效果图标（空物品）
    public static final DeferredItem<Item> GreatZhengqiIconItem = ITEMS.registerSimpleItem("great_zhengqi_icon");
    // 麒麟之翼图标（空物品）
    public static final DeferredItem<Item> FlyHigherIconItem = ITEMS.registerSimpleItem("fly_higher_icon");
    // 定身药水效果图标（空物品）
    public static final DeferredItem<Item> DingshenIconItem = ITEMS.registerSimpleItem("dingshen_icon");
    // 「唯快不破」进度图标（空物品）
    public static final DeferredItem<Item> UnstoppableSpeedIcon = ITEMS.registerSimpleItem("unstoppable_speed_icon");
    // 「壮士断腕」进度图标（空物品）
    public static final DeferredItem<Item> SpellBinderIcon = ITEMS.registerSimpleItem("spell_binder_icon");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
