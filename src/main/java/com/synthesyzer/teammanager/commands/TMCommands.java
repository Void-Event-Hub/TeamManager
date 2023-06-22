package com.synthesyzer.teammanager.commands;

import com.synthesyzer.teammanager.TeamManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class TMCommands {

    public static void register() {
        TeamManager.LOGGER.info("Registering commands");
        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> AssignTeamsCommand.register(dispatcher));
    }

}
