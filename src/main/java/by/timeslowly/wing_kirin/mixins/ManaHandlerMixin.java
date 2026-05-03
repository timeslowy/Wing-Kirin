package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ManaHandler.class, remap = false)
public abstract class ManaHandlerMixin {

    // 判断是否拥有足够法力
    @Inject(method = "hasEnoughMana", at = @At("HEAD"), cancellable = true, remap = false)
    private static void additionalFreeManaCheck(Player player, float manaCost, CallbackInfoReturnable<Boolean> cir) {
        if (shouldIgnoreManaCost$wingkirin(player)) {
            cir.setReturnValue(true);
        }
    }

    // 判断是否要消耗法力
    @Inject(method = "consumeMana", at = @At("HEAD"), cancellable = true, remap = false)
    private static void skipManaConsumption(Player player, float manaCost, CallbackInfo ci) {
        if (shouldIgnoreManaCost$wingkirin(player)) {
            ci.cancel();
        }
    }

    // 核心方法
    @Unique
    private static boolean shouldIgnoreManaCost$wingkirin(Player player) {
        // 是否启用浩然正气无视法力消耗配置
        if (!WKServerConfig.shouldGreatZhengqiIgnoreManaCost()) {
            return false;
        }
        // 是否为龙玩家
        if (!DragonStateProvider.isDragon(player)) {
            return false;
        }
        Holder<DragonSpecies> species = DragonStateProvider.getData(player).species();
        if (species == null) {
            return false;
        }
        // 是否为翼麒麟龙玩家
        if (!species.is(DragonSurvival.res("wing_kirin"))) {
            return false;
        }
        // 是否拥有「浩然正气」药水效果
        return player.hasEffect(WKEffects.GREAT_ZHENGQI);
    }
}