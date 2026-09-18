package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.Wing_kirin;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.jarcontents.JarContents;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.resource.JarContentsPackResources;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * 注册本模组内置的可选包（数据包 / 资源包）。
 * <p>
 * 凡「覆盖」了 mod_data 里已有文件的数据包，必须用 {@link #addOverridingDataPack}：
 * NeoForge 的便捷 API {@link AddPackFindersEvent#addPackFinders} 会附带 {@code KnownPack} 声明，
 * 客户端于是只用本地资源解析这些注册表元素，而客户端本地的加载顺序与 {@code mod_data} 相反，
 * 覆盖会静默失效（无任何日志）。不声明 {@code KnownPack} 即可让服务端始终下发覆盖后的数据。
 * <p>
 * 完整原理与排查过程见 Additional Abilities 的 {@code PackFinders}。
 * <p>
 * 只新增内容的数据包、以及全部客户端资源包用 {@link #addSimplePack} 即可（不参与该协商）。
 *
 * <h2>26.1.2 相对 1.21.1 的 API 差异</h2>
 * 参与 KnownPack 陷阱判定的 API <b>全部未变</b>，修复思路可直接照搬：
 * <ul>
 *     <li>{@code AddPackFindersEvent#addPackFinders} 依旧硬编码
 *         {@code new KnownPack("neoforge", "mod/" + packLocation, version)}，无法去掉；</li>
 *     <li>{@link PackLocationInfo}、{@link PackSelectionConfig}、{@code Pack#readMetaAndCreate}、
 *         {@code Pack.Position}、{@code PackSource#FEATURE} 签名一致；</li>
 *     <li>{@code getPackType()} / {@code addRepositorySource(...)} 一致。</li>
 * </ul>
 * 但「定位模组文件内资源」的 API 已变更，必须改写：
 * <ul>
 *     <li>{@code ResourceLocation} → {@link Identifier}；</li>
 *     <li>{@code BuiltInPackSource#fromName(...)} 已删除；</li>
 *     <li>{@code IModFile#findResource(String...)} 已删除，改为 {@code IModFile#getContents()}
 *         返回 {@link JarContents}（开发环境为目录包、正式环境为 jar 包，由实现类自行区分）。</li>
 * </ul>
 * 因此这里改用 NeoForge 自己的写法
 * {@code new JarContentsPackResources.JarContentsResourcesSupplier(contents, prefix)} ——
 * 与 {@code addPackFinders} 内部所用的 supplier 完全一致，仅去掉 KnownPack 声明。
 */
@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class PackFinders {
    private static final Logger LOGGER = LogUtils.getLogger();

    // 数据包：均覆盖了 data/… 下本模组已有的定义（dragon_ability / dragon_species / tags）
    private static final String LOW_UNLOCK_CONDITION = "data/wing_kirin/datapacks/low_unlock_condition";
    private static final String INNATE_LOW_UPGRADE_REQUIREMENT = "data/wing_kirin/datapacks/innate_low_upgrade_requirement";
    private static final String UNLOCK_ACQUIRED_ABILITIES = "data/wing_kirin/datapacks/unlock_acquired_abilities";
    private static final String ACQUIRED_LOW_UPGRADE_REQUIREMENT = "data/wing_kirin/datapacks/acquired_low_upgrade_requirement";

    // 资源包：纯客户端使用，不参与注册表同步
    private static final String WING_KIRIN_UI = "assets/wing_kirin/resourcepacks/wing_kirin_ui";
    private static final String ORIGINAL_FLY_ANIMATION = "assets/wing_kirin/resourcepacks/original_fly_animation";
    private static final String SIMPLE_DESCRIPTION = "assets/wing_kirin/resourcepacks/simple_description";

    @SubscribeEvent
    public static void addPackFinders(@NotNull AddPackFindersEvent event) {
        // ① 覆盖型数据包
        addOverridingDataPack(event, LOW_UNLOCK_CONDITION, "datapack.wing_kirin.low_unlock_condition");
        addOverridingDataPack(event, INNATE_LOW_UPGRADE_REQUIREMENT, "datapack.wing_kirin.innate_low_upgrade_requirement");
        addOverridingDataPack(event, UNLOCK_ACQUIRED_ABILITIES, "datapack.wing_kirin.unlock_acquired_abilities");
        addOverridingDataPack(event, ACQUIRED_LOW_UPGRADE_REQUIREMENT, "datapack.wing_kirin.acquired_low_upgrade_requirement");

        // ② 客户端资源包
        addSimplePack(event, PackType.CLIENT_RESOURCES, WING_KIRIN_UI, "resourcepack.wing_kirin.wing_kirin_ui", false);
        addSimplePack(event, PackType.CLIENT_RESOURCES, ORIGINAL_FLY_ANIMATION, "resourcepack.wing_kirin.original_fly_animation", false);
        addSimplePack(event, PackType.CLIENT_RESOURCES, SIMPLE_DESCRIPTION, Component.literal("精简描述"), false);
    }

    /**
     * 注册一个「会覆盖已有资源」的服务端数据包（默认可选、默认关闭）。
     *
     * @param path    包在模组资源中的路径，形如 {@code data/<modid>/datapacks/<name>}
     * @param nameKey 显示名语言键，如 {@code datapack.<modid>.<name>}
     */
    public static void addOverridingDataPack(
            @NotNull AddPackFindersEvent event, @NotNull String path, @NotNull String nameKey) {
        addOverridingDataPack(event, path, nameKey, false);
    }

    /**
     * 同上，可指定 {@code alwaysActive = true} 强制启用（玩家将无法关闭该数据包）。
     */
    public static void addOverridingDataPack(
            @NotNull AddPackFindersEvent event, @NotNull String path, @NotNull String nameKey, boolean alwaysActive) {
        if (event.getPackType() != PackType.SERVER_DATA) {
            return; // 覆盖型只可能是数据包
        }

        IModFileInfo modFileInfo = modFileInfo();
        if (modFileInfo == null) {
            return;
        }

        // 26.1.2：IModFile#findResource 已删除，改由 JarContents 访问模组文件内的资源
        JarContents contents = modFileInfo.getFile().getContents();

        // 关键：knownPackInfo 传 Optional.empty()，使本包无法参与「已知包」协商
        PackLocationInfo locationInfo = new PackLocationInfo(
                "mod/" + Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, path),
                Component.translatable(nameKey),
                PackSource.FEATURE,
                Optional.empty()
        );

        Pack pack = Pack.readMetaAndCreate(
                locationInfo,
                new JarContentsPackResources.JarContentsResourcesSupplier(contents, path),
                PackType.SERVER_DATA,
                new PackSelectionConfig(alwaysActive, Pack.Position.TOP, false)
        );

        if (pack == null) {
            // pack.mcmeta 读取失败时返回 null；塞进 repository 会在 discoverAvailable 里 NPE
            LOGGER.error("[PackFinders] 无法读取内置数据包 {} 的 pack.mcmeta，该数据包不会被注册（检查目录路径与 pack.mcmeta）",
                    path);
            return;
        }

        event.addRepositorySource(consumer -> consumer.accept(pack));
    }

    /**
     * 注册一个「只新增内容」的包（数据包或资源包），直接使用 NeoForge 的便捷 API。
     * <p>
     * 适用于不与任何模组已有文件同名的数据包，以及所有客户端资源包；
     * 这类包保留 {@code KnownPack} 传输优化不会造成两端不一致。
     *
     * @param type         {@link PackType#SERVER_DATA} 或 {@link PackType#CLIENT_RESOURCES}
     * @param path         包在模组资源中的路径（{@code data/…} 或 {@code assets/…}）
     * @param nameKey      显示名语言键
     * @param alwaysActive 是否强制启用（{@code false} = 玩家手动启用）
     */
    public static void addSimplePack(
            @NotNull AddPackFindersEvent event, @NotNull PackType type, @NotNull String path,
            @NotNull String nameKey, boolean alwaysActive) {
        addSimplePack(event, type, path, Component.translatable(nameKey), alwaysActive);
    }

    /** 同上，显示名直接给 {@link Component}（例如尚未配语言键的包）。 */
    public static void addSimplePack(
            @NotNull AddPackFindersEvent event, @NotNull PackType type, @NotNull String path,
            @NotNull Component name, boolean alwaysActive) {
        // 该方法内部会自行比对 packType，类型不符时是空操作
        event.addPackFinders(
                Identifier.fromNamespaceAndPath(Wing_kirin.MOD_ID, path),
                type,
                name,
                PackSource.FEATURE,
                alwaysActive,
                Pack.Position.TOP
        );
    }

    @Nullable
    private static IModFileInfo modFileInfo() {
        IModFileInfo modFileInfo = ModList.get().getModFileById(Wing_kirin.MOD_ID);

        if (modFileInfo == null) {
            LOGGER.error("[PackFinders] 找不到模组 {}，内置包不会被注册", Wing_kirin.MOD_ID);
        }

        return modFileInfo;
    }
}
