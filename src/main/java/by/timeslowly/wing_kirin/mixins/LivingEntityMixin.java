package by.timeslowly.wing_kirin.mixins;

import by.timeslowly.wing_kirin.registry.WKAttributes;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * 在 addEffect 方法执行前拦截并修改效果实例
     */
    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private @NotNull MobEffectInstance modifyDingShenDuration(@NotNull MobEffectInstance original) {
        // 只处理定身效果且目标拥有抗性属性
        if (!original.is(WKEffects.DingShen))
            return original;

        LivingEntity self = (LivingEntity) (Object) this;
        AttributeInstance attr = self.getAttribute(WKAttributes.DINGSHEN_EFFECT_RESISTANCE);
        if (attr == null || attr.getValue() <= 0.0)
            return original;

        int originalDuration = original.getDuration();
        // 无限时长不修改
        if (originalDuration == MobEffectInstance.INFINITE_DURATION)
            return original;
        // 减少多少的比例，例：0.3属性值，减少30%时长
        int newDuration = (int) (originalDuration * (1.0 - attr.getValue()));
        if (newDuration <= 0) {
            // 返回时长为 0 的效果（原版会自动拒绝添加）
            return new MobEffectInstance(original.getEffect(), 0, original.getAmplifier(),
                    original.isAmbient(), original.isVisible(), original.showIcon());
        }

        // 时长未变则返回原实例，否则返回新实例
        return newDuration == originalDuration ? original :
                new MobEffectInstance(original.getEffect(), newDuration, original.getAmplifier(),
                        original.isAmbient(), original.isVisible(), original.showIcon());
    }
}