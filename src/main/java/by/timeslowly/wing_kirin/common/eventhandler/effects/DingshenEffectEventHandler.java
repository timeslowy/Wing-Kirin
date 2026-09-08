package by.timeslowly.wing_kirin.common.eventhandler.effects;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import by.dragonsurvivalteam.dragonsurvival.registry.DSEffects;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.DragonSpecies;
import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.effect.DingShenEffect;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.registry.WKAttributes;
import by.timeslowly.wing_kirin.registry.WKEffects;
import by.timeslowly.wing_kirin.registry.WKEnchantments;
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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 定身效果事件处理（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * 1.20.1 适配：
 * <ul>
 *   <li>LivingIncomingDamageEvent → LivingHurtEvent（护甲减免前，可改原始伤害，同既有移植惯例）；</li>
 *   <li>1.20.1 MobEffect 无 onEffectAdded/onMobRemoved 回调 → 初应用处理移入
 *       {@link #onDingShenAdded}（Forge MobEffectEvent.Added，以 getOldEffectInstance() 区分全新施加与
 *       叠加刷新——1.21.1 中 MobEffect.onEffectAdded 仅全新施加触发，而 NeoForge 的 Added 与 Forge 一致
 *       刷新时也触发，故肌肉松弛判定保留在同一事件中）；死亡清理走 LivingDeathEvent；</li>
 *   <li>WKAttachments 肌肉松弛标记 → getPersistentData()（{@link #MUSCLE_RELAXED_KEY}）；</li>
 *   <li>挖掘封锁（1.21.1 为 MINING_EFFICIENCY 属性 -100%）→ {@link #onBreakSpeed} 置 0；</li>
 *   <li>定身抗性附魔的属性施加（1.21.1 为附魔数据包的属性效果组件，1.20.1 无此机制）→
 *       {@link #onEquipmentChange} / {@link #onEntityJoinLevel} 汇总护甲附魔等级写入瞬时修饰符。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID)
public class DingshenEffectEventHandler {
    /** 定身被粉碎时需重置的 mcfunction（恢复AI、清效果、移除标签、重置计分板、杀死骑乘展示实体） */
    private static final ResourceLocation DING_SHEN_REMOVE_EFFECTS_FUNCTION =
            new ResourceLocation(WingKirin.MODID, "dragon_ability/stasia_hex/desctuor/remove_effects");

    /** 肌肉松弛触发阈值：被施加的定身效果总时长超过该刻数（50 秒）时生效，直至本次效果结束 */
    private static final int DING_SHEN_MUSCLE_RELAX_THRESHOLD = 50 * 20;

    /** 「肌肉松弛」标记的持久 NBT 键（1.21.1 为 WKAttachments.DING_SHEN_MUSCLE_RELAXED） */
    public static final String MUSCLE_RELAXED_KEY = "DingShenMuscleRelaxed";

    /** 定身抗性附魔属性修饰符的固定 UUID（瞬时装卸，不存档） */
    private static final UUID DINGSHEN_RESISTANCE_MODIFIER_UUID =
            UUID.nameUUIDFromBytes("wing_kirin:enchantment.dingshen_resistance".getBytes(StandardCharsets.UTF_8));

    /**
     * 效果初应用（全新施加，非叠加刷新）：禁用AI、锁位模式下解除骑乘、播放音效粒子、
     * 为玩家生成"定"字展示实体。对应 1.21.1 的 DingShenEffect.onEffectAdded。
     * <p>
     * 肌肉松弛判定：被施加超过 50 秒的定身效果时打上标记，
     * 此后直至效果结束（到期/移除/死亡）全程承受额外 50% 伤害。
     * 定身的叠加刷新（mcfunction 每次 give 累加后的总时长）同样会触发本判定
     * （Forge 的 Added 在刷新时同样触发，与 NeoForge 一致）。
     */
    @SubscribeEvent
    public static void onDingShenAdded(MobEffectEvent.@NotNull Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance.getEffect() != WKEffects.DING_SHEN.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();

        // ---- 肌肉松弛判定（仅服务端；全新施加与叠加刷新均判定） ----
        if (!entity.level().isClientSide()) {
            int duration = instance.getDuration();
            if (duration == -1 || duration > DING_SHEN_MUSCLE_RELAX_THRESHOLD) {
                // 仅在首次进入肌肉松弛时播放骨块破坏声（叠加刷新不重复播放）
                if (!entity.getPersistentData().getBoolean(MUSCLE_RELAXED_KEY)) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.BONE_BLOCK_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.getPersistentData().putBoolean(MUSCLE_RELAXED_KEY, true);
                }
            }
        }

        // ---- 初应用处理（仅全新施加；对应 1.21.1 MobEffect.onEffectAdded，双侧触发） ----
        if (event.getOldEffectInstance() != null) {
            return;
        }
        // 禁用生物AI
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }

        // 锁位模式下强制解除骑乘关系，防止被坐骑带动移动
        if (ConfigSyncHandler.dingShenLockPosition()) {
            entity.stopRiding();
        }

        // 播放声音（烈焰人受伤）
        DingShenEffect.playSound(entity.level(), entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_HURT);

        // 生成粒子 + 为玩家手动生成"定"字展示实体（原版 /ride 指令不支持实体骑乘玩家）
        if (entity.level() instanceof ServerLevel serverLevel) {
            DingShenEffect.spawnParticles(serverLevel, entity);
            if (entity instanceof ServerPlayer) {
                DingShenEffect.spawnDingShenDisplay(entity, serverLevel);
            }
        }
    }

    /**
     * 检查受害者的定身状态效果
     * 定身时间过长会导致"肌肉松弛"，承受更多伤害
     */
    @SubscribeEvent
    public static void onLivingDamage(@NotNull LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        float originalDamage = event.getAmount();
        float multiplier = 1.0f;
        // 肌肉松弛：被施加超过 50 秒定身的实体，全程承受额外 50% 伤害（直至本次效果结束）
        if (victim.getPersistentData().getBoolean(MUSCLE_RELAXED_KEY)) {
            multiplier *= 1.5f;
        }

        if (multiplier != 1.0f) {
            event.setAmount(originalDamage * multiplier);
        }

        // 粉碎机制：单次受到的伤害超过最大血量（服务端配置比例，默认 30%）时，定身效果被"粉碎"
        if (victim.hasEffect(WKEffects.DING_SHEN.get())
                && event.getAmount() > victim.getMaxHealth() * ConfigSyncHandler.dingShenShatterRatio()) {
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
        victim.removeEffect(WKEffects.DING_SHEN.get());

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
        // （1.20.1 稳妥起见用 unwrapKey 取 RL 比较，不依赖 Holder.is(ResourceLocation)）
        ResourceLocation speciesId = species.unwrapKey().map(key -> key.location()).orElse(null);
        if (!new ResourceLocation("dragonsurvival", "wing_kirin").equals(speciesId)
                && !ConfigSyncHandler.shatterBuffForNonWingKirin()) {
            return;
        }
        // 不破不立：生命恢复Ⅲ+魔源涌动Ⅱ
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 2));
        player.addEffect(new MobEffectInstance(DSEffects.SOURCE_OF_MAGIC.get(), 100, 1));
    }

    /**
     * 带定身效果生物死亡时的清理（对应 1.21.1 的 MobEffect.onMobRemoved —— 1.20.1 无此回调）。
     */
    @SubscribeEvent
    public static void onLivingDeath(@NotNull LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(WKEffects.DING_SHEN.get())) {
            DingShenEffect.handleExpireOrRemoval(entity);
        }
    }

    /**
     * 玩家退出服务器时清理"定"字展示实体。
     * <p>
     * 原版 PlayerList.remove 以 UNLOADED_WITH_PLAYER 原因移除玩家，而 LivingEntity.remove
     * 仅对 KILLED/DISCARDED 触发效果清理（1.21.1 的 onMobRemoved），因此退出服务器时须在此事件中手动清理，
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
        if (ConfigSyncHandler.dingShenLockPosition()) {
            Entity entity = event.getEntity();
            if (entity instanceof LivingEntity living && living.hasEffect(WKEffects.DING_SHEN.get())) {
                event.setCanceled(true);
            }
            return;
        }
        // 未锁位：穿越前清理旧维度的展示实体
        if (event.getEntity() instanceof LivingEntity living) {
            DingShenEffect.cleanupPlayerDisplay(living);
        }
    }

    /**
     * 挖掘封锁：定身效果下挖掘速度置 0（1.21.1 为 MINING_EFFICIENCY 属性 -100%，1.20.1 无该属性）。
     */
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.@NotNull BreakSpeed event) {
        if (event.getEntity().hasEffect(WKEffects.DING_SHEN.get())) {
            event.setNewSpeed(0.0F);
        }
    }

    /**
     * 定身抗性附魔：护甲槽装备变化时汇总附魔等级并刷新属性修饰符。
     * （1.21.1 为附魔数据包的属性效果组件——每级 +0.2 定身抗性；1.20.1 无此机制，改用事件实现）
     */
    @SubscribeEvent
    public static void onEquipmentChange(@NotNull LivingEquipmentChangeEvent event) {
        // 1.20.1 的 EquipmentSlot.Type 只有 HAND/ARMOR（HUMANOID_ARMOR 为 1.20.5+）
        if (event.getSlot().getType() != EquipmentSlot.Type.ARMOR) {
            return;
        }
        updateDingshenResistance(event.getEntity());
    }

    /**
     * 实体携带已附魔护甲进入世界时（如刷怪蛋生成的带甲生物）补齐一次属性刷新。
     */
    @SubscribeEvent
    public static void onEntityJoinLevel(@NotNull EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            updateDingshenResistance(living);
        }
    }

    private static void updateDingshenResistance(@NotNull LivingEntity entity) {
        AttributeInstance attr = entity.getAttribute(WKAttributes.DINGSHEN_EFFECT_RESISTANCE.get());
        if (attr == null) {
            return; // 仅玩家挂载了该属性（与 1.21.1 一致），其余实体直接跳过
        }
        int totalLevel = 0;
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            totalLevel += EnchantmentHelper.getItemEnchantmentLevel(
                    WKEnchantments.DINGSHEN_RESISTANCE.get(), entity.getItemBySlot(slot));
        }
        attr.removeModifier(DINGSHEN_RESISTANCE_MODIFIER_UUID);
        if (totalLevel > 0) {
            // 每级 +0.2（同 1.21.1 数据包定义），上限 1.0（RangedAttribute 亦有限幅）
            attr.addTransientModifier(new AttributeModifier(
                    DINGSHEN_RESISTANCE_MODIFIER_UUID,
                    "wing_kirin:dingshen_resistance",
                    Math.min(totalLevel * 0.2, 1.0),
                    AttributeModifier.Operation.ADDITION));
        }
    }
}
