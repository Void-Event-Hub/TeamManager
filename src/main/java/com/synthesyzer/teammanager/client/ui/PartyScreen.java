package com.synthesyzer.teammanager.client.ui;

import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.client.data.PartyData;
import com.synthesyzer.teammanager.networking.TMNetwork;
import com.synthesyzer.teammanager.networking.packets.clienttoserver.LeavePartyPacket;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PartyScreen extends BaseUIModelScreen<FlowLayout> {

    protected PartyScreen() {
        super(
                FlowLayout.class,
                DataSource.asset(new Identifier(TeamManager.MOD_ID, "party_screen"))
//                DataSource.file("party_screen.xml")
        );
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        ScreenUtil.addMenuButtonFunctionality(rootComponent, this.client);

        if (PartyData.getParty() == null) {
            rootComponent.childById(VerticalFlowLayout.class, "main-page").child(
                    Components.label(Text.of("You are not in a party"))
                            .positioning(Positioning.relative(50, 50))
            );
            return;
        }

        var players = ScreenUtil.getPlayers(
                this.client.getNetworkHandler(),
                player -> PartyData.isInParty(player.getProfile())
        );

        rootComponent.childById(VerticalFlowLayout.class, "player-container").children(
                players.stream().map(this::getPlayerComponent).toList()
        );
    }

    private Component getPlayerComponent(PlayerListEntry player) {
        List<Component> actionButtons = new ArrayList<>();

        boolean playerIsLeader = PartyData.isLeader(this.client.player.getGameProfile());

        if (playerIsOwnPlayer(player) || playerIsLeader) {
            actionButtons.add(removeFromTeamButton(player));
        }

        return ScreenUtil.playerComponent(player, List.of(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .children(actionButtons)
                        .positioning(Positioning.relative(90, 0))
        ));
    }

    private Component removeFromTeamButton(PlayerListEntry player) {
        return Components.button(Text.of(playerIsOwnPlayer(player) ? " → " : " ✕ "), component -> {
                    TMNetwork.CHANNEL.clientHandle().send(
                            new LeavePartyPacket(player.getProfile().getId(), player.getProfile().getName())
                    );
                    if (playerIsOwnPlayer(player)) {
                        PartyData.setParty(null);
                    }
                    this.client.setScreen(null);
                })
                .tooltip(Text.of(playerIsOwnPlayer(player) ? "Leave Party" : "Kick from Party"));
    }

    private boolean playerIsOwnPlayer(PlayerListEntry player) {
        return player.getProfile().getId().equals(this.client.player.getGameProfile().getId());
    }
}
