package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 仅在客户端生效（颜色渲染是客户端逻辑）
@OnlyIn(Dist.CLIENT)
// 混入到 Entity 类，修改其队伍/发光颜色方法
@Mixin(Entity.class)
public abstract class EntityGlowColorMixin {

    // 定义你想要的目标颜色（0xFBDC92）
    @Unique
    private static final int WING_KIRIN_GLOW_COLOR = 16506002; // 金色

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof LivingEntity living) {
            if (living.hasEffect(WKEffects.DING_SHEN)) { // 如果具有定身效果
                cir.setReturnValue(WING_KIRIN_GLOW_COLOR); // 返回金色 0xFBDC92
            }
        }
    }
}
