package by.timeslowly.wing_kirin.registry;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.effect.AmnesiaEffect;
import by.timeslowly.wing_kirin.common.effect.DingShenEffect;
import by.timeslowly.wing_kirin.common.effect.GreatZhengqiEffect;
import by.timeslowly.wing_kirin.common.effect.UnstoppableSpeedEffect;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 注册药水效果（自 1.21.1 NeoForge 分支移植，仅含已移植的效果）。
 * 注册时机：主类构造器中调用 {@link #register(IEventBus)}。
 */
// 药水效果事件订阅（MobEffectEvent 在 Forge 1.20.1 是游戏总线事件——由 MinecraftForge.EVENT_BUS 发布，
// 因此这里用默认 FORGE 总线；勿照搬 1.21.1 之前 javadoc 中「补 Bus.MOD」的说法，那会导致订阅永远不触发）
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class WKEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            ForgeRegistries.MOB_EFFECTS, WingKirin.MODID
    );

    // 定身（HARMFUL，颜色同 1.21.1：16506002）
    public static final RegistryObject<MobEffect> DING_SHEN = MOB_EFFECTS.register("ding_shen",
            () -> new DingShenEffect(MobEffectCategory.HARMFUL, 16506002));

    // 浩然正气（BENEFICIAL，颜色同 1.21.1：16506002）
    public static final RegistryObject<MobEffect> GREAT_ZHENGQI = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqiEffect(MobEffectCategory.BENEFICIAL, 16506002));

    // 失神/失忆（HARMFUL，颜色同 1.21.1：0x9FB3C8）
    public static final RegistryObject<MobEffect> AMNESIA = MOB_EFFECTS.register("amnesia",
            () -> new AmnesiaEffect(MobEffectCategory.HARMFUL, 0x9FB3C8));

    // 唯快不破（BENEFICIAL，颜色同 1.21.1：3190479）
    public static final RegistryObject<MobEffect> UNSTOPPABLE_SPEED = MOB_EFFECTS.register("unstoppable_speed",
            () -> new UnstoppableSpeedEffect(MobEffectCategory.BENEFICIAL, 3190479));

    // 魔法封禁效果不可治愈（magic_disabled.incurable）——利用 Forge 1.20.1 的效果可治愈物品机制：
    // 牛奶走 LivingEntity.curePotionEffects(ItemStack)，对每个活动效果先检查 instance.isCurativeItem(物品)，
    // 在施加时清空 MAGIC_DISABLED 实例的 curative items 列表，牛奶便会直接跳过它（连 MobEffectEvent.Remove 都不触发）。
    // 相比 1.21.1 的 getCure() 拦截（1.20.1 的 Remove 无此信息，无差别取消会误伤 /effect clear 等命令路径），
    // 此方案只影响治愈行为，命令、模组移除与自然到期均不受影响。
    // 注：ClientPacketListener 同步药水时也会在客户端触发 Added，对客户端实例清列表无害。
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.@NotNull Added event) {
        if (ConfigSyncHandler.magicDisabledIncurable()
                && event.getEffectInstance().getEffect() == DSEffects.MAGIC_DISABLED.get()) {
            event.getEffectInstance().setCurativeItems(List.of());
        }
        // 唯快不破无法被常规手段消去（1.20.1 无 fillEffectCures，对齐 1.21.1 的 cures.clear() 语义：
        // 仅封死牛奶/蜂蜜等治愈路径；命令、模组移除与自然到期不受影响）
        if (event.getEffectInstance().getEffect() == UNSTOPPABLE_SPEED.get()) {
            event.getEffectInstance().setCurativeItems(List.of());
        }
    }

    // 药水效果移除行为
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.@NotNull Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(event.getEntity(), effectInstance);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.@NotNull Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance != null) {
            expireEffects(event.getEntity(), effectInstance);
        }
    }

    private static void expireEffects(LivingEntity entity, @NotNull MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() instanceof DingShenEffect) {
            DingShenEffect.onEffectExpired(entity);
        }
        if (effectInstance.getEffect() instanceof UnstoppableSpeedEffect) {
            UnstoppableSpeedEffect.onEffectExpired(entity, effectInstance.getAmplifier());
        }
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
