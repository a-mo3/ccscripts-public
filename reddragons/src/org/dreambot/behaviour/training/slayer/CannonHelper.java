package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.settings.PlayerSettings;

public class CannonHelper {
    public static int getAmmo() {
        return PlayerSettings.getConfig(4);
    }

    public static int getDownState() {
        return PlayerSettings.getConfig(2);
    }

}
