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

    /**
     * 定身药水效果抗性属性，默认0.0，范围 0~1。
     * 1.21.1 使用 NeoForge 专有的 PercentageAttribute（1.20.1 Forge 无此类），改用 RangedAttribute
     * 手动限幅（显示为数值而非百分比，仅展示差异，语义一致）。
     * 应用逻辑见 {@link by.timeslowly.wing_kirin.mixin.LivingEntityEffectMixin}（按抗性比例减少定身效果时长），
     * 附魔等级 → 属性的施加见 {@link by.timeslowly.wing_kirin.common.eventhandler.effects.DingshenEffectEventHandler}。
     */
    public static final RegistryObject<Attribute> DINGSHEN_EFFECT_RESISTANCE =
            ATTRIBUTES.register("dingshen_effect_resistance",
                    () -> new RangedAttribute("attribute.name.wing_kirin.dingshen_effect_resistance",
                            0.0,
                            0.0,
                            1.0)
                            .setSyncable(true));

    // 注册属性（给玩家实体挂载）
    @SubscribeEvent
    public static void modifyEntityAttributes(@NotNull EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, WKAttributes.SONIC_BOOM_DAMAGE_MULTIPLIER.get());
        event.add(EntityType.PLAYER, WKAttributes.DINGSHEN_EFFECT_RESISTANCE.get());
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
