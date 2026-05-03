package by.timeslowly.wing_kirin.common.command;

import by.timeslowly.wing_kirin.Wing_kirin;
import by.timeslowly.wing_kirin.registry.WKStats;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(modid = Wing_kirin.MOD_ID)
public class WKStatsCommand {

    /**
     * 自动补全建议器：列出模组注册的所有自定义统计项
     */
    private static final SuggestionProvider<CommandSourceStack> STAT_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    WKStats.getStatNames(),
                    builder
            );

    // 修改统计信息命令主体：/wk-stats add/set <targets> <stats_id> <int>
    @SubscribeEvent
    public static void register(@NotNull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("wk-stats")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("stat", ResourceLocationArgument.id())
                                                .suggests(STAT_SUGGESTIONS)
                                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                        .executes(ctx -> executeSet(ctx, EntityArgument.getPlayers(ctx, "targets"),
                                                                IntegerArgumentType.getInteger(ctx, "value")))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("stat", ResourceLocationArgument.id())
                                                .suggests(STAT_SUGGESTIONS)
                                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                        .executes(ctx -> executeAdd(ctx, EntityArgument.getPlayers(ctx, "targets"),
                                                                IntegerArgumentType.getInteger(ctx, "value")))
                                                )
                                        )
                                )
                        )
        );
    }

    /**
     * 根据命令参数中的 ResourceLocation 查找对应的自定义统计 Stat 对象
     * @return 对应的 Stat 对象，若无效则返回 null
     */
    private static @Nullable Stat<ResourceLocation> resolveStat(CommandContext<CommandSourceStack> ctx) {
        ResourceLocation statId = ResourceLocationArgument.getId(ctx, "stat");
        Optional<DeferredHolder<ResourceLocation, ? extends ResourceLocation>> holderOpt = WKStats.getHolder(statId);
        if (holderOpt.isEmpty()) {
            return null;
        }
        ResourceLocation statValue = holderOpt.get().get();
        return Stats.CUSTOM.get(statValue);
    }

    /**
     * /wk-stats set <targets> <stat> <value>
     * 将指定玩家的指定统计项设置为给定值
     */
    private static int executeSet(@NotNull CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets, int value) {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation statId = ResourceLocationArgument.getId(ctx, "stat");

        Stat<ResourceLocation> stat = resolveStat(ctx);
        if (stat == null) {
            source.sendFailure(Component.translatable("command.wing_kirin.wk_stats.unknown_stat", statId.toString()));
            return 0;
        }

        for (ServerPlayer player : targets) {
            ServerStatsCounter statsCounter = Objects.requireNonNull(player.getServer()).getPlayerList().getPlayerStats(player);
            statsCounter.setValue(player, stat, value);
        }

        if (targets.size() == 1) {
            source.sendSuccess(
                    () -> Component.translatable("command.wing_kirin.wk_stats.set_success.single",
                            targets.iterator().next().getDisplayName(),
                            Component.translatable("stat." + statId.getNamespace() + "." + statId.getPath()),
                            value),
                    true
            );
        } else {
            source.sendSuccess(
                    () -> Component.translatable("command.wing_kirin.wk_stats.set_success.multiple",
                            targets.size(),
                            Component.translatable("stat." + statId.getNamespace() + "." + statId.getPath()),
                            value),
                    true
            );
        }
        return targets.size();
    }

    /**
     * /wk-stats add <targets> <stat> <value>
     * 在指定玩家的指定统计项当前值基础上增加给定值
     */
    private static int executeAdd(@NotNull CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets, int value) {
        CommandSourceStack source = ctx.getSource();
        ResourceLocation statId = ResourceLocationArgument.getId(ctx, "stat");

        Stat<ResourceLocation> stat = resolveStat(ctx);
        if (stat == null) {
            source.sendFailure(Component.translatable("command.wing_kirin.wk_stats.unknown_stat", statId.toString()));
            return 0;
        }

        for (ServerPlayer player : targets) {
            ServerStatsCounter statsCounter = Objects.requireNonNull(player.getServer()).getPlayerList().getPlayerStats(player);
            int oldValue = statsCounter.getValue(stat);
            int newValue = oldValue + value;
            statsCounter.setValue(player, stat, newValue);
        }

        if (targets.size() == 1) {
            ServerPlayer target = targets.iterator().next();
            ServerStatsCounter statsCounter = Objects.requireNonNull(target.getServer()).getPlayerList().getPlayerStats(target);
            int newValue = statsCounter.getValue(stat);
            source.sendSuccess(
                    () -> Component.translatable("command.wing_kirin.wk_stats.add_success.single",
                            target.getDisplayName(),
                            Component.translatable("stat." + statId.getNamespace() + "." + statId.getPath()),
                            value, newValue),
                    true
            );
        } else {
            source.sendSuccess(
                    () -> Component.translatable("command.wing_kirin.wk_stats.add_success.multiple",
                            targets.size(),
                            Component.translatable("stat." + statId.getNamespace() + "." + statId.getPath()),
                            value),
                    true
            );
        }
        return targets.size();
    }
}
