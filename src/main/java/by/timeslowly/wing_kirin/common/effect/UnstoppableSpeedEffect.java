package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.EffectCure;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class UnstoppableSpeedEffect extends MobEffect {
    /**
     * 待施加的过期副作用（实体 -> 效果等级）。
     * <p>
     * <b>不能在 {@code MobEffectEvent.Expired} 回调内直接 addEffect。</b>
     * 该事件在 {@code LivingEntity#tickEffects()} 迭代 {@code activeEffects} 期间、
     * {@code iterator.remove()} 之前触发；此时任何对效果表的写入都会让紧随其后的
     * {@code iterator.remove()} 抛出 {@code ConcurrentModificationException}，
     * 而该异常被原版空 catch 静默吞掉，结果是：
     * 效果实例既未从表中移除（duration 已归零），也未广播移除包；
     * 下一 tick 再次判定过期，形成每 tick 反复抛异常的循环，
     * 并中断该 tick 的遍历，令其后的所有效果停止倒计时。
     * <p>
     * 这里只登记，统一在服务器 tick 结束、所有实体 tick 处理完毕后施加，
     * 详见 {@link by.timeslowly.wing_kirin.common.eventhandler.effects.UnstoppableSpeedEventHandler}。
     */
    private static final Map<LivingEntity, Integer> PENDING_EXPIRY = new IdentityHashMap<>();

    /**
     * 修改受击冷却的逻辑见： {@link by.timeslowly.wing_kirin.mixins.LivingEntityHurtMixin}
     */
    public UnstoppableSpeedEffect(MobEffectCategory category, int color) {

        super(category, color);
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_1"), 0.8,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.unstoppable_speed_2"), 0.8,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    // 效果结束导致虚弱与缓慢，根据效果等级应用（仅登记，实际施加由 tick 结束后的统一处理完成）
    public static void onEffectExpired(@NotNull LivingEntity entity, int amplifier) {
        // 客户端不参与：效果由服务端同步，避免在客户端效果表上重复写入
        if (entity.level().isClientSide()) {
            return;
        }
        PENDING_EXPIRY.merge(entity, amplifier, Math::max);
    }

    /**
     * 在服务器 tick 结束、所有实体（含其 {@code tickEffects} 迭代）处理完毕后，
     * 统一施加过期副作用。同一 tick 内同一实体只会施加一次。
     */
    public static void resolvePendingExpiry() {
        if (PENDING_EXPIRY.isEmpty()) {
            return;
        }

        PENDING_EXPIRY.forEach((entity, amplifier) -> {
            if (entity.isRemoved() || !entity.isAlive()) {
                return;
            }
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, amplifier, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, amplifier, false, true, true));
        });

        PENDING_EXPIRY.clear();
    }

    // 使其无法被常规手段消去
    @Override
    public void fillEffectCures(final @NotNull Set<EffectCure> cures, @NotNull final MobEffectInstance effectInstance) {
        cures.clear();
    }

}
