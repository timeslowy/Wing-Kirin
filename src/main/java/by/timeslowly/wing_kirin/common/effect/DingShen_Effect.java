package by.timeslowly.wing_kirin.common.effect;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// 定身 药水效果
// TODO:太多了，见发的文档
public class DingShen_Effect extends MobEffect {
    public DingShen_Effect(MobEffectCategory category, int color) {
        super(category, color);
        // 属性修改
        // 攻击速度
        this.addAttributeModifier(Attributes.ATTACK_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_0"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 移动速度
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_1"), -1,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 重力
        this.addAttributeModifier(Attributes.GRAVITY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_2"), 0,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        // 挖掘速度
        this.addAttributeModifier(Attributes.MINING_EFFICIENCY,
                ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "effect.ding_shen_3"), -1,
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
        if (Mth.nextInt(RandomSource.create(), 1, 20) == 1) {
            if (entity instanceof Player _player)
                _player.closeContainer();
        }

        return super.applyEffectTick(entity, amplifier);
    }
    // 生物死亡时时执行一次
    @Override
    public void onMobRemoved(@NotNull LivingEntity entity, int amplifier, Entity.@NotNull RemovalReason reason) {
        super.onMobRemoved(entity, amplifier, reason);
        if (entity.level() instanceof Level _level) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
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
        }
    }
    // 效果消失时执行一次
    public static void onEffectExpired(LivingEntity entity, int amplifier) {
        if (entity.level() instanceof Level _level) {
            double x = entity.getX();
            double y = entity.getY();
            double z = entity.getZ();
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
        }
    }
}
