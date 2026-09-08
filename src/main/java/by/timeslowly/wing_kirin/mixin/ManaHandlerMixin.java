package by.timeslowly.wing_kirin.mixin;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.common.handlers.magic.ManaHandler;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 「浩然正气」无视法力消耗（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * DS 1.20.1 的 ManaHandler 与 1.21.1 方法签名一致（已核实），Mixin 逻辑逐字保留；
 * 包名按 1.20.1 工作空间的惯例为单数 mixin。
 * <p>
 * 效果：翼麒麟龙玩家持有「浩然正气」效果且配置开启（默认开）时，
 * 施法不检查也不消耗法力（SERVER 配置项 great_zhengqi.ignoreManaCost；
 * 客户端经 by.timeslowly.wing_kirin.network.ConfigSyncHandler 读取服务端下发的快照）。
 */
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
        if (!ConfigSyncHandler.greatZhengqiIgnoreManaCost()) {
            return false;
        }
        // 是否为龙玩家
        if (!DragonStateProvider.isDragon(player)) {
            return false;
        }
        // 是否为翼麒麟龙玩家（DS 1.20.1 的 species() 同为 Holder<DragonSpecies> 体系）
        if (DragonStateProvider.getData(player).species() == null) {
            return false;
        }
        if (!DragonStateProvider.getData(player).species().is(DragonSurvival.res("wing_kirin"))) {
            return false;
        }
        // 是否拥有「浩然正气」药水效果（1.20.1 的 hasEffect 接收 MobEffect 实例）
        return player.hasEffect(WKEffects.GREAT_ZHENGQI.get());
    }
}
