package com.synthesyzer.teammanager.client.data;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;

public class PendingPartyInvites {

    private static final List<GameProfile> incomingInvites = new ArrayList<>();

    public static void addInvite(GameProfile sender) {
        incomingInvites.add(sender);
    }

    public static void delete(GameProfile sender) {
        incomingInvites.remove(sender);
    }

    public static List<GameProfile> getInvites() {
        return incomingInvites;
    }

    public static boolean hasInviteFrom(GameProfile profile) {
        return incomingInvites.contains(profile);
    }

    public static int count() {
        return incomingInvites.size();
    }

}
