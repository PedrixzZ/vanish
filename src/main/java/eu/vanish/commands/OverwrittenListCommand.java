package eu.vanish.commands;

import com.mojang.brigadier.CommandDispatcher;
import eu.vanish.Vanish;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.literal;

public final class OverwrittenListCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("list")
            .executes(commandContext -> executeNames(commandContext.getSource()))
            .then(literal("uuids").executes(commandContext -> executeUuids(commandContext.getSource()))));
    }

    private static int executeNames(ServerCommandSource source) {
        return execute(source, PlayerEntity::getDisplayName);
    }

    private static int executeUuids(ServerCommandSource source) {
        return execute(source, serverPlayerEntity -> Text.translatable("commands.list.nameAndId", serverPlayerEntity.getName(), serverPlayerEntity.getGameProfile().getId()));
    }

    private static int execute(ServerCommandSource source, Function<ServerPlayerEntity, Text> nameProvider) {
        PlayerManager playerManager = source.getServer().getPlayerManager();
        List<ServerPlayerEntity> list = new ArrayList<>(playerManager.getPlayerList());
        list.removeIf(Vanish.INSTANCE.vanishedPlayers::isVanished);
        Text text = Texts.join(list, nameProvider);
        source.sendFeedback(() -> Text.translatable("commands.list.players", list.size(), playerManager.getMaxPlayerCount(), text), false);
        return list.size();
    }
}
