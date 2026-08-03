package by.timeslowly.wing_kirin.common.eventhandler;

import by.dragonsurvivalteam.dragonsurvival.DragonSurvival;
import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.registry.WKEffects;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class EffectEventHandler {
    /** 定身粉碎阈值：单次受到的伤害超过最大血量的该比例时，定身效果被"粉碎" */
    private static final double DING_SHEN_SHATTER_RATIO = 0.3;

    /** 定身被粉碎时需重置的 mcfunction（恢复AI、清效果、移除标签、重置计分板、杀死骑乘展示实体） */
    private static final ResourceLocation DING_SHEN_REMOVE_EFFECTS_FUNCTION =
            ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "dragon_ability/stasia_hex/desctuor/remove_effects");

    /**
     * 检查受害者的定身状态效果
     * 定身时间过长会导致"肌肉松弛"，承受更多伤害
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        var DingShen = victim.getEffect(WKEffects.DING_SHEN);
        if (DingShen != null) {
            int duration = DingShen.getDuration();
            if (duration == -1 || duration > 1000 ) {
                multiplier *= 1.5f;
            }
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }

        // 粉碎机制：单次受到的伤害超过最大血量 30% 时，定身效果被"粉碎"
        if (victim.hasEffect(WKEffects.DING_SHEN)
                && event.getAmount() > victim.getMaxHealth() * DING_SHEN_SHATTER_RATIO) {
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
     * 定身锁位模式下禁止任何维度穿越（下界门、末地门、mod 传送门等）。
     */
    @SubscribeEvent
    public static void onEntityTravelToDimension(@NotNull EntityTravelToDimensionEvent event) {
        if (!WKServerConfig.shouldDingShenLockPosition()) return;
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living && living.hasEffect(WKEffects.DING_SHEN)) {
            event.setCanceled(true);
        }
    }
}
