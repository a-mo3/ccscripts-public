package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.settings.PlayerSettings;

public class Shayzien {
    public static final int SPAWN_EXPLODE_ANI = 7159;

    public static int getFavourPercent() {
        return PlayerSettings.getBitValue(4894) / 10;
    }
}
