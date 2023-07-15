package com.synthesyzer.teammanager.events;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.party.Party;
import com.synthesyzer.teammanager.data.party.PartyInviteManager;
import com.synthesyzer.teammanager.data.party.PartyManager;
import com.synthesyzer.teammanager.data.teamswap.TeamSwapRequestManager;
import com.synthesyzer.teammanager.networking.TMNetwork;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceivePartyInvitePacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceiveTeamSwapRequestPacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.UpdatePartyPacket;
import com.synthesyzer.teammanager.util.Messenger;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class PlayerJoinServerEvent {

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, serverWorld) -> {
            if (entity instanceof ServerPlayerEntity player) {
                handleLoadPlayerEntity(player, serverWorld);
                Messenger.sendMessage(player, "§7Invite your friends to a party by pressing §eX§7. Players in your party will be assigned to the same team!");
            }
        });
    }

    private static void handleLoadPlayerEntity(ServerPlayerEntity player, ServerWorld world) {
        informClientOfPendingTeamSwapRequests(player);
        informClientOfPendingPartyInvites(player);
        informClientOfCurrentParty(player);
    }

    private static void informClientOfPendingTeamSwapRequests(ServerPlayerEntity player) {
        TeamSwapRequestManager.getRequests(player.getGameProfile()).forEach(senderProfile -> {
            TMNetwork.CHANNEL.serverHandle(player).send(new ReceiveTeamSwapRequestPacket(senderProfile.getId(), senderProfile.getName()));
        });
    }

    private static void informClientOfPendingPartyInvites(ServerPlayerEntity player) {
        PartyInviteManager.getInvites(player.getGameProfile()).forEach(inviterProfile -> {
            TMNetwork.CHANNEL.serverHandle(player).send(new ReceivePartyInvitePacket(inviterProfile.getId(), inviterProfile.getName()));
        });
    }

    private static void informClientOfCurrentParty(ServerPlayerEntity player) {
        Party party = PartyManager.getPartyByMember(player.getGameProfile());

        if (party == null) {
            return;
        }

        TMNetwork.CHANNEL.serverHandle(player).send(new UpdatePartyPacket(
                party.getLeader().getId(),
                party.getMembers().stream().map(GameProfile::getId).toArray(UUID[]::new))
        );
    }
}
