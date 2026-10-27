package by.timeslowly.wing_kirin.common.eventhandler.effects;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.UnstoppableSpeedEffect;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 「唯快不破」的过期副作用调度。
 * <p>
 * 所有对效果表的写操作都必须发生在 {@code LivingEntity#tickEffects()} 之外：
 * 该方法的服务端分支用 {@code Iterator} 遍历 {@code activeEffects}，
 * 在 {@code MobEffectEvent.Expired} 回调返回后才执行 {@code iterator.remove()}。
 * 回调内一旦 addEffect/removeEffect，modCount 变化会让 {@code iterator.remove()}
 * 抛出 {@code ConcurrentModificationException}，该异常被原版空 catch 吞掉，
 * 导致过期效果滞留（duration 为 0）并每 tick 重复触发过期事件。
 * <p>
 * 因此本类统一在服务器 tick 收尾阶段（此时所有实体 tick 已结束）做两件事：
 * 清理滞留实例、施加已登记的过期副作用。
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class UnstoppableSpeedEventHandler {

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.@NotNull Post event) {
        MinecraftServer server = event.getServer();
        cleanupStuckInstances(server);
        UnstoppableSpeedEffect.resolvePendingExpiry();
    }

    /**
     * 兜底清理：{@code duration} 已归零却仍残留在效果表中的「唯快不破」实例。
     * <p>
     * 正常情况下这类实例会被 {@code tickEffects()} 自行移除；只有迭代被并发修改打断时
     * 才会残留。此时在 tick 之外移除必定成功，移除动作会触发 Remove 回调，
     * 其副作用经由 {@link UnstoppableSpeedEffect#resolvePendingExpiry()} 在同一 tick 收尾时施加。
     */
    private static void cleanupStuckInstances(@NotNull MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MobEffectInstance instance = player.getEffect(WKEffects.UNSTOPPABLE_SPEED);
            if (instance == null) {
                continue;
            }

            int duration = instance.getDuration();
            // 尚在计时或为无限时长：无需处理
            if (duration > 0 || duration == MobEffectInstance.INFINITE_DURATION) {
                continue;
            }

            player.removeEffect(WKEffects.UNSTOPPABLE_SPEED);
        }
    }
}
