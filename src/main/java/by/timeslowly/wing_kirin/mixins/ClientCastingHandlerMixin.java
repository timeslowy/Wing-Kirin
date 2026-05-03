package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientCastingHandler;
import by.dragonsurvivalteam.dragonsurvival.config.ClientConfig;
import by.dragonsurvivalteam.dragonsurvival.input.Keybind;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = ClientCastingHandler.class, remap = false)
public abstract class ClientCastingHandlerMixin {

    /**
     * 仅在按下当前施法键时才拦截并提示
     */
    @Inject(method = "beginCast", at = @At("HEAD"), cancellable = true, remap = false)
    private static void blockBeginCastWhenDingShen(@NotNull Player player, InputConstants.Key input, CallbackInfo ci) {
        if (player.hasEffect(WKEffects.DING_SHEN) && WKServerConfig.shouldDingShenDisableAbilities()) {
            MagicData magicData = MagicData.getData(player);
            int selectedSlot = magicData.getSelectedAbilitySlot();
            Keybind castKey = ClientConfig.alternateCastMode ? wingKirin$getSlotKeybind(selectedSlot) : Keybind.USE_ABILITY;
            // 匹配，拦截施法蓄力条出现
            if (castKey.matches(input)) {
                player.displayClientMessage(Component.translatable("actionbar.wing_kirin.ability.stasis_hex.disable_ability"), true);
                ci.cancel();
            }
        }
    }

    @Unique
    private static Keybind wingKirin$getSlotKeybind(int slot) {
        return switch (slot) {
            case 1 -> Keybind.ABILITY2;
            case 2 -> Keybind.ABILITY3;
            case 3 -> Keybind.ABILITY4;
            default -> Keybind.ABILITY1;
        };
    }
}