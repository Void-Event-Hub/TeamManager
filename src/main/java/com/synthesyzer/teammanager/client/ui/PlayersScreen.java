package com.synthesyzer.teammanager.client.ui;

import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.networking.TMNetwork;
import com.synthesyzer.teammanager.networking.packets.clienttoserver.SendPartyInvitePacket;
import com.synthesyzer.teammanager.networking.packets.clienttoserver.SendTeamSwapRequestPacket;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class PlayersScreen extends BaseUIModelScreen<FlowLayout> {

    public PlayersScreen() {
        super(
                FlowLayout.class,
                DataSource.asset(new Identifier(TeamManager.MOD_ID, "players_screen"))
//                DataSource.file("players_screen.xml")
        );
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        ScreenUtil.addMenuButtonFunctionality( rootComponent, this.client);

        var players = ScreenUtil.getPlayers(
                this.client.getNetworkHandler(),
                player -> !player.getProfile().getId().equals(this.client.player.getGameProfile().getId())
        );

        if (players.size() == 0) {
            rootComponent.childById(VerticalFlowLayout.class, "main-page").child(
                    Components.label(Text.of("No one else is online"))
                            .positioning(Positioning.relative(50, 50))
            );
            return;
        }

        rootComponent.childById(VerticalFlowLayout.class, "player-container").children(
                players.stream().map(this::getPlayerComponent).toList()
        );
    }

    private Component getPlayerComponent(PlayerListEntry player) {
        return ScreenUtil.playerComponent(player, List.of(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(switchTeamsButton(player))
                        .child(inviteButton(player))
                        .positioning(Positioning.relative(90, 0))
        ));
    }

    private Component switchTeamsButton(PlayerListEntry player) {
        return Components.button(Text.of(" ⇄ "), component -> {
                    TMNetwork.CHANNEL.clientHandle().send(
                            new SendTeamSwapRequestPacket(player.getProfile().getId(), player.getProfile().getName())
                    );
                    this.client.setScreen(null);
                })
                .textShadow(true)
                .tooltip(Text.of("Switch Teams"))
                .margins(Insets.right(2));
    }

    private Component inviteButton(PlayerListEntry player) {
        return Components.button(Text.of(" ✉ "), component -> {
                    TMNetwork.CHANNEL.clientHandle().send(
                            new SendPartyInvitePacket(player.getProfile().getId(), player.getProfile().getName())
                    );
                    this.client.setScreen(null);
                })
                .textShadow(true)
                .tooltip(Text.of("Invite to Party"))
                .margins(Insets.right(2));
    }
}
