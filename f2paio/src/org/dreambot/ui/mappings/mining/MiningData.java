package org.dreambot.ui.mappings.mining;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
class MiningData {
    private int targetLevel;
    private boolean mineCopper;
    private boolean mineIron;
    private boolean mineCoal;
    private boolean mineGold;
    private boolean mineMithril;
    private boolean mineAdamant;
    // todo rune

    private boolean shouldBank;
}
