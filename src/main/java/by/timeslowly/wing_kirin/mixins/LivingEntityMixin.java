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
     * 修改定身效果的持续时间，根据实体的定身抗性属性进行减免
     * <p>
     * 该方法通过 Mixin 在 {@link LivingEntity#addEffect(MobEffectInstance, net.minecraft.world.entity.Entity)}
     * 方法执行前拦截并修改传入的效果实例。如果目标实体拥有定身抗性属性，则按抗性比例减少效果时长。
     * </p>
     *
     * @param original 原始的效果实例，包含效果类型、持续时间、等级等信息
     * @return 修改后的效果实例。如果满足以下条件之一则返回原实例：
     *         <ul>
     *           <li>效果类型不是定身效果</li>
     *           <li>实体没有定身抗性属性或抗性值小于等于0</li>
     *           <li>效果时长为无限</li>
     *           <li>计算后的新时长与原时长相同</li>
     *         </ul>
     *         如果抗性完全抵消效果（新时长<=0），则返回时长为0的效果实例（原版会自动拒绝添加）
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

        // 根据抗性属性值按比例减少持续时间，例：0.3属性值减少30%时长
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