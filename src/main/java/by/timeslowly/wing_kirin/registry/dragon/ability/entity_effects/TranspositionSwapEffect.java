package by.timeslowly.wing_kirin.registry.dragon.ability.entity_effects;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.entity_effects.AbilityEntityEffect;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * 换位·位置交换（{@code wing_kirin:transposition_swap}，无字段效果）。
 * <p>
 * 加在 looking_at 目标选择中（执行者参数 dragon = 施法者，target = 被视线选中的实体）：
 * 若施法者已就绪（{@link TranspositionMarkEffect}），交换两者位置——
 * 目标传至施法者旧坐标（保持自身朝向），施法者传至目标坐标并转向自身原视线的完全反向，
 * 随后在施法者新位置播放音效与云粒子。
 * <p>
 * 移植说明：替代 1.21.1 的 {@code run_function -> main.mcfunction} + {@code tp_target.mcfunction}。
 * 唯一的宏行 {@code $tp @s $(x) $(y) $(z)}（1.20.2+）由 {@link Entity#teleportTo(double, double, double)}
 * （仅改位置、保持朝向，语义一致）取代；玩家的转体 180° 由
 * {@link ServerPlayer#teleportTo(ServerLevel, double, double, double, float, float)} 以目标朝向 + 180° 实现
 * （等价原 {@code rotated as @s} + {@code ~180} 的执行上下文）。
 * 音效替换：原 {@code entity.wind_charge.wind_burst} 为 1.20.5+ 音效，1.20.1 改用末影人传送音。
 */
public record TranspositionSwapEffect() implements AbilityEntityEffect {
    public static final TranspositionSwapEffect INSTANCE = new TranspositionSwapEffect();
    public static final MapCodec<TranspositionSwapEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public void apply(final ServerPlayer dragon, final DragonAbilityInstance ability, final Entity target) {
        Vec3 readyPos = TranspositionMarkEffect.takeReady(dragon);
        // 无就绪标记（理论上不会发生：同一次触发的 self action 必先执行）则跳过
        if (readyPos == null || target == dragon) {
            return;
        }

        // 先捕获目标的当前坐标（交换的目的地）——必须在移动目标之前读取，
        // 否则目标移动后 getX/Y/Z 返回的已是施法者旧坐标，施法者会被传回原地
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();

        // 施法者换位前的视角（~180 相对玩家自身旋转在客户端应用，最终朝向 = 原视线完全反向）
        float playerYaw = dragon.getYRot();
        float playerPitch = dragon.getXRot();

        // 目标传至施法者旧坐标（仅改位置、保持朝向，等价原宏 tp @s $(x) $(y) $(z)）
        target.teleportTo(readyPos.x, readyPos.y, readyPos.z);

        // 施法者传至目标坐标并转体 180°（等价 tp @s ~ ~ ~ ~180 ~：相对玩家自身旋转，
        // 与 1.21.1 的玩家视角完全反向一致；非目标朝向）
        dragon.teleportTo(dragon.serverLevel(), targetX, targetY, targetZ,
                playerYaw + 180.0F, playerPitch);

        // 音效（1.20.1 无 entity.wind_charge.wind_burst，以末影人传送音替代）
        ServerLevel level = dragon.serverLevel();
        level.playSound(null, dragon.getX(), dragon.getY(), dragon.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 1.0F, 1.0F);

        // 云粒子（施法者新位置上方一格）
        level.sendParticles(ParticleTypes.CLOUD, dragon.getX(), dragon.getY() + 1.0D, dragon.getZ(),
                10, 0.5D, 0.5D, 0.5D, 0.1D);
    }

    @Override
    public MapCodec<? extends AbilityEntityEffect> entityCodec() {
        return CODEC;
    }
}
