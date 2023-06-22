package com.synthesyzer.teammanager.data.teamswap;

import com.mojang.authlib.GameProfile;

import java.util.*;

public class TeamSwapRequestManager {

    // map: key = senders, value = map of receivers with requests
    private static final Map<GameProfile, Map<GameProfile, TeamSwapRequest>> requests = new HashMap<>();

    public static void addRequest(GameProfile sender, GameProfile receiver) {
        TeamSwapRequest request = new TeamSwapRequest(sender, receiver, new Date());

        if (!requests.containsKey(request.sender())) {
            requests.put(request.sender(), new HashMap<>());
        }

        requests.get(request.sender()).put(request.receiver(), request);
    }

    public static Optional<TeamSwapRequest> getRequest(GameProfile sender, GameProfile receiver) {
        if (!requests.containsKey(sender)) {
            return Optional.empty();
        }

        return Optional.ofNullable(requests.get(sender).get(receiver));
    }

    public static List<GameProfile> getRequests(GameProfile receiver) {
        List<GameProfile> requesters = new ArrayList<>();

        for (Map.Entry<GameProfile, Map<GameProfile, TeamSwapRequest>> entry : requests.entrySet()) {
            if (entry.getValue().containsKey(receiver)) {
                requesters.add(entry.getKey());
            }
        }

        return requesters;
    }

    public static void deleteRequest(GameProfile sender, GameProfile receiver) {
        if (!requests.containsKey(sender)) {
            return;
        }

        requests.get(sender).remove(receiver);
    }

}
