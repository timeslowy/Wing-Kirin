package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MagicHUD.OutlineColorData.class)
public interface OutlineColorDataAccessor {
    // 颜色（26.1 起为 int ARGB）
    @Accessor("color")
    void setColor(int color);

    // 延迟
    @Accessor("pastDelay")
    void setPastDelay(boolean pastDelay);
}