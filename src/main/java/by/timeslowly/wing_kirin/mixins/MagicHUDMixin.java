package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
import software.bernie.geckolib.util.Color;

@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    @Final
    @Shadow
    private static MagicHUD.OutlineColorData[] colors;

    /**
     * 技能图标边框调整
     */
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;lerpToColor(ILsoftware/bernie/geckolib/util/Color;)V",
            ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void applyDingShenBorder(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci,
                                            @Local(name = "x") int x,
                                            @Local(name = "ability") DragonAbilityInstance ability) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ability == null) return;

        // 若有定身效果则覆盖灰色遮罩
        if (player.hasEffect(WKEffects.DING_SHEN) && ability.value().activation().type() != Activation.Type.PASSIVE) {
            MagicHUD.OutlineColorData data = colors[x];
            ((OutlineColorDataAccessor) data).setColor(Color.ofRGBA(0.3f, 0.3f, 0.3f, 0.7f));
            ((OutlineColorDataAccessor) data).setPastDelay(false);
        }
    }

    /**
     *  法力图标统一灰色
     */
    @ModifyArgs(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;blit(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIFFFF)V"),
            remap = false)
    private static void grayOutManaSprites(Args args) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.hasEffect(WKEffects.DING_SHEN)) {
            args.set(6, 0.3f); // red
            args.set(7, 0.3f); // green
            args.set(8, 0.3f); // blue
        }
    }
}