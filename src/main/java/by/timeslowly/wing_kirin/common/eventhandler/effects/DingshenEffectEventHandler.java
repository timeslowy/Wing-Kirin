package by.timeslowly.wing_kirin.common.eventhandler.effects;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.common.effect.DingShenEffect;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKAttachments;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class DingshenEffectEventHandler {
    /** 定身被粉碎时需重置的 mcfunction（恢复AI、清效果、移除标签、重置计分板、杀死骑乘展示实体） */
    private static final ResourceLocation DING_SHEN_REMOVE_EFFECTS_FUNCTION =
            ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "dragon_ability/stasia_hex/desctuor/remove_effects");

    /** 肌肉松弛触发阈值：被施加的定身效果总时长超过该刻数（50 秒）时生效，直至本次效果结束 */
    private static final int DING_SHEN_MUSCLE_RELAX_THRESHOLD = 50 * 20;

    /**
     * 肌肉松弛判定：被施加超过 50 秒的定身效果时打上标记，
     * 此后直至效果结束（到期/移除/死亡）全程承受额外 50% 伤害。
     * 定身的叠加刷新（mcfunction 每次 give 累加后的总时长）同样会触发本判定。
     */
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.@NotNull Added event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null || !instance.is(WKEffects.DING_SHEN)) {
            return;
        }
        int duration = instance.getDuration();
        if (duration == -1 || duration > DING_SHEN_MUSCLE_RELAX_THRESHOLD) {
            LivingEntity entity = event.getEntity();
            // 仅在首次进入肌肉松弛时播放骨块破坏声（叠加刷新不重复播放）
            if (!entity.getData(WKAttachments.DING_SHEN_MUSCLE_RELAXED)) {
                entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.BONE_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                entity.setData(WKAttachments.DING_SHEN_MUSCLE_RELAXED, true);
            }
        }
    }

    /**
     * 检查受害者的定身状态效果
     * 定身时间过长会导致"肌肉松弛"，承受更多伤害
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        // 肌肉松弛：被施加超过 50 秒定身的实体，全程承受额外 50% 伤害（直至本次效果结束）
        if (victim.getData(WKAttachments.DING_SHEN_MUSCLE_RELAXED)) {
            multiplier *= 1.5f;
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }

        // 粉碎机制：单次受到的伤害超过最大血量（服务端配置比例，默认 30%）时，定身效果被"粉碎"
        if (victim.hasEffect(WKEffects.DING_SHEN)
                && event.getAmount() > victim.getMaxHealth() * WKServerConfig.getDingShenShatterRatio()) {
            shatterDingShen(victim);
            // 不破不立：攻击粉碎的龙玩家获得增益
            grantShatterBuff(event.getSource().getEntity());
        }
    }

    /**
     * 粉碎定身：移除定身效果，并以受害者为执行者执行 remove_effects mcfunction。
     * <p>
     * 移除效果会触发 {@link WKEffects#onEffectRemoved} 的清理链（恢复AI、清理"定"字展示实体、
     * 播放粉碎音效与粒子）；mcfunction 则负责重置命令侧的定身状态（being_frozen 标签、
     * freezeTimer 计分板、骑乘的物品展示实体），避免计时器自然归零后重复执行清理。
     */
    private static void shatterDingShen(@NotNull LivingEntity victim) {
        // 先移除效果再执行函数：避免函数中 effect clear 再次触发移除事件导致重复播放音效/粒子
        victim.removeEffect(WKEffects.DING_SHEN);

        if (victim.level() instanceof ServerLevel serverLevel) {
            ServerFunctionManager functions = serverLevel.getServer().getFunctions();
            // 实体命令源的权限为 0，而函数内命令（effect/tag/data/scoreboard/kill）均为权限 2 命令，
            // 直接执行会因权限不足被全部静默跳过；须与游戏循环（getGameLoopSender）一致提升到权限 2
            CommandSourceStack source = victim.createCommandSourceStack().withPermission(2).withSuppressedOutput();
            functions.get(DING_SHEN_REMOVE_EFFECTS_FUNCTION)
                    .ifPresent(function -> functions.execute(function, source));
        }
    }

    /**
     * 「不破不立」粉碎增益：攻击粉碎被定身实体的龙玩家获得增益。
     * <p>
     * 翼麒麟龙玩家始终可获得；非翼麒麟龙玩家需开启服务端配置
     * {@code ding_shen.shatterBuffForNonWingKirin} 后才能获得（默认为关）。
     */
    private static void grantShatterBuff(@Nullable Entity attacker) {
        if (!(attacker instanceof ServerPlayer player)) {
            return;
        }
        // 仅龙玩家
        if (!DragonStateProvider.isDragon(player)) {
            return;
        }
        Holder<DragonSpecies> species = DragonStateProvider.getData(player).species();
        if (species == null) {
            return;
        }
        // 翼麒麟始终可获得；非翼麒麟需配置开启
        if (!species.is(DragonSurvival.res("wing_kirin"))
                && !WKServerConfig.shouldProvideShatterBuffToNonWingKirinDragons()) {
            return;
        }
        // 不破不立：生命恢复Ⅱ+魔源涌动Ⅱ，5 秒
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 1));
        player.addEffect(new MobEffectInstance(DSEffects.SOURCE_OF_MAGIC, 5 * 20, 1));
    }

    /**
     * 玩家退出服务器时清理"定"字展示实体。
     * <p>
     * 原版 PlayerList.remove 以 UNLOADED_WITH_PLAYER 原因移除玩家，而 LivingEntity.remove
     * 仅对 KILLED/DISCARDED 触发效果清理（onMobRemoved），因此退出服务器时须在此事件中手动清理，
     * 否则展示实体将残留在世界中。
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.@NotNull PlayerLoggedOutEvent event) {
        DingShenEffect.cleanupPlayerDisplay(event.getEntity());
    }

    /**
     * 定身锁位模式下禁止任何维度穿越（下界门、末地门、mod 传送门等）。
     * 未开启锁位时，玩家穿越维度前清理旧维度中的"定"字展示实体
     * （生物骑乘的展示实体会被 tick 函数中的孤立检测兜底清理）。
     */
    @SubscribeEvent
    public static void onEntityTravelToDimension(@NotNull EntityTravelToDimensionEvent event) {
        if (WKServerConfig.shouldDingShenLockPosition()) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity living && living.hasEffect(WKEffects.DING_SHEN)) {
                event.setCanceled(true);
            }
            return;
        }
        // 未锁位：穿越前清理旧维度的展示实体
        if (event.getEntity() instanceof LivingEntity living) {
            DingShenEffect.cleanupPlayerDisplay(living);
        }
    }
}
