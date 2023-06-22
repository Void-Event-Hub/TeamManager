package com.synthesyzer.teammanager.events;

import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.config.MyConfigModel;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Stack;

public class PlayerTeamAssignment {

    private static final Stack<Team> teams = new Stack<>();

    public static void assignPlayerToTeam(ServerPlayerEntity player, ServerWorld world) {
        Team team = getNextTeam(world);
        world.getScoreboard().addPlayerToTeam(player.getName().getString(), team);
    }

    private static Team getNextTeam(ServerWorld world) {
        var assignStrategy = TeamManager.CONFIG.teamAssignStrategy();

        if (assignStrategy == MyConfigModel.TeamAssignStrategy.ROUND_ROBIN) {
            return getTeamRoundRobin(world);
        }

        return getRandomTeam(world);
    }

    private static Team getTeamRoundRobin(ServerWorld world) {
        if (teams.isEmpty()) {
            world.getScoreboard().getTeams().forEach(teams::push);
        }

        return teams.pop();
    }

    private static Team getRandomTeam(ServerWorld world) {
        List<Team> teams = world.getScoreboard().getTeams().stream().toList();
        int randomIndex = (int) (Math.random() * teams.size());
        return teams.get(randomIndex);
    }

    private PlayerTeamAssignment() {}
}
