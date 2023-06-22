package com.synthesyzer.teammanager.data.teamswap;

import com.mojang.authlib.GameProfile;
import com.synthesyzer.teammanager.TeamManager;

import java.util.Date;

public record TeamSwapRequest(GameProfile sender, GameProfile receiver, Date date) {

    public boolean isExpired() {
        int minutesTillRequestExpires = TeamManager.CONFIG.minutesTillRequestExpires();

        if (minutesTillRequestExpires == 0) {
            return false;
        }

        long timeSinceRequest = new Date().getTime() - date.getTime();
        long maxTime = 1000L * 60 * minutesTillRequestExpires;

        return timeSinceRequest > maxTime;
    }

}
