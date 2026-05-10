package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class WKStats {
    public static final DeferredRegister<ResourceLocation> CUSTOM_STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, Wing_kirin.MOD_ID);

    // 自定义统计信息项
    // 定身数项
    public static final DeferredHolder<ResourceLocation, ResourceLocation> CastedStasisHexCTimes =
            CUSTOM_STATS.register("casted_stasis_hex_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "casted_stasis_hex_times"));

    // 触发回光返照次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> TriggerLastStandTimes =
            CUSTOM_STATS.register("triggered_last_stand_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "triggered_last_stand_times"));

    // 施法金风玉露总时长
    public static final DeferredHolder<ResourceLocation, ResourceLocation> CastedEmpyreanWineSeconds =
            CUSTOM_STATS.register("casted_empyrean_wine_seconds", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "casted_empyrean_wine_seconds"));

    // 施法 云销雨霁 次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> CastedWeatherClearTimes =
            CUSTOM_STATS.register("casted_weather_clear_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "casted_weather_clear_times"));

    // 射出 穿云箭 次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> ShootSignalArrowCount =
            CUSTOM_STATS.register("shoot_signal_arrow_count", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "shoot_signal_arrow_count"));

    // 不坏金身 反震次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> IndestructibleBodyCountershockTimes =
            CUSTOM_STATS.register("indestructible_body_countershock_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "indestructible_body_countershock_times"));

    // 触发 天降正义 次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> TriggeredHeavenlyJusticeTimes =
            CUSTOM_STATS.register("triggered_heavenly_justice_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "triggered_heavenly_justice_times"));

    // 破隐一击次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> BrokenInstantInvisibilityTimes =
            CUSTOM_STATS.register("broken_instant_invisibility_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "broken_instant_invisibility_times"));

    // 触发 返老还童 次数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> TriggeredReverseAgingTimes =
            CUSTOM_STATS.register("triggered_reverse_aging_times", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "triggered_reverse_aging_times"));

    // 仁者无敌 治愈友方总数
    public static final DeferredHolder<ResourceLocation, ResourceLocation> CuredAliesCount =
            CUSTOM_STATS.register("cured_alies_count", () ->
                    ResourceLocation.fromNamespaceAndPath(Wing_kirin.MOD_ID, "cured_alies_count"));

    /**
     * 秒数格式化器：将整数值作为秒数显示，自动转换为合适的单位（s/m/h/d）
     */
    private static final StatFormatter SECONDS_FORMATTER = (value) -> {
        if ((double) value > 86400.0) return String.format(Locale.ROOT, "%.2f", (double) value / 86400.0) + " d";
        if ((double) value > 3600.0) return String.format(Locale.ROOT, "%.2f", (double) value / 3600.0) + " h";
        if ((double) value > 60.0) return String.format(Locale.ROOT, "%.2f", (double) value / 60.0) + " m";
        return value + " s";
    };

    /**
     * 统计项 ID → DeferredHolder 的 O(1) 查找缓存，在 FMLCommonSetupEvent 阶段构建
     */
    private static Map<ResourceLocation, DeferredHolder<ResourceLocation, ? extends ResourceLocation>> holderCache;

    /**
     * 获取所有已注册统计项的注册名（ResourceLocation key）流
     */
    public static Stream<ResourceLocation> getStatEntries() {
        return CUSTOM_STATS.getEntries().stream()
                .map(DeferredHolder::getId);
    }

    /**
     * 获取所有已注册统计项的名称字符串流（用于命令自动补全）
     */
    public static Stream<String> getStatNames() {
        return getStatEntries().map(ResourceLocation::toString);
    }

    /**
     * 根据注册名查找对应的 DeferredHolder（O(1) HashMap 查找）
     * @param id 注册名 ResourceLocation
     * @return 对应的 DeferredHolder，若不存在则返回 Optional.empty()
     */
    public static @NotNull Optional<DeferredHolder<ResourceLocation, ? extends ResourceLocation>> getHolder(ResourceLocation id) {
        if (holderCache == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(holderCache.get(id));
    }

    /**
     * 在 FMLCommonSetupEvent 阶段注入自定义 StatFormatter，
     * 通过 Stats.CUSTOM.get(value, formatter) 预先注册带自定义格式化器的 Stat 对象。
     */
    private static void setupCustomFormatters() {
        // 构建 O(1) 查询缓存
        holderCache = new HashMap<>();
        for (var holder : CUSTOM_STATS.getEntries()) {
            holderCache.put(holder.getId(), holder);
        }

        // 施法金风玉露总时长 → 秒数格式化器
        Stats.CUSTOM.get(CastedEmpyreanWineSeconds.get(), SECONDS_FORMATTER);
    }

    /**
     * FMLCommonSetupEvent 回调：在模组通用设置阶段注入自定义格式化器
     */
    public static void onCommonSetup(@NotNull FMLCommonSetupEvent event) {
        event.enqueueWork(WKStats::setupCustomFormatters);
    }

    public static void register(IEventBus eventBus) {
        CUSTOM_STATS.register(eventBus);
    }
}
