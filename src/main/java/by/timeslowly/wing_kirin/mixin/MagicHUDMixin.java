package by.timeslowly.wing_kirin.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.core.object.Color;

/**
 * 定身置灰法力 HUD（自 1.21.1 移植）。
 * 1.20.1 适配（均已对字节码/局部变量表验证）：
 * <ul>
 *   <li>render 签名为 (GuiGraphics, float)（1.21.1 为 DeltaTracker）；</li>
 *   <li>GeckoLib 4 的 Color 位于 core.object 包；</li>
 *   <li>lerpToColor 共 3 次调用、局部变量 x/ability 同名，注入点 ordinal=2 不变；</li>
 *   <li>blit 9 参 (GuiGraphics,RL,IIIFFFF)，语义为 (graphics, atlas, x, y, size, alpha, R, G, B)；</li>
 *   <li><b>不能用 1.21.1 的 @ModifyArgs</b>：ArgsClassGenerator 生成的 org.spongepowered.asm.synthetic.args.Args$1
 *       在 Forge 1.20.1 的 ModuleClassLoader 模块体系下无法被 dragonsurvival 模块加载，
 *       DS 的 ConfigHandler 扫描 @ConfigOption 反射加载 MagicHUD 时直接 NoClassDefFoundError 崩启动
 *       （2026-09-08 实证）——改用 @Redirect（不生成合成 Args 类）。</li>
 * </ul>
 * 仅客户端：登记于 mixins.json 的 client 数组。
 */
@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    @Final
    @Shadow
    private static MagicHUD.OutlineColorData[] colors;

    @Shadow
    private static void blit(GuiGraphics graphics, ResourceLocation atlas, int x, int y, int size,
                             float alpha, float red, float green, float blue) {}

    /**
     * 技能图标边框调整
     */
    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;lerpToColor(ILsoftware/bernie/geckolib/core/object/Color;)V",
            ordinal = 2, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT, remap = false)
    private static void applyDingShenBorder(GuiGraphics graphics, float partialTick, CallbackInfo ci,
                                            @Local(name = "x") int x,
                                            @Local(name = "ability") DragonAbilityInstance ability) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ability == null) return;

        // 若有定身效果且配置启用禁用技能，则覆盖灰色遮罩
        if (ConfigSyncHandler.dingShenDisableAbilities() && player.hasEffect(WKEffects.DING_SHEN.get()) && ability.value().activation().type() != Activation.Type.PASSIVE) {
            MagicHUD.OutlineColorData data = colors[x];
            ((OutlineColorDataAccessor) data).setColor(Color.ofRGBA(0.3f, 0.3f, 0.3f, 0.7f));
            ((OutlineColorDataAccessor) data).setPastDelay(false);
        }
    }

    /**
     * 法力图标统一灰色（@Redirect 改写 blit 的 RGB 入参，替代 1.21.1 的 @ModifyArgs——原因见类注释）。
     * 7 处 blit 调用点全部重定向，与 1.21.1 @ModifyArgs 全包装的行为一致。
     */
    @Redirect(method = "render", at = @At(value = "INVOKE",
            target = "Lby/dragonsurvivalteam/dragonsurvival/client/gui/hud/MagicHUD;blit(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/resources/ResourceLocation;IIIFFFF)V"),
            remap = false)
    private static void wing_kirin$grayOutBlit(GuiGraphics graphics, ResourceLocation atlas, int x, int y, int size,
                                               float alpha, float red, float green, float blue) {
        Player player = Minecraft.getInstance().player;
        if (player != null && ConfigSyncHandler.dingShenDisableAbilities() && player.hasEffect(WKEffects.DING_SHEN.get())) {
            red = 0.3f;
            green = 0.3f;
            blue = 0.3f;
        }
        blit(graphics, atlas, x, y, size, alpha, red, green, blue);
    }
}
