package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.item.EmpyreanEssenceItem;
import by.timeslowly.wing_kirin.common.item.EmpyreanMilkwineItem;
import by.timeslowly.wing_kirin.common.item.EmpyreanWineItem;
import by.timeslowly.wing_kirin.common.item.WingKirinHeartItem;
import by.timeslowly.wing_kirin.common.item.WingKirinTreatItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 注册物品（自 1.21.1 NeoForge 分支移植）。
 * TODO:注：金钟（golden_bell/GoldenBellItem）按用户要求未随本次移植。
 * 1.20.1 Forge 无 DeferredRegister.createItems，改用 DeferredRegister.create(ForgeRegistries.ITEMS, ...)。
 */
public class WKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WingKirin.MODID);
    // 金风玉露
    public static final RegistryObject<Item> EmpyreanWine = ITEMS.register("empyrean_wine", EmpyreanWineItem::new);
    // 奶露之合
    public static final RegistryObject<Item> EmpyreanMilkwine = ITEMS.register("empyrean_milkwine", EmpyreanMilkwineItem::new);
    // 翼麒麟的佳肴
    public static final RegistryObject<Item> WingKirinTreat = ITEMS.register("wing_kirin_treat", WingKirinTreatItem::new);
    // 通仙心
    public static final RegistryObject<Item> WingKirinHeart = ITEMS.register("wing_kirin_upgrade", WingKirinHeartItem::new);
    // 天仙玉露
    public static final RegistryObject<Item> EmpyreanEssence = ITEMS.register("empyrean_essence", EmpyreanEssenceItem::new);

    // 远古翼麒麟头像（空物品）
    public static final RegistryObject<Item> WingKirinFullIconItem = ITEMS.register("wing_kirin_full_icon", () -> new Item(new Item.Properties()));
    // 新生翼麒麟头像（空物品）
    public static final RegistryObject<Item> WingKirinIconItem = ITEMS.register("wing_kirin_icon", () -> new Item(new Item.Properties()));
    // 浩然正气效果图标（空物品）
    public static final RegistryObject<Item> GreatZhengqiIconItem = ITEMS.register("great_zhengqi_icon", () -> new Item(new Item.Properties()));
    // 麒麟之翼图标（空物品）
    public static final RegistryObject<Item> FlyHigherIconItem = ITEMS.register("fly_higher_icon", () -> new Item(new Item.Properties()));
    // 定身药水效果图标（空物品）
    public static final RegistryObject<Item> DingshenIconItem = ITEMS.register("dingshen_icon", () -> new Item(new Item.Properties()));
    // 「唯快不破」进度图标（空物品）
    public static final RegistryObject<Item> UnstoppableSpeedIcon = ITEMS.register("unstoppable_speed_icon", () -> new Item(new Item.Properties()));
    // 「壮士断腕」进度图标（空物品）
    public static final RegistryObject<Item> SpellBinderIcon = ITEMS.register("spell_binder_icon", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
