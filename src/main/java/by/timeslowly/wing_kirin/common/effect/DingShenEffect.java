package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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

        // 随机关闭 GUI（使用实体自身的随机源）
        if (entity.getRandom().nextInt(1, 101) == 1) {  // nextInt(1,51) 等价于原 1~50 范围
            if (entity instanceof Player player) {
                player.closeContainer();
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
                entity.getZ(),
                200,
                1.0, 1.0, 1.0,
                0.0
        );
    }
}