package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    /**
     * 技能图标边框调整。
     * <p>
     * DS 2.0.72 起，边框颜色的状态机（原 {@code render} 内的 {@code lerpToColor}）已被抽离到
     * {@code tickOutlineColors(ClientTickEvent.Post)}，{@code render} 改为调用 {@code interpolatedColor} 取色；
     * 26.1 起该方法返回类型由 GeckoLib 的 {@code Color} 改为 int ARGB。
     * 因此注入点由 {@code render#lerpToColor} 迁移至 {@code interpolatedColor} 头部，直接短路返回值：
     * 既覆盖逐槽边框（{@code render} 第 1 处调用），也覆盖选中槽高亮（第 2 处调用）。
     */
    @Inject(method = "interpolatedColor(IF)I", at = @At("HEAD"), cancellable = true, remap = false)
    private static void applyDingShenBorder(int slot, float partialTick, CallbackInfoReturnable<Integer> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (!WKServerConfig.shouldDingShenDisableAbilities() || !player.hasEffect(WKEffects.DING_SHEN)) return;

        DragonAbilityInstance ability = MagicData.getData(player).fromSlot(slot);
        if (ability == null) return;
        if (ability.value().activation().type() == Activation.Type.PASSIVE) return;

        // 灰色半透明 (0.3, 0.3, 0.3, 0.7) —— ARGB
        cir.setReturnValue(0xB34C4C4C);
    }

    /**
     *  法力图标统一灰色
     */
    @ModifyArgs(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;blit(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/resources/Identifier;IIIFFFF)V"),
            remap = false)
    private static void grayOutManaSprites(Args args) {
        Player player = Minecraft.getInstance().player;
        if (player != null && WKServerConfig.shouldDingShenDisableAbilities() && player.hasEffect(WKEffects.DING_SHEN)) {
            args.set(6, 0.3f); // red
            args.set(7, 0.3f); // green
            args.set(8, 0.3f); // blue
        }
    }
}
