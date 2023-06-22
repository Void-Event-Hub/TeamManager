package com.synthesyzer.teammanager.networking.packets.servertoclient;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.client.data.PartyData;
import com.synthesyzer.teammanager.data.party.Party;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * updates the party data of a player on the client-side
 */
public record UpdatePartyPacket(UUID leaderId, UUID... memberIds) {
    public static void register(OwoNetChannel channel) {
        channel.registerClientbound(UpdatePartyPacket.class, ((message, access) -> {
            World world = access.player().world;
            PlayerEntity leader = world.getPlayerByUuid(message.leaderId());
            Party party = new Party(leader.getGameProfile());
            List<GameProfile> members = Arrays.stream(message.memberIds()).map(world::getPlayerByUuid)
                    .filter(Objects::nonNull)
                    .map(PlayerEntity::getGameProfile)
                    .toList();

            party.setMembers(members);
            PartyData.setParty(party);
        }));
    }
}
