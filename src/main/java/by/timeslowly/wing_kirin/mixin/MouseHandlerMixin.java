package by.timeslowly.wing_kirin.mixin;

import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 定身禁用视角旋转（自 1.21.1 移植）。
 * 1.20.1 适配：turnPlayer 无参（1.21.1 为 turnPlayer(double timeDelta)），注入签名对应调整。
 * 仅客户端：登记于 mixins.json 的 client 数组。
 * <p>
 * 配置读取：客户端一律走 {@link ConfigSyncHandler}（服务端下发的配置快照），
 * 不直接读 SERVER 类型的 {@code WKServerConfig}——后者在退出世界时已被卸载，dev 环境会抛
 * IllegalStateException（详见 ConfigSyncHandler 类注释）。
 */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void wing_kirin$blockTurnPlayer(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        // 配置启用且有定身效果则禁止视角旋转
        if (player != null && ConfigSyncHandler.dingShenDisableLookRotation() && player.hasEffect(WKEffects.DING_SHEN.get())) {
            ci.cancel();
        }
    }
}
