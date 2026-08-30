package org.dreambot.ui.mappings.smithing;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmithingData {
    private int targetLvl;

    private boolean smithBronze;
    private boolean smithIron;
    private boolean smithSilver;
    private boolean smithSteel;
    private boolean smithGold;
    private boolean smithMithril;
    private boolean smithAdamant;
    private boolean smithRunite;
}
