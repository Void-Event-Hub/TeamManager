package com.synthesyzer.teammanager.networking.packets.clienttoserver;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.party.Party;
import com.synthesyzer.teammanager.data.party.PartyManager;
import com.synthesyzer.teammanager.networking.packets.servertoclient.EmptyPartyPacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.UpdatePartyPacket;
import com.synthesyzer.teammanager.util.Messenger;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record LeavePartyPacket(UUID playerId, String playerName) {

    public static void register(OwoNetChannel channel) {
        channel.registerServerbound(LeavePartyPacket.class, ((message, access) -> {
            ServerPlayerEntity remover = access.player();
            GameProfile removedProfile = new GameProfile(message.playerId(), message.playerName());
            ServerWorld world = remover.getWorld();

            if (remover.getUuid().equals(removedProfile.getId())) {
                handlePlayerLeftByThemselves(remover, removedProfile, channel, world);
            } else {
                if (!PartyManager.isLeader(remover.getGameProfile())) {
                    Messenger.sendError(remover, "You are not the leader of your party!");
                    return;
                }
                handlePlayerWasRemoved(remover, removedProfile, channel, world);
            }
        }));
    }

    private static void handlePlayerLeftByThemselves(ServerPlayerEntity remover, GameProfile removedProfile, OwoNetChannel channel, ServerWorld world) {
        Party party = PartyManager.getPartyByMember(removedProfile);

        if (party == null) {
            Messenger.sendError(remover, "You are not in a party!");
            return;
        }

        if (PartyManager.isLeader(removedProfile)) {
            PartyManager.removeParty(removedProfile);
            getMembers(party, world).forEach(member -> {
                Messenger.sendMessage(member, "Your party has been disbanded!");
                channel.serverHandle(member).send(new EmptyPartyPacket());
            });

            Messenger.sendSuccess(remover, "You have disbanded your party!");
        } else {
            PartyManager.removeMember(removedProfile);
            var partyUpdatePacket = new UpdatePartyPacket(
                    party.getLeader().getId(),
                    party.getMembers().stream().map(GameProfile::getId).toArray(UUID[]::new)
            );
            getMembers(party, world).forEach(member -> {
                Messenger.sendMessage(member, removedProfile.getName() + " has left the party!");
                channel.serverHandle(member).send(partyUpdatePacket);
            });

            ServerPlayerEntity leader = (ServerPlayerEntity) world.getPlayerByUuid(party.getLeader().getId());
            Messenger.sendSuccess(leader, removedProfile.getName() + " has left your party!");
            channel.serverHandle(leader).send(partyUpdatePacket);

            Messenger.sendSuccess(remover, "You have left the party!");
        }
    }

    private static void handlePlayerWasRemoved(ServerPlayerEntity remover, GameProfile removedProfile, OwoNetChannel channel, ServerWorld world) {
        Party party = PartyManager.getPartyByMember(removedProfile);

        if (party == null) {
            Messenger.sendError(remover, removedProfile.getName() + " is not in a party!");
            return;
        }

        PartyManager.removeMember(removedProfile);
        var partyUpdatePacket = new UpdatePartyPacket(
                party.getLeader().getId(),
                party.getMembers().stream().map(GameProfile::getId).toArray(UUID[]::new)
        );
        getMembers(party, world).forEach(member -> {
            Messenger.sendMessage(member, removedProfile.getName() + " has been removed from the party!");
            channel.serverHandle(member).send(partyUpdatePacket);
        });

        Messenger.sendMessage(remover, "You have removed " + removedProfile.getName() + " from the party!");
        channel.serverHandle(remover).send(partyUpdatePacket);
        ServerPlayerEntity removed = (ServerPlayerEntity) world.getPlayerByUuid(removedProfile.getId());

        if (removed == null) {
            return;
        }

        Messenger.sendMessage(removed, "You have been removed from the party!");
        channel.serverHandle(removed).send(new EmptyPartyPacket());
    }

    private static List<ServerPlayerEntity> getMembers(Party party, ServerWorld world) {
        return party.getMembers()
                .stream()
                .map(profile -> (ServerPlayerEntity) world.getPlayerByUuid(profile.getId()))
                .filter(Objects::nonNull)
                .toList();
    }

}
