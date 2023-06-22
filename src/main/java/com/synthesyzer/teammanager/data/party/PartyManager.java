package com.synthesyzer.teammanager.data.party;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.TeamManager;

import java.util.Collection;
import java.util.HashMap;

public class PartyManager {

    private static final HashMap<GameProfile, Party> parties = new HashMap<>();

    public static void addMember(GameProfile leader, GameProfile member) {
        if (!parties.containsKey(leader)) {
            createParty(leader);
        }

        parties.get(leader).addMember(member);
    }

    public static boolean isLeader(GameProfile profile) {
        return parties.containsKey(profile);
    }

    public static boolean isInParty(GameProfile profile) {
        return isLeader(profile) || parties.values()
                .stream()
                .anyMatch(party -> party.getMembers().contains(profile));
    }

    public static Party getParty(GameProfile leader) {
        return parties.get(leader);
    }

    public static Party getPartyByMember(GameProfile member) {
        return parties.values()
                .stream()
                .filter(party -> party.getMembers().contains(member) || party.getLeader().equals(member))
                .findFirst()
                .orElse(null);
    }

    public static void removeMember(GameProfile member) {
        GameProfile leader = getPartyByMember(member).getLeader();
        parties.get(leader).removeMember(member);
    }

    public static void removeParty(GameProfile leader) {
        parties.remove(leader);
    }

    public static Collection<Party> getParties() {
        return parties.values();
    }

    private static void createParty(GameProfile leader) {
        parties.put(leader, new Party(leader));
    }

    public static boolean partyIsFull(GameProfile leader) {
        if (parties.get(leader) == null) {
            return false;
        }
        return parties.get(leader).getMembers().size() >= (TeamManager.CONFIG.maxPartySize() - 1);
    }

}
