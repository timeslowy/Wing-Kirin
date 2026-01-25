package by.timeslowly.wing_kirin.network;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class EffectSyncHandler {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effectInstance = event.getEffectInstance();

        // 1. 确保在服务端运行
        // 2. 检查是否是定身效果
        if (effectInstance != null && !entity.level().isClientSide() &&
                effectInstance.getEffect().value() == WKEffects.DingShen.value()) {

            if (entity.level() instanceof ServerLevel serverLevel) {
                // 创建同步数据包
                // 参数含义：实体ID, 效果实例, 是否显示颗粒, 是否是环境效果(Beacon等)
                ClientboundUpdateMobEffectPacket packet = new ClientboundUpdateMobEffectPacket(
                        entity.getId(),
                        effectInstance,
                        true
                );

                // 广播给所有正在追踪（能看到）该实体的玩家
                serverLevel.getChunkSource().broadcast(entity, packet);
            }
        }
    }
}
