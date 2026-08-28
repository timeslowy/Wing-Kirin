package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockTurnPlayer(double timeDelta, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        // 配置启用且有定身效果则禁止视角旋转
        if (player != null && WKServerConfig.shouldDingShenDisableLookRotation() && player.hasEffect(WKEffects.DING_SHEN)) {
            ci.cancel();
        }
    }
}
