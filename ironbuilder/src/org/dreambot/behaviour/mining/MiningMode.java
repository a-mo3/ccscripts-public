package org.dreambot.behaviour.mining;

public enum MiningMode {
    CLAY(1),
    TIN(1),
    COPPER(1),
    BRONZE(1),

    IRON(15),
    SILVER(20),
    COAL(30),
    GOLD(40),

    MITHRIL(55),
    ADAMANTITE(70),
    RUNITE(85),
    ;

    final int minLvl;

    MiningMode(int minLvl) {
        this.minLvl = minLvl;
    }
}
