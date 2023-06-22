package com.synthesyzer.teammanager.networking.packets.clienttoserver;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.teamswap.TeamSwapRequestManager;
import com.synthesyzer.teammanager.util.Messenger;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public record AcceptTeamSwapRequestPacket(UUID senderId, String senderName, boolean accepted) {
    public static void register(OwoNetChannel channel) {
        channel.registerServerbound(AcceptTeamSwapRequestPacket.class, ((message, access) -> {
            ServerPlayerEntity acceptor = access.player();
            ServerWorld world = acceptor.getWorld();
            GameProfile senderProfile = new GameProfile(message.senderId(), message.senderName());
            ServerPlayerEntity sender = (ServerPlayerEntity) world.getPlayerByUuid(senderProfile.getId());

            if (sender == null) {
                Messenger.sendError(acceptor, "Player not found!");
                return;
            }

            if (sender == acceptor) {
                Messenger.sendError(acceptor, "You can't swap with yourself!");
                return;
            }

            if (sender.getScoreboardTeam() == null) {
                Messenger.sendError(acceptor, "Player is not on a team!");
                return;
            }

            if (acceptor.getScoreboardTeam() == null) {
                Messenger.sendError(acceptor, "You are not on a team!");
                return;
            }

            if (acceptor.getScoreboardTeam().isEqual(sender.getScoreboardTeam())) {
                Messenger.sendError(acceptor, "You are already on the same team!");
                return;
            }

            var request = TeamSwapRequestManager.getRequest(sender.getGameProfile(), acceptor.getGameProfile());

            if (request.isEmpty()) {
                Messenger.sendError(acceptor, "You don't have a pending request!");
                return;
            }

            TeamSwapRequestManager.deleteRequest(sender.getGameProfile(), acceptor.getGameProfile());

            if (message.accepted()) {
                // confirmation message
                Messenger.sendSuccess(acceptor, "You have swapped teams with " + sender.getName().getString() + "!");

                // Notify sender
                Messenger.sendMessage(sender, acceptor.getName().getString() + " has accepted your request!");

                // Swap teams
                Team acceptorTeam = (Team) acceptor.getScoreboardTeam();
                world.getScoreboard().addPlayerToTeam(acceptor.getName().getString(), (Team) sender.getScoreboardTeam());
                world.getScoreboard().addPlayerToTeam(sender.getName().getString(), acceptorTeam);
            } else {
                // confirmation message
                Messenger.sendSuccess(acceptor, "You have declined the request from " + sender.getName().getString() + "!");

                // Notify sender
                Messenger.sendMessage(sender, acceptor.getName().getString() + " has declined your request!");
            }
        }));
    }
}
