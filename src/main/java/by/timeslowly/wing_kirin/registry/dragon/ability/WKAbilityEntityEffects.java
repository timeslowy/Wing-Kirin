package by.timeslowly.wing_kirin.registry.dragon.ability;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects.DamageReflectionEffect;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 向 DragonSurvival 的 ability_entity_effect 静态注册表注册本模组的自定义实体效果类型。
 * <p>
 * 注册后即可在 dragon_ability JSON 中通过
 * {@code "effect_type": "wing_kirin:damage_reflection"} 使用。
 */
public class WKAbilityEntityEffects {
    public static void register(final @NotNull IEventBus modEventBus) {
        modEventBus.addListener(WKAbilityEntityEffects::registerEntries);
    }

    private static void registerEntries(final @NotNull RegisterEvent event) {
        // 伤害反弹：wing_kirin:damage_reflection
        event.register(AbilityEntityEffect.REGISTRY_KEY,
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, "damage_reflection"),
                () -> DamageReflectionEffect.CODEC);
    }
}