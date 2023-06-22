package com.synthesyzer.teammanager.client.data;

import com.mojang.authlib.GameProfile;

import java.util.Date;
import java.util.HashMap;

public class PendingTeamSwapRequests {

    private static final HashMap<GameProfile, Date> incomingRequests = new HashMap<>();

    public static void addRequest(GameProfile sender) {
        incomingRequests.put(sender, new Date());
    }

    public static void deleteRequest(GameProfile sender) {
        incomingRequests.remove(sender);
    }

    public static HashMap<GameProfile, Date> getRequests() {
        return incomingRequests;
    }

    public static boolean hasRequestFrom(GameProfile profile) {
        return incomingRequests.containsKey(profile);
    }

    public static int count() {
        return incomingRequests.size();
    }
}
