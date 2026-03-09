package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WKAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, Wing_kirin.MOD_ID);

    // 重锤下落猛击倍率属性，默认1.0，范围0~1024
    public static final DeferredHolder<Attribute, Attribute> MACE_SMASH_DAMAGE_MULTIPLIER =
            ATTRIBUTES.register("mace_smash_damage_multiplier",
                    () -> new RangedAttribute("attribute.name.wing_kirin.mace_smash_damage_multiplier",
                            1.0,
                            0.0,
                            1024.0)
                            .setSyncable(true));  //客户端是否自动同步

    // 音爆伤害倍率属性，默认1.0，范围0~1024
    public static final DeferredHolder<Attribute, Attribute> SONIC_BOOM_DAMAGE_MULTIPLIER =
            ATTRIBUTES.register("sonic_boom_damage_multiplier",
                    () -> new RangedAttribute("attribute.name.wing_kirin.sonic_boom_damage_multiplier",
                            1.0,
                            0.0,
                            1024.0)
                            .setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}