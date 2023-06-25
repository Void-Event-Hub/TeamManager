package com.synthesyzer.teammanager.networking.packets.clienttoserver;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.party.Party;
import com.synthesyzer.teammanager.data.party.PartyInviteManager;
import com.synthesyzer.teammanager.data.party.PartyManager;
import com.synthesyzer.teammanager.networking.packets.servertoclient.UpdatePartyPacket;
import com.synthesyzer.teammanager.util.Messenger;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record AcceptPartyInvitePacket(UUID senderId, String senderName, boolean accepted) {

    public static void register(OwoNetChannel channel) {
        channel.registerServerbound(AcceptPartyInvitePacket.class, ((message, access) -> {
            ServerPlayerEntity acceptor = access.player();
            ServerWorld world = acceptor.getWorld();
            GameProfile senderProfile = new GameProfile(message.senderId(), message.senderName());
            ServerPlayerEntity sender = (ServerPlayerEntity) world.getPlayerByUuid(senderProfile.getId());

            if (sender == null) {
                Messenger.sendError(acceptor, "Player not found!");
                return;
            }

            if (PartyManager.isInParty(acceptor.getGameProfile())) {
                Messenger.sendError(acceptor, "You are already in a party!");
                return;
            }

            if (!PartyManager.isLeader(senderProfile) && PartyManager.isInParty(senderProfile)) {
                Messenger.sendError(acceptor, "Player is not a party leader!");
                return;
            }

            if (!PartyInviteManager.hasInvite(senderProfile, acceptor.getGameProfile())) {
                Messenger.sendError(acceptor, "You have no pending invites from this player!");
                return;
            }

            if (PartyManager.partyIsFull(senderProfile)) {
                Messenger.sendError(acceptor, "Party is full!");
                return;
            }

            PartyInviteManager.removeInvite(senderProfile, acceptor.getGameProfile());

            if (!message.accepted()) {
                Messenger.sendError(acceptor, "Invite declined!");
                Messenger.sendMessage(sender, acceptor.getGameProfile().getName() + " declined your invite!");
                return;
            }

            PartyManager.addMember(senderProfile, acceptor.getGameProfile());
            Party party = PartyManager.getParty(senderProfile);
            List<PlayerEntity> members = party.getMembers()
                    .stream()
                    .map(player -> world.getPlayerByUuid(player.getId()))
                    .filter(Objects::nonNull)
                    .toList();

            var partyUpdatePacket = new UpdatePartyPacket(
                    party.getLeader().getId(),
                    party.getMembers().stream().map(GameProfile::getId).toArray(UUID[]::new)
            );

            // inform members
            members.stream()
                    .filter(member -> !member.getUuid().equals(acceptor.getUuid()))
                    .forEach(member -> Messenger.sendMessage(member, acceptor.getGameProfile().getName() + " has joined the party!"));
            members.forEach(member -> channel.serverHandle(member).send(partyUpdatePacket));

            // inform leader
            channel.serverHandle(sender).send(partyUpdatePacket);
            Messenger.sendSuccess(sender, acceptor.getGameProfile().getName() + " has joined your party!");

            Messenger.sendSuccess(acceptor, "You have joined " + senderProfile.getName() + "'s party!");
        }));
    }

}
