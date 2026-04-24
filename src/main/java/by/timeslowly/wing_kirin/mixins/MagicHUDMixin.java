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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.util.Color;

@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    @Final
    @Shadow
    private static MagicHUD.OutlineColorData[] colors;

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;lerpToColor(ILsoftware/bernie/geckolib/util/Color;)V",
            ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void applyDingShenBorder(GuiGraphics graphics, DeltaTracker tracker, CallbackInfo ci,
                                            @Local(name = "x") int x,
                                            @Local(name = "ability") DragonAbilityInstance ability) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ability == null) return;

        if (player.hasEffect(WKEffects.DING_SHEN) && ability.value().activation().type() != Activation.Type.PASSIVE) {
            MagicHUD.OutlineColorData data = colors[x];
            ((OutlineColorDataAccessor) data).setColor(Color.ofRGBA(0.3f, 0.3f, 0.3f, 0.7f));
            ((OutlineColorDataAccessor) data).setPastDelay(true);
        }
    }
}