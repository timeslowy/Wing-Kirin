package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.eventhandler.effects.AmnesiaEffectEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

/**
 * 失忆药水效果：令（敌对）生物在效果生效时立即失去当前仇恨目标，
 * 且在效果存续期间无法产生任何新仇恨。
 * <p>
 * 实现原理（自 1.21.1 NeoForge 分支移植）：
 * <ol>
 *   <li>{@link AmnesiaEffectEventHandler} 监听
 *       {@link net.minecraftforge.event.entity.living.LivingChangeTargetEvent}，将失忆生物的
 *       待设置目标改写为 null —— 同时拦截 {@link Mob#setTarget}（MOB_TARGET，路径型生物的目标目标 AI）
 *       与脑记忆行为 StartAttacking（BEHAVIOR_TARGET，猪灵等基于 Brain 的生物）两条仇恨产生路径。</li>
 *   <li>1.21.1 中"效果初应用立即清目标"由 {@code onEffectStarted} 完成；1.20.1 无此钩子，
 *       改由 {@link AmnesiaEffectEventHandler} 监听 MobEffectEvent.Added 实现。
 *       本类在效果存续期间每秒（{@link #applyEffectTick}）执行 {@link #forgetTargets}：
 *       清除当前目标、清空脑记忆中的攻击/愤怒信息（覆盖直接写脑记忆的循声守卫等生物），
 *       并重置中立生物的持续愤怒计时。</li>
 * </ol>
 */
public class AmnesiaEffect extends MobEffect {
    // 1.20.1 的 addAttributeModifier 以 UUID 字符串区分修改器，对应 1.21.1 的 effect.amnesia_1 / effect.amnesia_2
    private static final String AMNESIA_ATTACK_DAMAGE_UUID = "7c9e6679-7425-40de-944b-e07fc1f90ae7";
    private static final String AMNESIA_ATTACK_SPEED_UUID = "d3f5a1b2-c4e6-4789-90ab-cdef12345678";

    public AmnesiaEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                AMNESIA_ATTACK_DAMAGE_UUID, -0.2,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                AMNESIA_ATTACK_SPEED_UUID, -0.1,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    // 1.21.1 的 onEffectStarted（效果初应用/刷新时立即清除当前仇恨目标）由 MobEffectEvent.Added 事件替代

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 每秒例行清理一次（setTarget 路径已由事件实时拦截，此处兜底拦截直接写脑记忆的目标）
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            forgetTargets(entity);
        }
    }

    /**
     * 清除当前仇恨并抑制仇恨相关状态。
     * 此方法无副作用：{@code setTarget(null)} 会再次触发 {@code LivingChangeTargetEvent}，
     * 因生物仍处于失忆状态，目标会被事件处理器再次改写为 null。
     */
    public static void forgetTargets(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            // 清除活动目标（PathfinderMob 的仇恨目标）
            mob.setTarget(null);

            // 清空脑记忆中的攻击/愤怒信息（猪灵、循声守卫等基于 Brain 的敌对生物）
            Brain<?> brain = mob.getBrain();
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
            brain.eraseMemory(MemoryModuleType.ANGRY_AT);
            brain.eraseMemory(MemoryModuleType.HURT_BY);
            brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
            brain.eraseMemory(MemoryModuleType.NEAREST_ATTACKABLE);
            brain.eraseMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);

            // 重置中立生物（猪灵、狼、铁傀儡、末影人等）的持续愤怒计时，避免效果结束后立即重新记仇
            if (mob instanceof NeutralMob neutral) {
                neutral.setRemainingPersistentAngerTime(0);
                neutral.setPersistentAngerTarget(null);
            }
        }
    }
}
