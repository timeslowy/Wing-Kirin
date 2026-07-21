package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import by.timeslowly.wing_kirin.mixins.DisplayAccessor;
import by.timeslowly.wing_kirin.mixins.ItemDisplayAccessor;
import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Brightness;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Objects;

// 定身药水效果
public class DingShenEffect extends MobEffect {
    /**
     * 修改发光效果的逻辑见： {@link by.timeslowly.wing_kirin.mixins.EntityGlowColorMixin} 和 {@link by.timeslowly.wing_kirin.network.EffectSyncHandler}
     */
    public DingShenEffect(MobEffectCategory category, int color) {
        super(category, color);
        // 属性修改
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_1"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_2"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 重力
        this.addAttributeModifier(Attributes.GRAVITY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_3"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        // 挖掘速度
        this.addAttributeModifier(Attributes.MINING_EFFICIENCY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_4"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    /**
     * 修改原版交互和视觉效果的逻辑见： {@link by.timeslowly.wing_kirin.common.eventhandler.EffectEventHandler}
     */
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        // 阻止移动
        entity.setDeltaMovement(Vec3.ZERO);

        // 随机关闭容器 GUI（使用实体自身的随机源），不会关闭游戏菜单（暂停界面）
        int chance = WKServerConfig.getDingShenCloseGuiChance();
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

        return super.applyEffectTick(entity, amplifier);
    }

    /**
     * 禁用龙生法力系统的逻辑见： {@link by.timeslowly.wing_kirin.mixins.DragonAbilityInstanceMixin} 和 {@link by.timeslowly.wing_kirin.mixins.MagicHUDMixin}
     */
    // 效果初应用时
    @Override
    public void onEffectAdded(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        // 禁用生物AI
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }

        // 锁位模式下强制解除骑乘关系，防止被坐骑带动移动
        if (WKServerConfig.shouldDingShenLockPosition()) {
            entity.stopRiding();
        }

        Level level = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        // 播放声音（烈焰人受伤）
        SoundEvent sound = Objects.requireNonNull(
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.hurt")));
                playSound(level, x, y, z, sound);

        // 生成粒子
        if (level instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel, entity);
        }

        // 为玩家手动生成"定"字展示实体（原版 /ride 指令不支持实体骑乘玩家）
        if (entity instanceof Player && level instanceof ServerLevel serverLevel) {
            spawnDingShenDisplay(entity, serverLevel);
        }
    }

    // 带效果生物被杀死时
    @Override
    public void onMobRemoved(@NotNull LivingEntity entity, int amplifier, Entity.@NotNull RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        handleExpireOrRemoval(entity);
    }

    // 效果消失时的处理（由事件调用）
    public static void onEffectExpired(LivingEntity entity) {
        handleExpireOrRemoval(entity);
    }

    // ---------- 以下为提取的辅助方法 ----------

    private static void handleExpireOrRemoval(LivingEntity entity) {
        restoreAi(entity);

        // 清理玩家身上的"定"字展示实体
        if (entity instanceof Player && entity.level() instanceof ServerLevel serverLevel) {
            int displayId = entity.getPersistentData().getInt(DISPLAY_ID_KEY);
            if (displayId != 0) {
                Entity display = serverLevel.getEntity(displayId);
                if (display != null) {
                    display.discard();
                }
                entity.getPersistentData().remove(DISPLAY_ID_KEY);
            }
        }

        Level level = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        // 播放声音（重生锚消耗）
        SoundEvent sound = Objects.requireNonNull(
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete")));
        playSound(level, x, y, z, sound);

        // 生成粒子
        if (level instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel, entity);
        }
    }

    // 恢复AI
    private static void restoreAi(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            mob.setNoAi(false);
        }
    }

    private static final String DISPLAY_ID_KEY = "DingShenDisplayId";

    // 为玩家生成"定"字展示实体（不采用骑乘，改由 applyEffectTick 每 tick 传送到玩家头顶）
    private static void spawnDingShenDisplay(@NotNull LivingEntity entity, ServerLevel level) {
        // 已有展示实体则不再生成
        int existingId = entity.getPersistentData().getInt(DISPLAY_ID_KEY);
        if (existingId != 0 && level.getEntity(existingId) != null) {
            return;
        }

        Display.ItemDisplay display = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, level);
        display.setPos(entity.getX(), entity.getY() + entity.getBbHeight() * 0.75, entity.getZ());

        // 物品：烟火之星 + 自定义模型数据 12020000 → "定"字贴图
        ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(12020000));
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
    private static void playSound(@NotNull Level level, double x, double y, double z, SoundEvent sound) {
        if (level.isClientSide()) {
            level.playLocalSound(x, y, z, sound, SoundSource.PLAYERS, (float) 2.0, (float) 1.2, false);
        } else {
            level.playSound(null, BlockPos.containing(x, y, z), sound, SoundSource.PLAYERS, (float) 2.0, (float) 1.2);
        }
    }

    // 粒子参数
    private static void spawnParticles(@NotNull ServerLevel level, @NotNull LivingEntity entity) {
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