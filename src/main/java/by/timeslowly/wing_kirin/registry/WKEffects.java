package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.common.effect.AmnesiaEffect;
import by.timeslowly.wing_kirin.common.effect.GreatZhengqiEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 注册药水效果（自 1.21.1 NeoForge 分支移植，仅含本次移植的两个效果）。
 * 注册时机：主类构造器中调用 {@link #register(IEventBus)}。
 * 注：1.21.1 版本中的 MobEffectEvent.Remove/Expired 订阅（定身、唯快不破的到期行为）
 * 属于其他效果，未随本次移植；届时补 @Mod.EventBusSubscriber(MOD) 注解即可。
 * TODO:还剩至少两个
 */
public class WKEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            ForgeRegistries.MOB_EFFECTS, WingKirin.MODID
    );

    // 浩然正气（BENEFICIAL，颜色同 1.21.1：16506002）
    public static final RegistryObject<MobEffect> GREAT_ZHENGQI = MOB_EFFECTS.register("great_zhengqi",
            () -> new GreatZhengqiEffect(MobEffectCategory.BENEFICIAL, 16506002));

    // 失神/失忆（HARMFUL，颜色同 1.21.1：0x9FB3C8）
    public static final RegistryObject<MobEffect> AMNESIA = MOB_EFFECTS.register("amnesia",
            () -> new AmnesiaEffect(MobEffectCategory.HARMFUL, 0x9FB3C8));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
