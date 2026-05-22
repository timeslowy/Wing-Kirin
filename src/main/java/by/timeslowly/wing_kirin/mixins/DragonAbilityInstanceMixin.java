package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
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
     * 阻止在定身状态下激活技能（主动/被动均可拦截，取决于配置）。
     */
    @Inject(method = "setActive", at = @At("HEAD"), cancellable = true, remap = false)
    private void wingKirin$preventActivation(Player player, boolean isActive, CallbackInfo ci) {
        if (isActive && wingKirin$shouldBlockByType(player)) {
            ci.cancel();
        }
    }

    /**
     * 如果已经激活的技能在定身效果下，立刻强制停止。
     */
    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void wingKirin$stopActiveWhenDingShen(Player dragon, CallbackInfo ci) {
        if (isActive && wingKirin$shouldBlockByType(dragon)) {
            stopCasting(dragon, true);
        }
    }

    /**
     * 根据技能类型和对应配置决定是否阻止：
     * - 主动技能 → {@link WKServerConfig#shouldDingShenDisableAbilities()}
     * - 被动技能 → {@link WKServerConfig#shouldDingShenDisablePassiveAbilities()}
     * 两个配置互不干扰。
     */
    @Unique
    private boolean wingKirin$shouldBlockByType(@NotNull Player player) {
        if (!player.hasEffect(WKEffects.DING_SHEN)) {
            return false;
        }
        boolean isPassive = ((DragonAbilityInstance) (Object) this).value().activation().type() == Activation.Type.PASSIVE;
        if (isPassive) {
            return WKServerConfig.shouldDingShenDisablePassiveAbilities();
        }
        return WKServerConfig.shouldDingShenDisableAbilities();
    }
}