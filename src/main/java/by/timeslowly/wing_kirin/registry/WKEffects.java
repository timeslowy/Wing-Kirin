package by.timeslowly.wing_kirin.registry;

import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.*;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// 注册药水效果
@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, Wing_kirin.MOD_ID
    );

    public static final Holder<MobEffect> DING_SHEN = MOB_EFFECTS.register("ding_shen",
            () -> new DingShenEffect(MobEffectCategory.HARMFUL, 16506002));

    public static final Holder<MobEffect> GREAT_ZHENGQI = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqiEffect(MobEffectCategory.BENEFICIAL, 16506002));

    public static final Holder<MobEffect> MACE_CRUSH = MOB_EFFECTS.register("mace_crush",
            () -> new MaceCrushEffect(MobEffectCategory.BENEFICIAL, 16506000));

    public static final Holder<MobEffect> UNSTOPPABLE_SPEED = MOB_EFFECTS.register("unstoppable_speed",
            () -> new UnstoppableSpeedEffect(MobEffectCategory.BENEFICIAL, 3190479));

    public static final Holder<MobEffect> AMNESIA = MOB_EFFECTS.register("amnesia",
            () -> new AmnesiaEffect(MobEffectCategory.HARMFUL, 0x9FB3C8));


    /**
     * 「唯快不破」结算条目：效果被移除/过期后需补加虚弱与缓慢。
     * <p>
     * 26.1 起 NeoForge 的 removeAllEffects() 在遍历活跃效果表的过程中同步触发
     * {@link MobEffectEvent.Remove}（自然过期同理，见 tickServer 迭代器），事件回调内
     * 直接 addEffect 会结构性修改正在迭代的表，抛出 ConcurrentModificationException——
     * 典型崩溃场景即饮用牛奶清空全部效果。故此处仅入队，待 ServerTickEvent.Post 统一结算。
     */
    private record PendingAftermath(LivingEntity target, int amplifier) {}

    private static final List<PendingAftermath> PENDING_AFTERMATH = new ArrayList<>();

    /**
     * 判定效果是否不可被常规手段治愈（牛奶、蜂蜜瓶等治愈类物品）。
     * <p>
     * 旧版通过 NeoForge EffectCure 机制（fillEffectCures / getCure）实现；26.1 该机制被整体移除，
     * 治愈物品改经 ConsumeEffect 路径清效果，由对应的 Mixin 拦截并调用本判定：
     * <ul>
     *     <li>「唯快不破」「从天而降」始终免疫常规治愈；</li>
     *     <li>魔法禁用（DragonSurvival）是否免疫由服务端配置 incurable 控制。</li>
     * </ul>
     * 指令（/effect clear）不经过 ConsumeEffect 路径，仍可正常清除，与旧版语义一致。
     */
    public static boolean isCureImmune(final @NotNull Holder<MobEffect> effect) {
        if (effect.is(UNSTOPPABLE_SPEED) || effect.is(MACE_CRUSH)) {
            return true;
        }
        return effect.is(DSEffects.MAGIC_DISABLED) && WKServerConfig.shouldMagicDisabledBeIncurable();
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

    // 服务端 tick 结束后统一结算延迟队列（此时无任何效果表的遍历在进行）
    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.@NotNull Post event) {
        if (PENDING_AFTERMATH.isEmpty()) {
            return;
        }

        List<PendingAftermath> pending = List.copyOf(PENDING_AFTERMATH);
        PENDING_AFTERMATH.clear();
        for (PendingAftermath aftermath : pending) {
            LivingEntity target = aftermath.target();
            if (!target.isRemoved()) {
                UnstoppableSpeedEffect.onEffectExpired(target, aftermath.amplifier());
            }
        }
    }

    private static void expireEffects(LivingEntity entity, @NotNull MobEffectInstance effectInstance) {
        if (effectInstance.getEffect().value() instanceof DingShenEffect) {
            // 定身收尾只清理数据/展示实体，不修改效果表，可同步执行
            DingShenEffect.onEffectExpired(entity);
        }
        if (effectInstance.getEffect().value() instanceof UnstoppableSpeedEffect) {
            // 会 addEffect（修改效果表），必须延迟，原因见 PendingAftermath
            PENDING_AFTERMATH.add(new PendingAftermath(entity, effectInstance.getAmplifier()));
        }
    }

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
