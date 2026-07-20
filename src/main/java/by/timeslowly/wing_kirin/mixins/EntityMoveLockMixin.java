package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMoveLockMixin {

    /**
     * 拦截所有基于物理的移动（行走、游泳、击退、活塞推动等）。
     * void move(MoverType, Vec3)
     */
    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockMove(MoverType type, Vec3 delta, CallbackInfo ci) {
        if (wingKirin$isDingShenLocked()) {
            ci.cancel();
        }
    }

    /**
     * 拦截同维度传送（末影珍珠、紫颂果、/tp 命令、mod 传送物品等）。
     * void teleportTo(double, double, double)
     */
    @Inject(method = "teleportTo(DDD)V", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockTeleport(double x, double y, double z, CallbackInfo ci) {
        if (wingKirin$isDingShenLocked()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean wingKirin$isDingShenLocked() {
        if (!WKServerConfig.shouldDingShenLockPosition()) return false;
        Entity self = (Entity) (Object) this;
        return self instanceof LivingEntity living && living.hasEffect(WKEffects.DING_SHEN);
    }
}
