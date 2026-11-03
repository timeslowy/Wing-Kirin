package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import software.bernie.geckolib.util.Color;

@OnlyIn(Dist.CLIENT)
@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    /**
     * 技能图标边框调整。
     * <p>
     * DS 2.0.72 起，边框颜色的状态机（原 {@code render} 内的 {@code lerpToColor}）被抽离到
     * {@code tickOutlineColors(ClientTickEvent.Post)}，{@code render} 改为调用本方法取色。
     * 因此注入点由 {@code render#lerpToColor} 迁移至 {@code interpolatedColor} 头部，
     * 直接短路返回值：既覆盖逐槽边框（render 第 1 处调用），也覆盖选中槽高亮（第 2 处调用）。
     */
    @Inject(method = "interpolatedColor(IF)Lsoftware/bernie/geckolib/util/Color;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void applyDingShenBorder(int slot, float partialTick, CallbackInfoReturnable<Color> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // 若有定身效果且配置启用禁用技能，则覆盖灰/红/黄边框为统一灰色
        if (!WKServerConfig.shouldDingShenDisableAbilities() || !player.hasEffect(WKEffects.DING_SHEN)) return;

        DragonAbilityInstance ability = MagicData.getData(player).fromSlot(slot);
        if (ability == null) return;
        if (ability.value().activation().type() == Activation.Type.PASSIVE) return;

        cir.setReturnValue(Color.ofRGBA(0.3f, 0.3f, 0.3f, 0.7f));
    }

    /**
     *  法力图标统一灰色
     */
    @ModifyArgs(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;blit(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIFFFF)V"),
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
