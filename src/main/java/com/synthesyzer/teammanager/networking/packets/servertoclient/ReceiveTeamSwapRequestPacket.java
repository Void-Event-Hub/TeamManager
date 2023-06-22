package com.synthesyzer.teammanager.networking.packets.servertoclient;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.client.data.PendingTeamSwapRequests;
import io.wispforest.owo.network.OwoNetChannel;

import java.util.UUID;

public record ReceiveTeamSwapRequestPacket(UUID senderId, String senderName) {
    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(ReceiveTeamSwapRequestPacket.class, ((message, access) -> PendingTeamSwapRequests.addRequest(new GameProfile(message.senderId(), message.senderName()))));
    }

}
