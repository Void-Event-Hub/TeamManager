package com.synthesyzer.teammanager.events;

import com.synthesyzer.teammanager.TeamManager;

public class TMEvents {

    public static void register() {
        TeamManager.LOGGER.info("Registering Events");
        PlayerJoinServerEvent.register();
    }

}
