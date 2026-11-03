package by.timeslowly.wing_kirin.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import by.dragonsurvivalteam.dragonsurvival.registry.attachments.MagicData;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.activation.Activation;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.core.object.Color;

/**
 * 定身置灰法力 HUD（自 1.21.1 移植）。
 * 1.20.1 适配（均已对 DS 2.0.69 字节码/反编译验证）：
 * <ul>
 *   <li>render 签名为 (GuiGraphics, float)（1.21.1 为 DeltaTracker）；</li>
 *   <li>GeckoLib 4 的 Color 位于 core.object 包；</li>
 *   <li>DS 2.0.69 起，边框颜色的状态机（原 {@code render} 内的 {@code lerpToColor}）被抽离到
 *       {@code tickOutlineColors(ClientTickEvent)}，{@code render} 改为调用 {@code interpolatedColor(int, float)} 取色。
 *       因此注入点由 {@code render#lerpToColor} 迁移至 {@code interpolatedColor} 头部，直接短路返回值：
 *       既覆盖逐槽边框（render 第 1 处调用），也覆盖选中槽高亮（render 第 2 处调用）；</li>
 *   <li>blit 9 参 (GuiGraphics,RL,IIIFFFF)，语义为 (graphics, atlas, x, y, size, alpha, R, G, B)，2.0.69 未变，
 *       render 中 7 处调用点全部重定向；</li>
 *   <li><b>不能用 1.21.1 的 @ModifyArgs</b>：ArgsClassGenerator 生成的 org.spongepowered.asm.synthetic.args.Args$1
 *       在 Forge 1.20.1 的 ModuleClassLoader 模块体系下无法被 dragonsurvival 模块加载，
 *       DS 的 ConfigHandler 扫描 @ConfigOption 反射加载 MagicHUD 时直接 NoClassDefFoundError 崩启动
 *       （2026-09-08 实证）——改用 @Redirect（不生成合成 Args 类）。</li>
 * </ul>
 * 仅客户端：登记于 mixins.json 的 client 数组。
 */
@Mixin(value = MagicHUD.class, remap = false)
public abstract class MagicHUDMixin {

    @Shadow
    private static void blit(GuiGraphics graphics, ResourceLocation atlas, int x, int y, int size,
                             float alpha, float red, float green, float blue) {}

    /**
     * 技能图标边框调整。
     */
    @Inject(method = "interpolatedColor(IF)Lsoftware/bernie/geckolib/core/object/Color;",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void applyDingShenBorder(int slot, float partialTick, CallbackInfoReturnable<Color> cir) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // 若有定身效果且配置启用禁用技能，则覆盖灰/红/黄边框为统一灰色
        if (!ConfigSyncHandler.dingShenDisableAbilities() || !player.hasEffect(WKEffects.DING_SHEN.get())) return;

        DragonAbilityInstance ability = MagicData.getData(player).fromSlot(slot);
        if (ability == null) return;
        if (ability.value().activation().type() == Activation.Type.PASSIVE) return;

        cir.setReturnValue(Color.ofRGBA(0.3f, 0.3f, 0.3f, 0.7f));
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
