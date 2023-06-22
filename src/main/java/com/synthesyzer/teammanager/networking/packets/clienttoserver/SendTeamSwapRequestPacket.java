package com.synthesyzer.teammanager.networking.packets.clienttoserver;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.teamswap.TeamSwapRequestManager;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceiveTeamSwapRequestPacket;
import com.synthesyzer.teammanager.util.Messenger;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

/**
 * Handles when a player sends a request to swap teams
 */
public record SendTeamSwapRequestPacket(UUID receiverId, String receiverName) {

    public static void register(OwoNetChannel channel) {
        channel.registerServerbound(SendTeamSwapRequestPacket.class, ((message, access) -> {
            ServerPlayerEntity sender = access.player();
            ServerWorld world = sender.getWorld();
            GameProfile receiverProfile = new GameProfile(message.receiverId(), message.receiverName());
            ServerPlayerEntity receiver = (ServerPlayerEntity) world.getPlayerByUuid(receiverProfile.getId());

            if (receiver == null) {
                Messenger.sendError(sender, "Player not found!");
                return;
            }

            if (receiver == sender) {
                Messenger.sendError(sender, "You can't swap with yourself!");
                return;
            }

            if (sender.getScoreboardTeam() == null) {
                Messenger.sendError(sender, "You are not on a team!");
                return;
            }

            if (receiver.getScoreboardTeam() == null) {
                Messenger.sendError(sender, "Player is not on a team!");
                return;
            }

            if (receiver.getScoreboardTeam().isEqual(sender.getScoreboardTeam())) {
                Messenger.sendError(sender, "You are already on the same team!");
                return;
            }

            if (TeamSwapRequestManager.getRequest(sender.getGameProfile(), receiver.getGameProfile()).isPresent()) {
                Messenger.sendError(sender, "You already have a pending request!");
                return;
            }

            TeamSwapRequestManager.addRequest(sender.getGameProfile(), receiver.getGameProfile());

            // confirmation message
            Messenger.sendSuccess(sender, "Request sent to" + receiver.getName().getString() + "!");

            // Notify receiver
            Messenger.sendMessage(receiver, sender.getName().getString() + ", from Team " +
                    receiver.getScoreboardTeam().getName() + " wants to swap teams with you!");

            channel.serverHandle(receiver).send(new ReceiveTeamSwapRequestPacket(
                    sender.getGameProfile().getId(),
                    sender.getGameProfile().getName()
            ));
        }));
    }

}
