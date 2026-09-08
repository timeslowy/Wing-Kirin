package by.timeslowly.wing_kirin.network;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 效果同步（自 1.21.1 NeoForge 分支移植）：对需要手动广播的效果，在原版同步之外再广播一次，
 * 确保所有追踪客户端及时收到。
 * <p>
 * 1.20.1 适配：
 * <ul>
 *   <li>{@code ClientboundUpdateMobEffectPacket} 为 (entityId, instance) 双参构造
 *       （1.21.1 的第三参 blend 为 1.21.2+ 的 blend 效果体系引入）；</li>
 *   <li>{@code ClientboundRemoveMobEffectPacket} 接收 {@link MobEffect} 实例而非 Holder；</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class EffectSyncHandler {

    // 1. 处理效果被添加
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.@NotNull Added event) {
        syncEffectChange(event.getEntity(), event.getEffectInstance(), true);
    }

    // 2. 处理效果被移除（例如被指令移除，或模组移除）
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
     * 需要手动广播的效果（除原版同步外再广播一次，确保所有追踪客户端及时收到）。
     * 唯快不破：残影渲染依赖观察者客户端上的效果实例（1.21.1 注释：定身效果可能施加于
     * 未完整追踪的实体，移植定身后同样需要）。
     * <p>
     * 注意：此处只能存 {@link RegistryObject} 引用而非 {@code MobEffect} 实例——本类为
     * {@code @Mod.EventBusSubscriber}，类在 mod 构造阶段即被加载，静态初始化器中
     * {@code RegistryObject.get()} 会因注册表尚未填充而 NPE
     * （1.21.1 版无此问题：NeoForge 的 Holder 字段引用不触发注册查找）。
     * 取值在事件回调内进行，此时注册已完成，安全。
     */
    private static final List<RegistryObject<MobEffect>> SYNCED_EFFECTS = List.of(WKEffects.DING_SHEN, WKEffects.UNSTOPPABLE_SPEED);

    /**
     * 核心同步逻辑
     * @param isAdded true 代表添加/更新，false 代表移除/过期
     */
    private static void syncEffectChange(@NotNull LivingEntity entity, MobEffectInstance instance, boolean isAdded) {
        // 仅在服务端且是需要手动同步的效果时执行（此处 get() 安全：注册事件已全部完成）
        if (!entity.level().isClientSide() && isSyncedEffect(instance.getEffect())) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                if (isAdded) {
                    // 发送更新/添加包
                    ClientboundUpdateMobEffectPacket packet = new ClientboundUpdateMobEffectPacket(entity.getId(), instance);
                    serverLevel.getChunkSource().broadcast(entity, packet);
                } else {
                    // 发送移除包，参数为 实体ID 和 效果类型
                    ClientboundRemoveMobEffectPacket packet = new ClientboundRemoveMobEffectPacket(entity.getId(), instance.getEffect());
                    serverLevel.getChunkSource().broadcast(entity, packet);
                }
            }
        }
    }

    private static boolean isSyncedEffect(@NotNull MobEffect effect) {
        for (RegistryObject<MobEffect> synced : SYNCED_EFFECTS) {
            if (synced.get() == effect) {
                return true;
            }
        }
        return false;
    }
}
