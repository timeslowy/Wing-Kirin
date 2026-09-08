package by.timeslowly.wing_kirin.network;

import by.timeslowly.wing_kirin.WingKirin;
import by.timeslowly.wing_kirin.config.WKServerConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

/**
 * 服务端配置 → 客户端 同步（1.20.1 新增，1.21.1 分支无此机制）。
 * <p>
 * 背景：客户端侧代码（mixin / 客户端事件）原先直接读 {@link WKServerConfig}（SERVER 类型）。
 * Forge 的 SERVER 配置在集成服务端停止时由 {@code ServerLifecycleHooks.handleServerStopped}
 * → {@code ConfigTracker.unloadConfigs(SERVER)} 卸载（spec.childConfig 置 null），而
 * {@code Minecraft.clearLevel} 会在此后继续 {@code runTick} → {@code MouseHandler.turnPlayer}，
 * 于是开发环境下 {@code ConfigValue.get()} 直接抛 IllegalStateException（退出世界必崩）。
 * 连专用服务器时客户端的 SERVER spec 更是从未加载（仅 vanilla 连接会填默认值），
 * 生产环境虽不崩但只能拿到本地默认值，配置根本不生效。
 * <p>
 * 方案：服务端在玩家登录与配置重载时下发一份完整快照，客户端缓存后只读缓存；
 * 未收到快照时回落到「本地 SERVER 配置（若已加载）/ 常量默认值」，因此服务端侧取值不受影响，
 * 且退出世界期间不再触碰已卸载的 spec。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigSyncHandler {

    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WingKirin.MODID, "config"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    /** 客户端侧最近一次收到的配置快照；服务端侧与尚未收到包时为 null */
    private static volatile Snapshot clientSnapshot = null;

    /** 注册网络包（主类构造阶段调用一次） */
    public static void init() {
        CHANNEL.registerMessage(0, Snapshot.class, Snapshot::encode, Snapshot::decode, Snapshot::handle);
    }

    /** 玩家登录（Forge 主总线，由主类注册） */
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendTo(player);
        }
    }

    /** SERVER 配置被重载（如 Configured 游戏内改配置）→ 广播给所有在线玩家（MOD 总线） */
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        ModConfig config = event.getConfig();
        if (config.getType() != ModConfig.Type.SERVER || !WingKirin.MODID.equals(config.getModId())) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                sendTo(player);
            }
        }
    }

    private static void sendTo(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), Snapshot.fromServer());
    }

    // ---------- 双端通用取值：客户端优先用同步快照，否则回落本地配置 / 默认值 ----------

    public static boolean dingShenDisableAbilities() {
        Snapshot s = clientSnapshot;
        return s != null ? s.disableAbilities : serverOrDefaultBool(WKServerConfig::shouldDingShenDisableAbilities, true);
    }

    public static boolean dingShenDisablePassiveAbilities() {
        Snapshot s = clientSnapshot;
        return s != null ? s.disablePassiveAbilities : serverOrDefaultBool(WKServerConfig::shouldDingShenDisablePassiveAbilities, false);
    }

    public static boolean dingShenDisableInteraction() {
        Snapshot s = clientSnapshot;
        return s != null ? s.disableInteraction : serverOrDefaultBool(WKServerConfig::shouldDingShenDisableInteraction, true);
    }

    public static int dingShenCloseGuiChance() {
        Snapshot s = clientSnapshot;
        return s != null ? s.closeGuiChance : serverOrDefaultInt(WKServerConfig::getDingShenCloseGuiChance, 1);
    }

    public static boolean dingShenDisableLookRotation() {
        Snapshot s = clientSnapshot;
        return s != null ? s.disableLookRotation : serverOrDefaultBool(WKServerConfig::shouldDingShenDisableLookRotation, false);
    }

    public static boolean dingShenLockPosition() {
        Snapshot s = clientSnapshot;
        return s != null ? s.lockPosition : serverOrDefaultBool(WKServerConfig::shouldDingShenLockPosition, false);
    }

    public static boolean shatterBuffForNonWingKirin() {
        Snapshot s = clientSnapshot;
        return s != null ? s.shatterBuffForNonWingKirin : serverOrDefaultBool(WKServerConfig::shouldProvideShatterBuffToNonWingKirinDragons, false);
    }

    public static double dingShenShatterRatio() {
        Snapshot s = clientSnapshot;
        return s != null ? s.shatterRatio : serverOrDefaultDouble(WKServerConfig::getDingShenShatterRatio, 0.3);
    }

    public static boolean greatZhengqiIgnoreManaCost() {
        Snapshot s = clientSnapshot;
        return s != null ? s.ignoreManaCost : serverOrDefaultBool(WKServerConfig::shouldGreatZhengqiIgnoreManaCost, true);
    }

    public static boolean unstoppableSpeedAllDamageTypes() {
        Snapshot s = clientSnapshot;
        return s != null ? s.allDamageTypes : serverOrDefaultBool(WKServerConfig::shouldUnstoppableSpeedApplyToAllDamageTypes, false);
    }

    public static boolean unstoppableSpeedBypassArmor() {
        Snapshot s = clientSnapshot;
        return s != null ? s.bypassArmor : serverOrDefaultBool(WKServerConfig::shouldUnstoppableSpeedBypassArmor, false);
    }

    public static boolean fastDurabilityHurt() {
        Snapshot s = clientSnapshot;
        return s != null ? s.fastDurabilityHurt : serverOrDefaultBool(WKServerConfig::shouldFastDurabilityHurt, false);
    }

    public static boolean magicDisabledIncurable() {
        Snapshot s = clientSnapshot;
        return s != null ? s.magicDisabledIncurable : serverOrDefaultBool(WKServerConfig::shouldMagicDisabledBeIncurable, true);
    }

    /** 本地 SERVER 配置已加载时读配置，否则用默认值（避免 dev 环境抛异常） */
    private static boolean serverOrDefaultBool(Supplier<Boolean> configGetter, boolean fallback) {
        return WKServerConfig.SPEC.isLoaded() ? configGetter.get() : fallback;
    }

    private static int serverOrDefaultInt(Supplier<Integer> configGetter, int fallback) {
        return WKServerConfig.SPEC.isLoaded() ? configGetter.get() : fallback;
    }

    private static double serverOrDefaultDouble(Supplier<Double> configGetter, double fallback) {
        return WKServerConfig.SPEC.isLoaded() ? configGetter.get() : fallback;
    }

    /**
     * 配置快照（顺序即编解码顺序）。
     * <p>
     * 注意：本类两端都会加载，禁止引用任何客户端专属类。
     */
    public record Snapshot(
            boolean disableAbilities,
            boolean disablePassiveAbilities,
            boolean disableInteraction,
            int closeGuiChance,
            boolean disableLookRotation,
            boolean lockPosition,
            boolean shatterBuffForNonWingKirin,
            double shatterRatio,
            boolean ignoreManaCost,
            boolean allDamageTypes,
            boolean bypassArmor,
            boolean fastDurabilityHurt,
            boolean magicDisabledIncurable
    ) {
        /** 从服务端当前配置取值（仅在 SERVER 配置已加载的逻辑侧调用） */
        static Snapshot fromServer() {
            return new Snapshot(
                    WKServerConfig.shouldDingShenDisableAbilities(),
                    WKServerConfig.shouldDingShenDisablePassiveAbilities(),
                    WKServerConfig.shouldDingShenDisableInteraction(),
                    WKServerConfig.getDingShenCloseGuiChance(),
                    WKServerConfig.shouldDingShenDisableLookRotation(),
                    WKServerConfig.shouldDingShenLockPosition(),
                    WKServerConfig.shouldProvideShatterBuffToNonWingKirinDragons(),
                    WKServerConfig.getDingShenShatterRatio(),
                    WKServerConfig.shouldGreatZhengqiIgnoreManaCost(),
                    WKServerConfig.shouldUnstoppableSpeedApplyToAllDamageTypes(),
                    WKServerConfig.shouldUnstoppableSpeedBypassArmor(),
                    WKServerConfig.shouldFastDurabilityHurt(),
                    WKServerConfig.shouldMagicDisabledBeIncurable()
            );
        }

        void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(disableAbilities);
            buf.writeBoolean(disablePassiveAbilities);
            buf.writeBoolean(disableInteraction);
            buf.writeInt(closeGuiChance);
            buf.writeBoolean(disableLookRotation);
            buf.writeBoolean(lockPosition);
            buf.writeBoolean(shatterBuffForNonWingKirin);
            buf.writeDouble(shatterRatio);
            buf.writeBoolean(ignoreManaCost);
            buf.writeBoolean(allDamageTypes);
            buf.writeBoolean(bypassArmor);
            buf.writeBoolean(fastDurabilityHurt);
            buf.writeBoolean(magicDisabledIncurable);
        }

        static Snapshot decode(FriendlyByteBuf buf) {
            return new Snapshot(
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readDouble(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean(),
                    buf.readBoolean()
            );
        }

        void handle(Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> ConfigSyncHandler.clientSnapshot = this);
            context.setPacketHandled(true);
        }
    }
}
