package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class WKEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, Wing_kirin.MOD_ID);

    // 占位的支持物品列表（任意物品即可，此处以钻石剑为例）
    @Contract(" -> new")
    private static @NotNull HolderSet<Item> placeholderSupportedItems() {
        return HolderSet.direct(Holder.direct(Items.DIAMOND_SWORD));
    }

    public static final DeferredHolder<Enchantment, Enchantment> DINGSHEN_RESISTANCE =
            ENCHANTMENTS.register("dingshen_resistance", () -> Enchantment.enchantment(
                    Enchantment.definition(
                            placeholderSupportedItems(),        // supportedItems
                            // primaryItems
                            10,                                 // weight
                            1,                                  // maxLevel
                            Enchantment.constantCost(1),        // minCost
                            Enchantment.constantCost(1),        // maxCost
                            0,                                  // anvilCost
                            EquipmentSlotGroup.ARMOR            // slots
                    )
            ).build(ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "dingshen_resistance")));

    public static final DeferredHolder<Enchantment, Enchantment> UNENCUMBERED =
            ENCHANTMENTS.register("unencumbered", () -> Enchantment.enchantment(
                    Enchantment.definition(
                            placeholderSupportedItems(),
                            10,
                            1,
                            Enchantment.constantCost(1),
                            Enchantment.constantCost(1),
                            0,
                            EquipmentSlotGroup.ARMOR
                    )
            ).build(ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "unencumbered")));

    
    
    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
