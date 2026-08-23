package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, Wing_kirin.MOD_ID);

    /** 失神药水：3 分钟 */
    public static final Holder<Potion> AMNESIA = POTIONS.register("amnesia",
            () -> new Potion("amnesia", new MobEffectInstance(WKEffects.AMNESIA, 3 * 60 * 20)));

    /**
     * 失神药水（长效）：8 分钟。
     * 原版方式：条目单独注册（如原版 long_swiftness），但药水 name 字段沿用基础名 "amnesia"，
     * 使长效版物品名解析到与基础版相同的语言键，无需额外注册语言键。
     */
    public static final Holder<Potion> LONG_AMNESIA = POTIONS.register("long_amnesia",
            () -> new Potion("amnesia", new MobEffectInstance(WKEffects.AMNESIA, 8 * 60 * 20)));

    // 酿造配方：粗制药水 + 紫颂果 → 失忆药水；失忆药水 + 红石 → 失神药水（长效）
    // 喷溅型/滞留型药水与失忆之箭随药水注册自动可用


    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
