package com.synthesyzer.teammanager.networking.packets.servertoclient;

import com.synthesyzer.teammanager.client.data.PartyData;
import io.wispforest.owo.network.OwoNetChannel;

public record EmptyPartyPacket() {

    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(EmptyPartyPacket.class, ((message, access) -> PartyData.setParty(null)));
    }

}
