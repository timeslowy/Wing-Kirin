package by.timeslowly.wing_kirin.mixins;

import by.dragonsurvivalteam.dragonsurvival.client.gui.hud.MagicHUD;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import software.bernie.geckolib.util.Color;

@Mixin(MagicHUD.OutlineColorData.class)
public interface OutlineColorDataAccessor {
    @Accessor("color")
    void setColor(Color color);

    @Accessor("pastDelay")
    void setPastDelay(boolean pastDelay);
}