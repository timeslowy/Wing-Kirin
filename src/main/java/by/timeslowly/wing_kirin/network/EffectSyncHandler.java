package by.timeslowly.wing_kirin.network;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class EffectSyncHandler {

    // 1. 处理效果被添加
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.@NotNull Added event) {
        syncEffectChange(event.getEntity(), event.getEffectInstance(), true);
    }

    // 2. 处理效果被移除（例如被牛奶清除，或指令移除）
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.@NotNull Remove event) {
        if (event.getEffectInstance() != null) {
            syncEffectChange(event.getEntity(), event.getEffectInstance(), false);
        }
    }

    // 3. 处理效果过期（持续时间结束）
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.@NotNull Expired event) {
        syncEffectChange(event.getEntity(), event.getEffectInstance(), false);
    }

    /**
     * 核心同步逻辑
     * @param isAdded true 代表添加/更新，false 代表移除/过期
     */
    private static void syncEffectChange(@NotNull LivingEntity entity, MobEffectInstance instance, boolean isAdded) {
        // 仅在服务端且是定身效果时执行
        if (!entity.level().isClientSide() && instance.getEffect().value() == WKEffects.DingShen.value()) {
            if (entity.level() instanceof ServerLevel serverLevel) {

                if (isAdded) {
                    // 发送更新/添加包
                    ClientboundUpdateMobEffectPacket packet = new ClientboundUpdateMobEffectPacket(entity.getId(), instance, true);
                    serverLevel.getChunkSource().broadcast(entity, packet);
                } else {
                    // 发送移除包，参数为 实体ID 和 效果类型
                    ClientboundRemoveMobEffectPacket packet = new ClientboundRemoveMobEffectPacket(entity.getId(), WKEffects.DingShen);
                    serverLevel.getChunkSource().broadcast(entity, packet);
                }
            }
        }
    }
}
