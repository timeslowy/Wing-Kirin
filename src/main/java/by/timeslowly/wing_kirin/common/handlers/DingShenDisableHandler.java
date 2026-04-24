package by.timeslowly.wing_kirin.common.handlers;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.network.magic.SyncDisableAbility;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class DingShenDisableHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.@NotNull Added event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance.getEffect().value() == WKEffects.DING_SHEN.value()) {
                setAllActiveAbilitiesDisabled(player, true);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.@NotNull Remove event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance != null && instance.getEffect().value() == WKEffects.DING_SHEN.value()) {
                setAllActiveAbilitiesDisabled(player, false);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.@NotNull Expired event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MobEffectInstance instance = event.getEffectInstance();
            if (instance != null && instance.getEffect().value() == WKEffects.DING_SHEN.value()) {
                setAllActiveAbilitiesDisabled(player, false);
            }
        }
    }

    private static void setAllActiveAbilitiesDisabled(ServerPlayer player, boolean disabled) {
        if (!DragonStateProvider.isDragon(player)) {
            return;
        }

        MagicData magic = MagicData.getData(player);
        magic.getAbilities().forEach((key, ability) -> {
            // 只处理主动技能
            if (ability.value().activation().type() != Activation.Type.PASSIVE) {
                // 调用原版方法，并手动同步到客户端（模拟原版自动禁用逻辑）
                ability.setDisabled(player, disabled, false); // false 表示自动禁用
                PacketDistributor.sendToPlayer(player, new SyncDisableAbility(ability.key(), disabled, false));
            }
        });
    }
}