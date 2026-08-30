package org.dreambot.settings.timing;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ReactionSettings {
    private final int quickLow = 30;
    private final int quickHigh = 200;

    private final int normalLow = 450;
    private final int normalHigh = 850;

    private final int longLow = 400;
    private final int longHigh = 900;
}
