package by.timeslowly.wing_kirin.common.eventhandler.abilities;

import by.timeslowly.wing_kirin.WingKirin;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.scores.Objective;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 聚形散气追踪与结算（服务端，自 1.21.1 函数链按方案B 由 Java 接管）。
 * <p>
 * 替代 1.21.1 的 marker 标志实体 + UUID 转十六进制计分板持有者 + 宏函数链：
 * <ul>
 *   <li>每刻：倒计时（归零重置 remove_check）、超距破除（清隐身 + 粒子音效）、死亡/离线清理、
 *       actionbar 距离提示（三档颜色，仅移动时显示）；</li>
 *   <li>「破隐一击」：{@link LivingHurtEvent} 检测受惠玩家对实体造成伤害——
 *       替代 1.21.1 的 player_hurt_entity 进度（其 player 条件列表为 1.21 格式，1.20.1 无法直接表达，
 *       且两个 DS 自定义 type_specific 无法并入单个 EntityPredicate）。</li>
 * </ul>
 * remove_check 计分板由本类经计分板 API 维护，JSON 中伤害免疫效果的 early_removal_condition 原样工作：
 * 置 1 后效果保持，重置（删除）后 DS 依条件提前移除伤害免疫。
 * 「破隐一击」攻击加成（默认 +50% 攻击伤害，倍率乘算，支持等级函数）亦由本类经瞬态属性修饰符实现，
 * 随追踪器破除/到期/清理一并移除（原为 JSON 中 dragonsurvival:modifier 时长效果，已从 JSON 移除）。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class InstantInvisibilityEventHandler {
    private static final String REMOVE_CHECK_OBJECTIVE = "wk.instant_invisibility.remove_check";

    /** 破隐一击攻击加成的属性修饰符 ID（固定 UUID，重复施加前先移除即可安全替换） */
    private static final UUID ATTACK_BONUS_ID = UUID.nameUUIDFromBytes(
            "wing_kirin:instant_invisibility.attack_bonus".getBytes(java.nio.charset.StandardCharsets.UTF_8));

    /** 所有者UUID -> 追踪器（每名玩家同时至多一个：冷却 1200 刻 > 最长持续时间 600 刻） */
    private static final Map<UUID, Tracker> TRACKERS = new HashMap<>();

    /** 单个追踪器：施法位置（原 marker 实体）、剩余刻数、上一刻位置（用于移动判定） */
    private static final class Tracker {
        final String ownerName;
        final Vec3 castPos;
        final int radius;
        int ticksLeft;
        Vec3 lastPos;

        Tracker(String ownerName, Vec3 castPos, int radius, int ticksLeft, Vec3 startPos) {
            this.ownerName = ownerName;
            this.castPos = castPos;
            this.radius = radius;
            this.ticksLeft = ticksLeft;
            this.lastPos = startPos;
        }
    }

    /** 由 {@link by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects.InstantInvisibilityTrackerEffect} 调用 */
    public static void startTracking(final @NotNull ServerPlayer owner, int ticksLeft, int radius, float attackBonus) {
        setRemoveCheck(owner.server, owner.getScoreboardName(), 1);
        applyAttackBonus(owner, attackBonus);
        TRACKERS.put(owner.getUUID(), new Tracker(owner.getScoreboardName(), owner.position(), radius, ticksLeft, owner.position()));
    }

    /** 施加破隐一击攻击加成（瞬态修饰符：不随 NBT 保存，死亡/重生自然消失；同 ID 先移除再施加以安全替换） */
    private static void applyAttackBonus(final @NotNull ServerPlayer owner, float amount) {
        AttributeInstance attackDamage = owner.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_BONUS_ID);
            attackDamage.addTransientModifier(new AttributeModifier(ATTACK_BONUS_ID,
                    "wing_kirin.instant_invisibility", amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void removeAttackBonus(final @NotNull ServerPlayer owner) {
        AttributeInstance attackDamage = owner.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            attackDamage.removeModifier(ATTACK_BONUS_ID);
        }
    }

    @SubscribeEvent
    public static void onServerTick(final @NotNull TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TRACKERS.isEmpty()) {
            return;
        }
        MinecraftServer server = event.getServer();
        Iterator<Map.Entry<UUID, Tracker>> iterator = TRACKERS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Tracker> entry = iterator.next();
            ServerPlayer owner = server.getPlayerList().getPlayer(entry.getKey());
            Tracker tracker = entry.getValue();

            // 所有者离线/死亡：清理并重置 remove_check（效果本体已随实体消亡/刷新，
            // 不重置会令 usage_blocked 卡死；1.21.1 中死亡路径交由 player_death/main 处理，此处一并兜底）
            if (owner == null || owner.isRemoved() || owner.isDeadOrDying()) {
                // 离线时实体已不存在，其瞬态属性修饰符随之消失，无需移除
                if (owner != null) {
                    removeAttackBonus(owner);
                }
                resetRemoveCheck(server, tracker.ownerName);
                iterator.remove();
                continue;
            }

            // 超距破除（等价 distance_out.mcfunction）
            if (owner.distanceToSqr(tracker.castPos) > (double) tracker.radius * tracker.radius) {
                breakInvisibility(owner);
                resetRemoveCheck(server, tracker.ownerName);
                iterator.remove();
                continue;
            }

            // 倒计时（每刻减 1，归零重置 remove_check；DS 效果同期自然到期，与 1.21.1 一致）
            if (tracker.ticksLeft > 0) {
                tracker.ticksLeft--;
            }
            if (tracker.ticksLeft == 0) {
                resetRemoveCheck(server, tracker.ownerName);
                removeAttackBonus(owner);
                iterator.remove();
                continue;
            }

            // 距离提示（仅移动时显示，等价 is_moving 谓词的 speed ≥ 0.1 阈值）
            if (owner.position().subtract(tracker.lastPos).horizontalDistance() > 0.05D) {
                sendDistanceNotice(owner, tracker.castPos);
            }
            tracker.lastPos = owner.position();
        }
    }

    // 「破隐一击」：受惠玩家对实体造成伤害时立即破除（原 player_hurt_entity 进度触发，attack_remove.mcfunction 逻辑）
    @SubscribeEvent
    public static void onLivingHurt(final @NotNull LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer owner && TRACKERS.containsKey(owner.getUUID())) {
            TRACKERS.remove(owner.getUUID());
            breakInvisibility(owner);
            resetRemoveCheck(owner.server, owner.getScoreboardName());
            // TODO:「破隐一击次数」统计数据（1.21.1 为 wk-stats add @s wing_kirin:broken_instant_invisibility_times，

        }
    }

    @SubscribeEvent
    public static void onServerStopped(final @NotNull ServerStoppedEvent event) {
        TRACKERS.clear();
    }

    /** 破除隐身：清除隐身效果 + 移除攻击加成 + 现身云粒子 + 熄火音效（attack_remove 与 distance_out 的共有部分） */
    private static void breakInvisibility(final @NotNull ServerPlayer owner) {
        owner.removeEffect(MobEffects.INVISIBILITY);
        removeAttackBonus(owner);
        ServerLevel level = owner.serverLevel();
        level.sendParticles(ParticleTypes.CLOUD, owner.getX(), owner.getY(), owner.getZ(), 80, 1.0D, 1.0D, 1.0D, 0.0D);
        level.playSound(null, owner.getX(), owner.getY(), owner.getZ(),
                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    // TODO:提示可以动态调整
    /** actionbar 距离提示：按与施法位置的距离分三档颜色（红档逐格精确倒计时），复刻 distance_notice 函数链 */
    private static void sendDistanceNotice(final @NotNull ServerPlayer owner, final @NotNull Vec3 castPos) {
        double distance = owner.position().distanceTo(castPos);
        String text;
        String color;
        if (distance < 11.0D) {
            color = "#35e035";
            text = distance < 4.0D ? ">21" : distance < 7.0D ? ">18" : ">15";
        } else if (distance < 20.0D) {
            color = "#e9f00e";
            text = distance < 14.0D ? ">11" : distance < 17.0D ? ">8" : "6";
        } else {
            color = "#e61717";
            text = distance < 21.0D ? "5" : distance < 22.0D ? "4" : distance < 23.0D ? "3" : distance < 24.0D ? "2" : "1";
        }

        owner.displayClientMessage(
                Component.translatable("actionbar.wing_kirin.instant_invisibility.distance_notice")
                        .append(Component.literal(text).withStyle(style -> style.withColor(net.minecraft.network.chat.TextColor.parseColor(color)))),
                true);
    }

    private static void setRemoveCheck(final @NotNull MinecraftServer server, final @NotNull String ownerName, int value) {
        Objective objective = server.getScoreboard().getObjective(REMOVE_CHECK_OBJECTIVE);
        if (objective != null) {
            server.getScoreboard().getOrCreatePlayerScore(ownerName, objective).setScore(value);
        }
    }

    private static void resetRemoveCheck(final @NotNull MinecraftServer server, final @NotNull String ownerName) {
        Objective objective = server.getScoreboard().getObjective(REMOVE_CHECK_OBJECTIVE);
        if (objective != null) {
            // 等价 scoreboard players reset：删除分数，使 early_removal_condition（<1）成立、usage_blocked 解除
            server.getScoreboard().resetPlayerScore(ownerName, objective);
        }
    }
}
