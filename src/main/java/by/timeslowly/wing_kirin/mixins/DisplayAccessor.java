package by.timeslowly.wing_kirin.mixins;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

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
