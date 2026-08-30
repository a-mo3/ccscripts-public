package org.dreambot.behaviour.tutorial.iron;

import org.dreambot.api.methods.settings.PlayerSettings;

public enum IronmanState {
    NORMAL(0, "None"),
    IRONMAN(1, "Ironman"),
    HCIM(3, "Hardcore Ironman"),
    ;

    final int varbitState;
    final String type;

    IronmanState(int state, String type) {
        varbitState = state;
        this.type = type;
    }

    public static IronmanState getCurrent() {
        for (IronmanState value : values()) {
            if (PlayerSettings.getBitValue(1777) == value.varbitState) return value;
        }
        return NORMAL;
    }

}
