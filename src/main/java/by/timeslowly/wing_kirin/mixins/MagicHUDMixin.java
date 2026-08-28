package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    @Final
    @Shadow
    private static MagicHUD.OutlineColorData[] colors;

    /**
     * 技能图标边框调整
     */
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;lerpToColor(II)V",
            ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void applyDingShenBorder(GuiGraphicsExtractor graphics, DeltaTracker tracker, CallbackInfo ci,
                                            @Local(name = "x") int x,
                                            @Local(name = "ability") DragonAbilityInstance ability) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ability == null) return;

        // 若有定身效果且配置启用禁用技能，则覆盖灰色遮罩
        if (WKServerConfig.shouldDingShenDisableAbilities() && player.hasEffect(WKEffects.DING_SHEN) && ability.value().activation().type() != Activation.Type.PASSIVE) {
            MagicHUD.OutlineColorData data = colors[x];
            // 灰色半透明 (0.3, 0.3, 0.3, 0.7) —— 26.1 起 OutlineColorData.color 为 int ARGB
            ((OutlineColorDataAccessor) data).setColor(0xB34C4C4C);
            ((OutlineColorDataAccessor) data).setPastDelay(false);
        }
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