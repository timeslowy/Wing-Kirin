package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.eventhandler.effects.DingshenEffectEventHandler;
import by.timeslowly.wing_kirin.network.ConfigSyncHandler;
import by.timeslowly.wing_kirin.mixin.DisplayAccessor;
import by.timeslowly.wing_kirin.mixin.ItemDisplayAccessor;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Brightness;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * 定身药水效果（自 1.21.1 NeoForge 分支移植）。
 * <p>
 * 1.20.1 适配：
 * <ul>
 *   <li>addAttributeModifier 接收 UUID 字符串（同 UnstoppableSpeedEffect 的 nameUUIDFromBytes 派生法，
 *       与 1.21.1 的 RL 字符串同串同 UUID）；操作数枚举名为 MULTIPLY_TOTAL；</li>
 *   <li>1.20.1 无 Attributes.GRAVITY → ForgeMod.ENTITY_GRAVITY；
 *       无 Attributes.MINING_EFFICIENCY → 挖掘封锁改由 {@link DingshenEffectEventHandler} 的
 *       PlayerEvent.BreakSpeed 订阅实现（newSpeed 置 0，语义等同 -100%）；</li>
 *   <li>1.20.1 MobEffect 无 onEffectAdded / onMobRemoved 回调 → 效果施加（NoAI/解除骑乘/音效粒子/
 *       展示实体生成）与死亡清理均移入 {@link DingshenEffectEventHandler} 的 Forge 事件；</li>
 *   <li>shouldApplyEffectTickThisTick → isDurationEffectTick（签名一致）；</li>
 *   <li>数据组件（CUSTOM_MODEL_DATA）→ NBT CustomModelData；WKAttachments 肌肉松弛标记 →
 *       getPersistentData()（见 {@link DingshenEffectEventHandler#MUSCLE_RELAXED_KEY}）。</li>
 * </ul>
 * 修改发光效果的逻辑见： {@link by.timeslowly.wing_kirin.mixin.EntityGlowColorMixin} 和
 * {@link by.timeslowly.wing_kirin.network.EffectSyncHandler}
 */
public class DingShenEffect extends MobEffect {

    /** 玩家持久 NBT 中"定"字展示实体的实体 ID 键（1.21.1 同名） */
    public static final String DISPLAY_ID_KEY = "DingShenDisplayId";

    public DingShenEffect(MobEffectCategory category, int color) {
        super(category, color);
        // 属性修改（1.21.1 的 wing_kirin:effect.ding_shen_N 同串派生 UUID）
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                uuidFromId("effect.ding_shen_1"), -1,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                uuidFromId("effect.ding_shen_2"), -1,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        // 重力（1.20.1 无原版属性，用 Forge 补充的实体重力属性）
        this.addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(),
                uuidFromId("effect.ding_shen_3"), -1,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        // 挖掘速度：1.20.1 无 MINING_EFFICIENCY 属性，见 DingshenEffectEventHandler.onBreakSpeed
    }

    /** 以 1.21.1 的修饰符 RL（wing_kirin:<id>）派生固定 UUID，两分支同串同 UUID */
    private static String uuidFromId(@NotNull String id) {
        return UUID.nameUUIDFromBytes((WingKirin.MODID + ":" + id).getBytes()).toString();
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    /**
     * 修改原版交互和视觉效果的逻辑见： {@link DingshenEffectEventHandler}
     */
    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        // 阻止移动
        entity.setDeltaMovement(Vec3.ZERO);

        // 随机关闭容器 GUI（使用实体自身的随机源），不会关闭游戏菜单（暂停界面）
        int chance = ConfigSyncHandler.dingShenCloseGuiChance();
        if (chance > 0 && entity.getRandom().nextInt(100) < chance) {
            if (entity instanceof Player player) {
                // 仅关闭实际打开的容器（箱子、熔炉等），不关闭游戏菜单/暂停界面
                if (player.containerMenu != player.inventoryMenu) {
                    player.closeContainer();
                }
            }
        }

        // 将玩家的"定"字展示实体传送到头顶
        if (entity instanceof Player && entity.level() instanceof ServerLevel serverLevel) {
            int displayId = entity.getPersistentData().getInt(DISPLAY_ID_KEY);
            if (displayId != 0) {
                Entity display = serverLevel.getEntity(displayId);
                if (display != null) {
                    display.setPos(entity.getX(), entity.getY() + entity.getBbHeight() * 0.9, entity.getZ());
                }
            }
        }

        super.applyEffectTick(entity, amplifier);
    }

    /**
     * 禁用龙生法力系统的逻辑见： {@link by.timeslowly.wing_kirin.mixin.DragonAbilityInstanceMixin} 和
     * {@link by.timeslowly.wing_kirin.mixin.MagicHUDMixin}
     * <p>
     * 效果初应用时的处理（NoAI/解除骑乘/音效/粒子/玩家展示实体生成）见
     * {@link DingshenEffectEventHandler#onDingShenAdded}（1.20.1 MobEffect 无 onEffectAdded 回调）。
     */

    // 效果消失时的处理（由事件调用：MobEffectEvent.Remove / Expired；死亡走 LivingDeathEvent）
    public static void onEffectExpired(LivingEntity entity) {
        handleExpireOrRemoval(entity);
    }

    // ---------- 以下为提取的辅助方法 ----------

    /**
     * 效果结束（到期/移除/死亡）的统一清理：清除「肌肉松弛」标记、恢复AI、清理玩家展示实体、
     * 播放结束音效与粒子。1.20.1 由 DingshenEffectEventHandler 的各事件调用（幂等可重复执行）。
     */
    public static void handleExpireOrRemoval(@NotNull LivingEntity entity) {
        // 清除「肌肉松弛」标记（1.21.1 为 WKAttachments，1.20.1 用持久 NBT）
        entity.getPersistentData().remove(DingshenEffectEventHandler.MUSCLE_RELAXED_KEY);

        restoreAi(entity);

        // 清理玩家身上的"定"字展示实体
        cleanupPlayerDisplay(entity);

        Level level = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        // 播放声音（重生锚消耗；1.20.1 中该常量为 Holder.Reference<SoundEvent>，取 value()）
        playSound(level, x, y, z, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value());

        // 生成粒子
        if (level instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel, entity);
        }
    }

    // 恢复AI
    private static void restoreAi(LivingEntity entity) {
        if (entity instanceof net.minecraft.world.entity.Mob mob) {
            mob.setNoAi(false);
        }
    }

    /**
     * 清理玩家身上的"定"字展示实体。
     * <p>
     * 效果结束/死亡/粉碎（handleExpireOrRemoval）、退出服务器（PlayerLoggedOutEvent）、
     * 维度穿越（EntityTravelToDimensionEvent）时均需调用，幂等可重复执行。
     */
    public static void cleanupPlayerDisplay(LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return;
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
            int displayId = entity.getPersistentData().getInt(DISPLAY_ID_KEY);
            if (displayId != 0) {
                Entity display = serverLevel.getEntity(displayId);
                if (display != null) {
                    display.discard();
                }
                entity.getPersistentData().remove(DISPLAY_ID_KEY);
            }
        }
    }

    // 为玩家生成"定"字展示实体（不采用骑乘，改由 applyEffectTick 每 tick 传送到玩家头顶）
    // 1.20.1 由 DingshenEffectEventHandler.onDingShenAdded 在效果初应用时调用
    public static void spawnDingShenDisplay(@NotNull LivingEntity entity, ServerLevel level) {
        // 已有展示实体则不再生成
        int existingId = entity.getPersistentData().getInt(DISPLAY_ID_KEY);
        if (existingId != 0 && level.getEntity(existingId) != null) {
            return;
        }

        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        display.setPos(entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ());

        // 物品：烟火之星 + 自定义模型数据 12020000 → "定"字贴图（1.20.1 用 NBT，无数据组件）
        ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
        itemStack.getOrCreateTag().putInt("CustomModelData", 12020000);
        ((ItemDisplayAccessor) display).invokeSetItemStack(itemStack);
        ((ItemDisplayAccessor) display).invokeSetItemTransform(ItemDisplayContext.HEAD);

        // 变换：缩放 1.2,1.2,0.5 / 旋转默认 / 平移默认
        Transformation transformation = new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(1.2f, 1.2f, 0.5f),
                new Quaternionf(0, 0, 0, 1)
        );
        ((DisplayAccessor) display).invokeSetTransformation(transformation);
        // 垂直告示牌模式
        ((DisplayAccessor) display).invokeSetBillboardConstraints(Display.BillboardConstraints.VERTICAL);
        // 满亮度
        ((DisplayAccessor) display).invokeSetBrightnessOverride(Brightness.FULL_BRIGHT);
        // 发光 + 自定义发光颜色
        display.setGlowingTag(true);
        ((DisplayAccessor) display).invokeSetGlowColorOverride(16769841);

        level.addFreshEntity(display);

        // 存储 ID 以便后续传送和清理
        entity.getPersistentData().putInt(DISPLAY_ID_KEY, display.getId());
    }

    // 声音参数
    public static void playSound(@NotNull Level level, double x, double y, double z, SoundEvent sound) {
        if (level.isClientSide()) {
            level.playLocalSound(x, y, z, sound, SoundSource.PLAYERS, (float) 2.0, (float) 1.2, false);
        } else {
            level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.PLAYERS, (float) 2.0, (float) 1.2);
        }
    }

    // 粒子参数
    public static void spawnParticles(@NotNull ServerLevel level, @NotNull LivingEntity entity) {
        Vector3f color = new Vector3f(0.98F, 0.86F, 0.57F);
        float scale = 1.0F;
        level.sendParticles(
                new DustParticleOptions(color, scale),
                entity.getX(),
                entity.getY() + entity.getBbHeight() / 2.0F,
                entity.getZ(), 200, 1.0, 1.0, 1.0, 0.0
        );
    }
}
