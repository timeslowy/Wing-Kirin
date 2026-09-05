package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * 自定义属性注册（自 1.21.1 NeoForge 分支移植）。
 * 1.20.1 Forge 无 Registries.ATTRIBUTE 常量，改用 ForgeRegistries.ATTRIBUTES；
 * EntityAttributeModificationEvent 在 1.20.1 Forge 是 MOD 总线事件，需显式指定 Bus.MOD。
 * <p>
 * 重锤猛击倍率和音爆伤害倍率的应用逻辑见： {@link by.timeslowly.wing_kirin.common.eventhandler.AttributeEventHandler}
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WKAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
            ForgeRegistries.ATTRIBUTES, WingKirin.MODID
    );

    // 重锤下落猛击倍率属性，默认1.0，范围 0~1024 我去，1.20.1没有重锤！！

    // 音爆伤害倍率属性，默认1.0，范围0~1024
    public static final RegistryObject<Attribute> SONIC_BOOM_DAMAGE_MULTIPLIER =
            ATTRIBUTES.register("sonic_boom_damage_multiplier",
                    () -> new RangedAttribute("attribute.name.wing_kirin.sonic_boom_damage_multiplier",
                            1.0,
                            0.0,
                            1024.0)
                            .setSyncable(true));//客户端是否自动同步

    // TODO:定身药水效果抗性（dingshen_effect_resistance，默认0.0，范围0~1）按用户要求暂不移植。
    //  1.21.1 中该属性使用 NeoForge 专有的 PercentageAttribute（1.20.1 Forge 无此类，需用 RangedAttribute 手动实现百分比），
    //  其应用逻辑位于 1.21.1 的 LivingEntityEffectMixin（按抗性比例减少定身效果时长），
    //  且依赖尚未移植的定身（DING_SHEN）药水效果，待定身效果移植时一并补上。

    // 注册属性（给玩家实体挂载）
    @SubscribeEvent
    public static void modifyEntityAttributes(@NotNull EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER.get());
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
