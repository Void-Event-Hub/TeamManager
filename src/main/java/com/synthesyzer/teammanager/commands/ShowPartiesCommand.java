package com.synthesyzer.teammanager.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class ShowPartiesCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("teammanager")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("showteams").executes(context -> {
                    List<List<GameProfile>> teams = TMCommands.getPlayersGroupedByParty(context.getSource().getWorld());

                    StringBuilder sb = new StringBuilder();

                    teams.forEach(team -> sb.append(formatTeam(team)));

                    context.getSource().sendFeedback(Text.of(sb.toString()), false);

                    return 1;
                })));
    }

    private static String formatTeam(List<GameProfile> team) {
        StringBuilder sb = new StringBuilder();

        sb.append("Team: [");
        team.forEach(player -> sb.append(player.getName()));
        sb.append("] \n");

        return sb.toString();
    }

}
