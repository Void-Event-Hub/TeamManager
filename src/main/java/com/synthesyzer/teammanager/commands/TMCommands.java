package com.synthesyzer.teammanager.commands;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.data.party.Party;
import com.synthesyzer.teammanager.data.party.PartyManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;

public class TMCommands {

    public static void register() {
        TeamManager.LOGGER.info("Registering commands");
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> AssignTeamsCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> AllowSwapCommand.register(dispatcher));
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> ShowPartiesCommand.register(dispatcher));
    }

    /**
     * returns a list of all players in the world, grouped by party.
     * players that are not inside a party are grouped by themselves.
     */
    public static List<List<GameProfile>> getPlayersGroupedByParty(ServerWorld world) {
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

}
