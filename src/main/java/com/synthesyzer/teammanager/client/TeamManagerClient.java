package com.synthesyzer.teammanager.client;

import net.fabricmc.api.ClientModInitializer;

public class TeamManagerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        TMKeybindings.register();
        KeyPressEvent.register();
    }
}
