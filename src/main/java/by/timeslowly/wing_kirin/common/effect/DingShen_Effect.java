package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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

// 定身 药水效果
public class DingShen_Effect extends MobEffect {
    public DingShen_Effect(MobEffectCategory category, int color) {
        super(category, color);
        // 属性修改
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_1"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_2"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 重力
        this.addAttributeModifier(Attributes.GRAVITY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_3"), 0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 挖掘速度
        this.addAttributeModifier(Attributes.MINING_EFFICIENCY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_4"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    // 每刻持续应用的效果
    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        // 阻止移动
        entity.setDeltaMovement(new Vec3(0, 0, 0));

        // 冷却
        if (entity instanceof Player _player) {
            _player.getCooldowns().addCooldown(_player.getMainHandItem().getItem(), 10);
        }

        // 关闭GUI
        if (Mth.nextInt(RandomSource.create(), 1, 50) == 1) {
            if (entity instanceof Player _player)
                _player.closeContainer();
        }

        return super.applyEffectTick(entity, amplifier);
    }

    // 生物获得效果的瞬间
    @Override
    public void onEffectAdded(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);
        // 将实体AI禁用
        if (entity instanceof Mob mob) {
            mob.setNoAi(true);
        }

        if (entity.level() instanceof Level _level) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            // 烈焰人受伤声
            if (!_level.isClientSide()) {
                _level.playSound(null,
                        BlockPos.containing(x, y, z),
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.hurt"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2);
            }
            else {
                _level.playLocalSound(x, y, z,
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.blaze.hurt"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2,
                        false);
            }
            // 生成粒子
            if (entity.level() instanceof ServerLevel level) {
                Vector3f color = new Vector3f(0.98F,0.86F,0.57F); // 粒子颜色
                float scale = 1.0F; // 粒子大小
                level.sendParticles(
                        new DustParticleOptions(color, scale),
                        x,
                        y + entity.getBbHeight() / 2.0F,
                        z,
                        200,
                        1, 1, 1,
                        0.0F
                );
            }
        }
    }

    // 生物死亡时时执行一次
    @Override
    public void onMobRemoved(@NotNull LivingEntity entity, int amplifier, Entity.@NotNull RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        // 将实体AI恢复
        if (entity instanceof Mob mob) {
            mob.setNoAi(false);
        }

        if (entity.level() instanceof Level _level) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            // 播放声音
            if (!_level.isClientSide()) {
                _level.playSound(null,
                        BlockPos.containing(x, y, z),
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2);
            }
            else {
                _level.playLocalSound(x, y, z,
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2,
                        false);
            }
            // 生成粒子
            if (entity.level() instanceof ServerLevel level) {
                Vector3f color = new Vector3f(0.98F,0.86F,0.57F); // 粒子颜色
                float scale = 1.0F; // 粒子大小
                level.sendParticles(
                        new DustParticleOptions(color, scale),
                        x,
                        y + entity.getBbHeight() / 2.0F,
                        z,
                        200,
                        1, 1, 1,
                        0.0F
                );
            }
        }
    }

    // 效果消失时执行一次
    public static void onEffectExpired(LivingEntity entity) {
        // 将实体AI恢复
        if (entity instanceof Mob mob) {
            mob.setNoAi(false);
        }

        if (entity.level() instanceof Level _level) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
            // 播放声音
            if (!_level.isClientSide()) {
                _level.playSound(null,
                        BlockPos.containing(x, y, z),
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2);
            } else {
                _level.playLocalSound(x, y, z,
                        Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.respawn_anchor.deplete"))),
                        SoundSource.PLAYERS,
                        2,
                        (float) 1.2,
                        false);
            }

            // 生成粒子
            if (entity.level() instanceof ServerLevel level) {
                Vector3f color = new Vector3f(0.98F,0.86F,0.57F); // 粒子颜色
                float scale = 1.0F; // 粒子大小
                level.sendParticles(
                        new DustParticleOptions(color, scale),
                        x,
                        y + entity.getBbHeight() / 2.0F,
                        z,
                        200,
                        1, 1, 1,
                        0.0F
                );
            }
        }
    }
}
