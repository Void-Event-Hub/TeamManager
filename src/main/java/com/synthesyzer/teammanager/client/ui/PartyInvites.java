package com.synthesyzer.teammanager.client.ui;

import com.synthesyzer.teammanager.TeamManager;
import com.synthesyzer.teammanager.client.data.PendingPartyInvites;
import com.synthesyzer.teammanager.networking.TMNetwork;
import com.synthesyzer.teammanager.networking.packets.clienttoserver.AcceptPartyInvitePacket;
import io.wispforest.owo.ui.base.BaseUIModelScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class PartyInvites extends BaseUIModelScreen<FlowLayout> {

    protected PartyInvites() {
        super(
                FlowLayout.class,
                DataSource.asset(new Identifier(TeamManager.MOD_ID, "party_invites_screen"))
//        DataSource.file("party_invites_screen.xml")
        );
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        ScreenUtil.addMenuButtonFunctionality(rootComponent, this.client);

        var players = ScreenUtil.getPlayers(
                this.client.getNetworkHandler(),
                player -> PendingPartyInvites.hasInviteFrom(player.getProfile())
        );

        if (players.size() == 0) {
            rootComponent.childById(VerticalFlowLayout.class, "main-page").child(
                    Components.label(Text.of("No party invites"))
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
                        .child(acceptInviteButton(player, true))
                        .child(acceptInviteButton(player, false))
                        .positioning(Positioning.relative(90, 0))
        ));
    }

    private Component acceptInviteButton(PlayerListEntry player, boolean accept) {
        return Components.button(Text.of(accept ? " ✔ " : " ✕ "), component -> {
                    TMNetwork.CHANNEL.clientHandle().send(
                            new AcceptPartyInvitePacket(player.getProfile().getId(), player.getProfile().getName(), accept)
                    );
                    PendingPartyInvites.delete(player.getProfile());
                    this.client.setScreen(null);
                })
                .textShadow(true)
                .tooltip(Text.of(accept ? "Accept Invite" : "Reject Invite"))
                .margins(Insets.right(2));
    }

}
