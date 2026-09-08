package by.timeslowly.wing_kirin.mixin;

import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 与 1.21.1 相同：1.20.1 的 ItemDisplay.setItemStack 为包私有、setItemTransform 为 private
 * （已对源码验证），Invoker 直接平移。
 */
@Mixin(Display.ItemDisplay.class)
public interface ItemDisplayAccessor {
    @Invoker("setItemStack")
    void invokeSetItemStack(ItemStack stack);

    @Invoker("setItemTransform")
    void invokeSetItemTransform(ItemDisplayContext transform);
}
