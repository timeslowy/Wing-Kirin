package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMoveLockMixin {

    /**
     * ServerPlayer 重写了 teleportTo(DDD)V，不调用 super 而是直接通过 connection 发包，
     * 因此需要单独拦截。
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockTeleport(double x, double y, double z, CallbackInfo ci) {
        if (WKServerConfig.shouldDingShenLockPosition() && ((ServerPlayer) (Object) this).hasEffect(WKEffects.DING_SHEN)) {
            ci.cancel();
        }
    }
}
