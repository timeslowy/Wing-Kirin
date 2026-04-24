package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.handlers.magic.ClientCastingHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientCastingHandler.class, remap = false)
public abstract class ClientCastingHandlerMixin {

    /**
     * 在 beginCast 中拦截，避免定身状态下出现施法条
     */
    @Inject(method = "beginCast", at = @At("HEAD"), cancellable = true, remap = false)
    private static void blockBeginCastWhenDingShen(@NotNull Player player, InputConstants.Key input, CallbackInfo ci) {
        if (player.hasEffect(WKEffects.DING_SHEN)) {
            // 可选的提示信息
            player.displayClientMessage(Component.translatable("actionbar.wing_kirin.ability.stasis_hex.disable_ability"), true);
            ci.cancel();
        }
    }
}