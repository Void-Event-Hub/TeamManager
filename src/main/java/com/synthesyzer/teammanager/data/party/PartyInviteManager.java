package com.synthesyzer.teammanager.data.party;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyInviteManager {

    // map of sender to list of receivers
    private static final HashMap<GameProfile, List<GameProfile>> partyInvites = new HashMap<>();

    public static void addInvite(GameProfile sender, GameProfile receiver) {
        if (!partyInvites.containsKey(receiver)) {
            partyInvites.put(receiver, new ArrayList<>(List.of(sender)));
        } else {
            partyInvites.get(receiver).add(sender);
        }
    }

    public static void removeInvite(GameProfile sender, GameProfile receiver) {
        if (!partyInvites.containsKey(receiver)) {
            return;
        }

        partyInvites.get(receiver).remove(sender);
    }

    public static boolean hasInvite(GameProfile sender, GameProfile receiver) {
        if (!partyInvites.containsKey(receiver)) {
            return false;
        }

        return partyInvites.get(receiver).contains(sender);
    }

    public static List<GameProfile> getInvites(GameProfile receiver) {
        List<GameProfile> invites = new ArrayList<>();

        for (Map.Entry<GameProfile, List<GameProfile>> entry : partyInvites.entrySet()) {
            if (entry.getValue().contains(receiver)) {
                invites.add(entry.getKey());
            }
        }

        return invites;
    }

}
