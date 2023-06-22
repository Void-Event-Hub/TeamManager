package com.synthesyzer.teammanager.config;

import com.synthesyzer.teammanager.TeamManager;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = TeamManager.MOD_ID)
@Config(name = "void-civilizations", wrapperName = "MyConfig")
public class MyConfigModel {

    public TeamAssignStrategy teamAssignStrategy = TeamAssignStrategy.ROUND_ROBIN;
    public int minutesTillRequestExpires = 5;
    public int maxPartySize = 4;

    public enum TeamAssignStrategy {
        ROUND_ROBIN, RANDOM
    }
}