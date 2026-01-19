package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.item.EmpyreanWine;
import by.timeslowly.wing_kirin.common.item.GoldenBell;
import by.timeslowly.wing_kirin.common.item.WingKirinTreat;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;
// 注册物品
public class WKItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
            Wing_kirin.MOD_ID
    );
    // 金风玉露 使用流程
    public static final DeferredItem<Item> EmpyreanWineItem = ITEMS.register("empyrean_wine", EmpyreanWine::new);
    // 金钟（暂时未增加属性）
    // TODO：增加物品组件属性修饰效果（见文档）
    public static final DeferredItem<Item> GoldenBellItem = ITEMS.register("golden_bell", GoldenBell::new);
    // 翼麒麟的佳肴
    public static final DeferredItem<Item> WingKirinTreatItem = ITEMS.register("wing_kirin_treat", WingKirinTreat::new);
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
    // 通仙心
    // TODO:增加shift物品描述（见文档）
    public static final DeferredItem<Item> WingKirinHeartItem = ITEMS.register("wing_kirin_upgrade",
            () -> new Item(new Item.Properties()));







    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
