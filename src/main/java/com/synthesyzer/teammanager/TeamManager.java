package com.synthesyzer.teammanager;

import com.synthesyzer.teammanager.commands.TMCommands;
import com.synthesyzer.teammanager.config.MyConfig;
import com.synthesyzer.teammanager.events.TMEvents;
import com.synthesyzer.teammanager.networking.TMNetwork;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamManager implements ModInitializer {
	public static final String MOD_ID = "synthsteammanager";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final MyConfig CONFIG = MyConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Synth's Team Manager");
		TMEvents.register();
		TMNetwork.register();
		TMCommands.register();
	}
}
