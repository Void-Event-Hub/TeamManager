package com.synthesyzer.teammanager.networking.packets.servertoclient;

import java.util.UUID;



public record PlayerJoinsPartyPacket(UUID playerId, String playerName) {
}
