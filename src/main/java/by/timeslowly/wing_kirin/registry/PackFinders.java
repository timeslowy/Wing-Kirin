package by.timeslowly.wing_kirin.registry;

import by.timeslowly.wing_kirin.WingKirin;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 1.20.1 Forge 的 {@link AddPackFindersEvent} 只有 {@code addRepositorySource(RepositorySource)} +
 * {@code getPackType()}，没有 1.20.2+ / NeoForge 的 {@code addPackFinders(...)} 便捷重载。
 * 这里按 NeoForge 该方法的内部实现手工展开：ModList 取 mod 文件 → findResource 拿目录 Path
 * → PathPackResources 包一层 → Pack.readMetaAndCreate → 作为 RepositorySource 注册。
 */
@Mod.EventBusSubscriber(modid = WingKirin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PackFinders {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void addPackFinders(@NotNull AddPackFindersEvent event) {
        if (event.getPackType() == PackType.SERVER_DATA) {
            addPack(event, PackType.SERVER_DATA, "unlock_acquired_abilities");
            addPack(event, PackType.SERVER_DATA, "acquired_low_upgrade_requirement");
        } else if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            addPack(event, PackType.CLIENT_RESOURCES, "wing_kirin_ui");
            addPack(event, PackType.CLIENT_RESOURCES, "original_fly_animation");
        }
    }

    private static void addPack(AddPackFindersEvent event, PackType type, String name) {
        String resourcePath = (type == PackType.SERVER_DATA ? "data/" : "assets/")
                + WingKirin.MODID + "/" + (type == PackType.SERVER_DATA ? "datapacks/" : "resourcepacks/") + name;
        String packId = WingKirin.MODID + ":" + (type == PackType.SERVER_DATA ? "datapacks/" : "resourcepacks/") + name;

        Path path = findPackPath(resourcePath);
        if (path == null) {
            LOGGER.warn("[wing_kirin] 内置包不存在，已跳过：{}（资源路径 {}）", packId, resourcePath);
            return;
        }

        Component title = Component.translatable(
                type == PackType.SERVER_DATA
                        ? "datapack.wing_kirin." + name
                        : "resourcepack.wing_kirin." + name);

        Pack pack = Pack.readMetaAndCreate(
                packId,
                title,
                false,
                id -> new PathPackResources(id, true, path),
                type,
                Pack.Position.TOP,
                PackSource.FEATURE
        );
        if (pack == null) {
            LOGGER.warn("[wing_kirin] 内置包 {} 缺少 pack.mcmeta，已跳过", packId);
            return;
        }
        event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
    }

    private static Path findPackPath(String resourcePath) {
        IModFileInfo fileInfo = ModList.get().getModFileById(WingKirin.MODID);
        if (fileInfo == null) return null;
        Path path = fileInfo.getFile().findResource(resourcePath.split("/"));
        return path != null && Files.exists(path) ? path : null;
    }
}
