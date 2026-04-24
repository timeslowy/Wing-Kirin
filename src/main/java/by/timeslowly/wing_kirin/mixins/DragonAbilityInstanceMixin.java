package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DragonAbilityInstance.class, remap = false)
public abstract class DragonAbilityInstanceMixin {

    @Shadow
    private boolean isActive;

    @Shadow
    private void stopCasting(Player dragon, boolean notifyServer) {}

    /**
     * 阻止在定身状态下激活主动技能
     */
    @Inject(method = "setActive", at = @At("HEAD"), cancellable = true, remap = false)
    private void wingKirin$preventActivation(Player player, boolean isActive, CallbackInfo ci) {
        if (isActive && wingKirin$shouldBlock(player)) {
            ci.cancel();
        }
    }

    /**
     * 如果已经激活的主动技能在定身效果下，立刻强制停止
     */
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void wingKirin$stopActiveWhenDingShen(Player dragon, CallbackInfo ci) {
        if (isActive && wingKirin$shouldBlock(dragon)) {
            stopCasting(dragon, true);
        }
    }

    /**
     * 判断逻辑：非被动技能 且 玩家拥有定身效果
     */
    @Unique
    private boolean wingKirin$shouldBlock(Player player) {
        // 获取技能的类型（被动/主动）
        // 注意：value() 是目标类已有的 public 方法，可直接调用，无需 @Shadow
        if (((DragonAbilityInstance) (Object) this).value().activation().type() == Activation.Type.PASSIVE) {
            return false;
        }
        return player.hasEffect(WKEffects.DING_SHEN);
    }
}