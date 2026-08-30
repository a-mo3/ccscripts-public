package org.dreambot.behaviour.tutorial;


import org.dreambot.api.methods.settings.PlayerSettings;

public class MyVarps {
    public static int getTutVarp() {
        return PlayerSettings.getConfig(281);
    }
}
