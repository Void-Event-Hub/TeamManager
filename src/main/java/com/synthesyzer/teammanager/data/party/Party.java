package com.synthesyzer.teammanager.data.party;

import com.mojang.authlib.GameProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {

    private final UUID id;

    private GameProfile leader;
    private List<GameProfile> members;

    public Party(GameProfile leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        this.id = UUID.randomUUID();
    }

    public void setLeader(GameProfile leader) {
        this.leader = leader;
    }

    public void setMembers(List<GameProfile> members) {
        this.members = members;
    }

    public void addMember(GameProfile member) {
        members.add(member);
    }

    public void removeMember(GameProfile member) {
        members.remove(member);
    }

    public GameProfile getLeader() {
        return leader;
    }

    public List<GameProfile> getMembers() {
        return members;
    }

    public UUID getId() {
        return id;
    }

    public List<GameProfile> toList() {
        List<GameProfile> allPlayers = new ArrayList<>(members);
        allPlayers.add(leader);
        return allPlayers;
    }
}
