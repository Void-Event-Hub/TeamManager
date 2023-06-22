package com.synthesyzer.teammanager.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.synthesyzer.teammanager.data.party.Party;
import com.synthesyzer.teammanager.data.party.PartyManager;
import com.synthesyzer.teammanager.util.Messenger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AssignTeamsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("teammanager")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("assign").executes(AssignTeamsCommand::assignTeams)));
    }

    public static int assignTeams(CommandContext<ServerCommandSource> context) {
        ServerWorld world = context.getSource().getWorld();
        ServerPlayerEntity executor = context.getSource().getPlayer();

        if (world.getScoreboard().getTeams().isEmpty()) {
            Messenger.sendMessage(executor, "No scoreboard teams found.");
            return 1;
        }

        emptyScoreboardTeams(world);
        if (executor != null) {
            Messenger.sendMessage(executor, "Cleared scoreboard teams.");
        }

        List<List<GameProfile>> players = getPlayersGroupedByParty(world);
        Collections.shuffle(players);
        List<List<GameProfile>> teams = assignTeams(players, world.getScoreboard().getTeams().size());

        List<Team> scoreboardTeams = world.getScoreboard().getTeams().stream().toList();
        for (int i = 0; i < teams.size(); i++) {
            List<GameProfile> team = teams.get(i);
            Team scoreboardTeam = scoreboardTeams.get(i);

            for (GameProfile player : team) {
                PlayerEntity playerEntity = world.getPlayerByUuid(player.getId());
                if (playerEntity != null) {
                    world.getScoreboard().addPlayerToTeam(playerEntity.getEntityName(), scoreboardTeam);
                }
            }
        }

        int totalPlayers = players.stream().mapToInt(List::size).sum();
        if (executor != null) {
            Messenger.sendMessage(executor, "Assigned " + totalPlayers + " players to " +
                    scoreboardTeams.size() + " teams.");
        }

        return 1;
    }

    /**
     * Groups players into teams.
     */
    private static List<List<GameProfile>> assignTeams(List<List<GameProfile>> players, int numberOfTeams) {
        int totalParties = players.size();

        players.sort((party1, party2) -> party2.size() - party1.size());

        List<GameProfile>[] teams = new List[numberOfTeams];
        Arrays.fill(teams, new ArrayList<>());

        for (int i = 0; i < numberOfTeams; i++) {
            teams[i] = new ArrayList<>();
        }

        for (int i = 0; i < totalParties; i++) {
            List<GameProfile> party = players.get(i);
            int teamIndex = i % numberOfTeams;
            teams[teamIndex].addAll(party);
        }

        return Arrays.stream(teams).toList();
    }

    /**
     * returns a list of all players in the world, grouped by party.
     * players that are not inside a party are grouped by themselves.
     */
    private static List<List<GameProfile>> getPlayersGroupedByParty(ServerWorld world) {
        List<List<GameProfile>> players = new ArrayList<>(
                PartyManager.getParties()
                        .stream()
                        .map(Party::toList)
                        .toList()
        );

        List<GameProfile> partiedPlayers = players.stream().flatMap(List::stream).toList();

        List<GameProfile> unPartiedPlayers = world.getPlayers().stream()
                .map(PlayerEntity::getGameProfile)
                .filter(player -> !partiedPlayers.contains(player))
                .toList();

        players.addAll(unPartiedPlayers.stream().map(List::of).toList());

        return players;
    }

    private static void emptyScoreboardTeams(ServerWorld world) {
        List<Team> teams = world.getScoreboard().getTeams().stream().toList();
        for (Team team : teams) {
            List<String> players = team.getPlayerList().stream().toList();
            for (String player : players) {
                world.getScoreboard().removePlayerFromTeam(player, team);
            }
        }
    }


}
