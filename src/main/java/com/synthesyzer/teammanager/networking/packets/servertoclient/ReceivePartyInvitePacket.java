package com.synthesyzer.teammanager.networking.packets.servertoclient;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.client.data.PendingPartyInvites;
import io.wispforest.owo.network.OwoNetChannel;

import java.util.UUID;

public record ReceivePartyInvitePacket(UUID senderId, String senderName) {
    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(ReceivePartyInvitePacket.class, ((message, access) -> PendingPartyInvites.addInvite(new GameProfile(message.senderId(), message.senderName()))));
    }
}
