package com.synthesyzer.teammanager.networking;

import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.networking.packets.clienttoserver.*;
import com.synthesyzer.teammanager.networking.packets.servertoclient.EmptyPartyPacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceivePartyInvitePacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.ReceiveTeamSwapRequestPacket;
import com.synthesyzer.teammanager.networking.packets.servertoclient.UpdatePartyPacket;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.util.Identifier;

public class TMNetwork {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(TeamManager.MOD_ID, "main"));

    public static void register() {
        // Party
        SendPartyInvitePacket.register(CHANNEL);
        AcceptPartyInvitePacket.register(CHANNEL);
        LeavePartyPacket.register(CHANNEL);
        ReceivePartyInvitePacket.register(CHANNEL);
        UpdatePartyPacket.register(CHANNEL);
        EmptyPartyPacket.register(CHANNEL);

        // Team Swap
        SendTeamSwapRequestPacket.register(CHANNEL);
        AcceptTeamSwapRequestPacket.register(CHANNEL);
        ReceiveTeamSwapRequestPacket.register(CHANNEL);
    }


}
