package com.synthesyzer.teammanager.client;

import com.synthesyzer.teammanager.client.ui.PlayersScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class KeyPressEvent {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (TMKeybindings.openMenu.wasPressed()) {
                client.setScreen(new PlayersScreen());
            }
        });
    }

}
