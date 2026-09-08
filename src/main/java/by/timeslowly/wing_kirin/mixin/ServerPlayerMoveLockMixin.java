package by.timeslowly.wing_kirin.mixin;

import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 定身锁位：拦截 ServerPlayer 的同维度传送（自 1.21.1 平移；
 * 1.20.1 的 ServerPlayer 同样覆写了 teleportTo(DDD)V，已对源码验证）。
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMoveLockMixin {

    /**
     * ServerPlayer 重写了 teleportTo(DDD)V，不调用 super 而是直接通过 connection 发包，
     * 因此需要单独拦截。
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockTeleport(double x, double y, double z, CallbackInfo ci) {
        if (ConfigSyncHandler.dingShenLockPosition() && ((ServerPlayer) (Object) this).hasEffect(WKEffects.DING_SHEN.get())) {
            ci.cancel();
        }
    }
}
