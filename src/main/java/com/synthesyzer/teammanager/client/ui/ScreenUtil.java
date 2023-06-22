package com.synthesyzer.teammanager.client.ui;

import com.synthesyzer.teammanager.client.data.PendingPartyInvites;
import com.synthesyzer.teammanager.client.data.PendingTeamSwapRequests;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.Function;

public class ScreenUtil {

    public static List<PlayerListEntry> getPlayers(ClientPlayNetworkHandler clientPlayNetworkHandler) {
        return new ArrayList<>(clientPlayNetworkHandler.getPlayerList().stream().toList());
    }

    public static List<PlayerListEntry> getPlayers(
            ClientPlayNetworkHandler clientPlayNetworkHandler,
            Function<PlayerListEntry, Boolean> filter
    ) {
        return new ArrayList<>(clientPlayNetworkHandler.getPlayerList().stream()
                .filter(filter::apply)
                .toList()
        );
    }

    public static void addMenuButtonFunctionality(FlowLayout rootComponent, MinecraftClient client) {
        rootComponent.childById(ButtonComponent.class, "party-button")
                .onPress(component -> client.setScreen(new PartyScreen()));

        rootComponent.childById(ButtonComponent.class, "players-button")
                .onPress(component -> client.setScreen(new PlayersScreen()));

        rootComponent.childById(ButtonComponent.class, "party-invites-button")
                .onPress(component -> client.setScreen(new PartyInvites()))
                .setMessage(Text.literal("Party Invites (" + PendingPartyInvites.count() + ")"));

        rootComponent.childById(ButtonComponent.class, "team-switch-requests-button")
                .onPress(component -> client.setScreen(new SwitchTeamsRequestsScreen()))
                .setMessage(Text.literal("Switch Team Requests (" + PendingTeamSwapRequests.count() + ")"));
    }

    public static Component playerComponent(PlayerListEntry player, Collection<Component> components) {
        return Containers.horizontalFlow(Sizing.fill(100), Sizing.fixed(26))
                .child(playerSkinHead(player))
                .child(playerNameLabel(player))
                .child(playerTeamLabel(player))
                .children(components)
                .verticalAlignment(VerticalAlignment.CENTER)
                .surface(Surface.flat(0x77000000).and(Surface.outline(0xFF121212)))
                .padding(Insets.of(2))
                .margins(Insets.both(4, 2));
    }

    public static Component playerSkinHead(PlayerListEntry player) {
        return Components.texture(player.getSkinTexture(), 32, 32, 32, 32)
                .sizing(Sizing.fixed(16))
                .margins(Insets.horizontal(4));
    }

    public static Component playerNameLabel(PlayerListEntry player) {
        return Components.label(Text.of(player.getProfile().getName()))
                .maxWidth(100)
                .margins(Insets.left(10));
    }

    public static Component playerTeamLabel(PlayerListEntry player) {
        final Team scoreboardTeam = player.getScoreboardTeam();
        String scoreboardTeamName = "";
        Formatting scoreboardTeamColor = Formatting.WHITE;

        if (scoreboardTeam != null) {
            scoreboardTeamName = "[" + scoreboardTeam.getName() + "]";
            if (scoreboardTeam.getColor() != null) {
                scoreboardTeamColor = scoreboardTeam.getColor();
            }
        }
        return Components.label(Text.literal(scoreboardTeamName))
                .color(Color.ofFormatting(scoreboardTeamColor))
                .maxWidth(100)
                .margins(Insets.left(10));
    }

    private ScreenUtil() {
    }

}
