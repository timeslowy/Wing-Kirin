package by.timeslowly.wing_kirin.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects.DamageReflectionEffect;
import by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects.PercentagedDamageEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 向 DragonSurvival 的 ability_entity_effect 静态注册表注册本模组的自定义实体效果类型。
 * <p>
 * 注册后即可在 dragon_ability JSON 中通过
 * {@code "effect_type": "wing_kirin:<效果id>"} 使用。
 * <p>
 * DS 1.20.1 的注册机制与 1.21.1 相同（{@code AbilityEntityEffect.registerEntries(RegisterEvent)}，
 * 见其字节码/文档），本类以同样的两步模式挂载：主类构造器 addListener + RegisterEvent 注册条目。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WKAbilityEntityEffects {
    public static void register(final @NotNull IEventBus modEventBus) {
        modEventBus.addListener(WKAbilityEntityEffects::registerEntries);
    }

    private static void registerEntries(final @NotNull RegisterEvent event) {
        if (!event.getRegistryKey().location().equals(AbilityEntityEffect.REGISTRY_KEY.location())) {
            return;
        }

        // 伤害反震：wing_kirin:damage_reflection
        event.register(AbilityEntityEffect.REGISTRY_KEY,
                new ResourceLocation(WingKirin.MODID, "damage_reflection"),
                () -> DamageReflectionEffect.CODEC);

        // 百分比伤害：wing_kirin:percentaged_damage
        event.register(AbilityEntityEffect.REGISTRY_KEY,
                new ResourceLocation(WingKirin.MODID, "percentaged_damage"),
                () -> PercentagedDamageEffect.CODEC);
    }
}
