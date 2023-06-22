package com.synthesyzer.teammanager.client.data;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.data.party.Party;

public class PartyData {

    private static Party party;

    public static void setParty(Party party) {
        PartyData.party = party;
    }

    public static Party getParty() {
        return party;
    }

    public static boolean isLeader(GameProfile profile) {
        return party.getLeader().equals(profile);
    }

    public static boolean isInParty(GameProfile player) {
        return party.getMembers().contains(player) || isLeader(player);
    }

}
