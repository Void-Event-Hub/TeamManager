package com.synthesyzer.teammanager.networking.packets.clienttoserver;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.party.PartyInviteManager;
import com.synthesyzer.teammanager.data.party.PartyManager;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceivePartyInvitePacket;
import com.synthesyzer.teammanager.util.Messenger;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public record SendPartyInvitePacket(UUID receiverId, String receiverName) {
    public static void register(OwoNetChannel channel) {
        channel.registerServerbound(SendPartyInvitePacket.class, ((message, access) -> {
            ServerPlayerEntity sender = access.player();
            GameProfile senderProfile = sender.getGameProfile();
            GameProfile receiverProfile = new GameProfile(message.receiverId(), message.receiverName());
            ServerWorld world = sender.getWorld();

            if (senderProfile.getId().equals(receiverProfile.getId())) {
                Messenger.sendError(sender, "You cannot invite yourself to a party");
                return;
            }

            boolean isInParty = PartyManager.isInParty(senderProfile);
            boolean isLeader = PartyManager.isLeader(senderProfile);

            if (isInParty && !isLeader) {
                Messenger.sendError(sender, "You are not the party leader");
                return;
            }

            if (PartyManager.isInParty(receiverProfile)) {
                Messenger.sendError(sender, "Player is already in a party");
                return;
            }

            if (PartyManager.partyIsFull(senderProfile)) {
                Messenger.sendError(sender, "Your party is full");
                return;
            }

            ServerPlayerEntity receiver = (ServerPlayerEntity) world.getPlayerByUuid(receiverProfile.getId());

            if (receiver == null) {
                Messenger.sendError(sender, "Player not found!");
                return;
            }

            if (PartyInviteManager.hasInvite(senderProfile, receiverProfile)) {
                Messenger.sendError(sender, "You have already sent an invite to this player");
                return;
            }

            PartyInviteManager.addInvite(senderProfile, receiverProfile);

            Messenger.sendSuccess(sender, "Invite sent to " + receiverProfile.getName());
            Messenger.sendMessage(receiver, "You have been invited to " + senderProfile.getName() + "'s party");

            channel.serverHandle(receiver).send(new ReceivePartyInvitePacket(
                    sender.getGameProfile().getId(),
                    sender.getGameProfile().getName()
            ));
        }));
    }
}
