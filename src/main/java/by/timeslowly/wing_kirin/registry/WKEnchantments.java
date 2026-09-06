package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.enchantment.UnencumberedEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 附魔注册（自 1.21.1 NeoForge 分支移植）。
 * 1.21 起附魔支持数据包 JSON 定义，1.20.1 仅能用代码注册；
 * 1.20.1 无 Enchantment.definition 构建器，等级/费用/适用物品等定义改由 UnencumberedEnchantment 覆写方法提供。
 * 1.21.1 中 tradeable / in_enchanting_table 两个附魔标签在 1.20.1 无对应机制：
 * 非宝藏附魔默认可交易、可被发现（isTradeable/isDiscoverable 默认 true），行为一致，无需额外处理。
 */
public class WKEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(
            ForgeRegistries.ENCHANTMENTS, WingKirin.MODID
    );

    // 轻钟上阵：减轻金钟重量，逐级恢复移速/龙飞行速度/重力（属性效果实现于 GoldenBellItem）
    public static final RegistryObject<Enchantment> UNENCUMBERED = ENCHANTMENTS.register("unencumbered", UnencumberedEnchantment::new);

    // TODO:定身抗性（dingshen_resistance）未随本次移植：1.21.1 中其注册代码为占位定义（supportedItems 为钻石剑占位，无数据包 JSON），
    //  实际效果依赖尚未移植的定身（DING_SHEN）药水效果，待定身效果移植时一并补上。

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
