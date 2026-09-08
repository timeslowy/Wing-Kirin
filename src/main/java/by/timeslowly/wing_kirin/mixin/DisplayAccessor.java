package by.timeslowly.wing_kirin.mixin;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 与 1.21.1 相同：1.20.1 的 Display 各 setter 同样为 private（已对源码验证），Invoker 直接平移。
 */
@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void invokeSetTransformation(Transformation transformation);

    @Invoker("setBillboardConstraints")
    void invokeSetBillboardConstraints(Display.BillboardConstraints constraints);

    @Invoker("setBrightnessOverride")
    void invokeSetBrightnessOverride(Brightness brightness);

    @Invoker("setGlowColorOverride")
    void invokeSetGlowColorOverride(int color);
}
