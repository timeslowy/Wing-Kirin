package by.timeslowly.wing_kirin.mixin;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.core.object.Color;

/**
 * 自 1.21.1 平移；1.20.1 的 GeckoLib 4 中 Color 位于 core.object 包（已验证字段 color/pastDelay 存在）。
 * 仅客户端：登记于 mixins.json 的 client 数组。
 */
@Mixin(MagicHUD.OutlineColorData.class)
public interface OutlineColorDataAccessor {
    // 颜色
    @Accessor("color")
    void setColor(Color color);

    // 延迟
    @Accessor("pastDelay")
    void setPastDelay(boolean pastDelay);
}
