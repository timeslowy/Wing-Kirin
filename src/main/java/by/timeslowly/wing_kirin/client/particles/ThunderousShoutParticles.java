package by.timeslowly.wing_kirin.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 注意：仅客户端
@OnlyIn(Dist.CLIENT)
public class ThunderousShoutParticles extends SimpleAnimatedParticle {
    protected ThunderousShoutParticles(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, x, y, z, spriteSet, 0.0F);
        // x、y、z代表此粒子可被赋予速度
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.lifetime = 20;
        this.quadSize = 0.3F;
        // 设置渐隐色
        this.setFadeColor(15916745);
        // 必须存在
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize += 0.2F; // 每刻增大尺寸
    }

    // 检测碰撞
    @Override
    public void move(double x, double y, double z) {
        double oldX = this.x;
        double oldY = this.y;
        double oldZ = this.z;
        super.move(x, y, z);
        // 如果实际移动距离与预期移动距离不符，说明发生了碰撞，直接移除粒子
        if (Math.abs(oldX + x - this.x) > 0.001 || Math.abs(oldY + y - this.y) > 0.001 || Math.abs(oldZ + z - this.z) > 0.001) {
            this.remove();
        }
    }

    // 提供纹理图集
    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel,
                                                 double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ThunderousShoutParticles(clientLevel, pX, pY ,pZ, pXSpeed ,pYSpeed, pZSpeed ,this.spriteSet);
        }

    }
}
