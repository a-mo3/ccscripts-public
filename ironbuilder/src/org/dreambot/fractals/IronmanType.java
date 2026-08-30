package org.dreambot.fractals;

import org.dreambot.api.methods.settings.PlayerSettings;

public enum IronmanType {
    NORMAL(0, "None"),
    IRONMAN(1, "Ironman"),
    HCIM(3, "Hardcore Ironman"),
    ;

    final int varbitState;
    public final String type;

    IronmanType(int state, String type) {
        varbitState = state;
        this.type = type;
    }

    public static IronmanType getCurrent() {
        for (IronmanType value : values()) {
            if (PlayerSettings.getBitValue(1777) == value.varbitState) return value;
        }
        return NORMAL;
    }

}
