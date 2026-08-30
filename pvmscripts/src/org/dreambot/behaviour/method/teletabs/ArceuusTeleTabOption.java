package org.dreambot.behaviour.method.teletabs;

import org.dreambot.fractals.data.ItemID;

public enum ArceuusTeleTabOption {
    ARCEUUS_LIBRARY("Arceuus Library", 6, ItemID.LAW_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_DRAYNOR_MANOR("Draynor Manor", 17, ItemID.LAW_RUNE, ItemID.WATER_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_BATTLEFRONT("Battlefront", 23, ItemID.LAW_RUNE, ItemID.FIRE_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_MIND_ALTAR("Mind Altar", 28, ItemID.LAW_RUNE, ItemID.MIND_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_SALVE_GRAVEYARD("Salve Graveyard", 40, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_FENKENSTRAIN("Fenkenstrain's castle", 48, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_WEST_ARD("West Ardougne", 61, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_HARMONY_ISLAND("Harmony Island", 65, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.NATURE_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_CEMETERY("Wilderness Cemetary", 71, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.BLOOD_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_BARROWS("Barrows", 83, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.BLOOD_RUNE, ItemID.EARTH_RUNE),
    ARCEUUS_APE_ATOLL("Ape Atoll", 90, ItemID.LAW_RUNE, ItemID.SOUL_RUNE, ItemID.BLOOD_RUNE, ItemID.EARTH_RUNE),
    ;

    public final String title;
    public final int magicReq;
    public final int[] runeReqs;

    ArceuusTeleTabOption(String title, int magicReq, int... runeReqs) {
        this.title = title;
        this.magicReq = magicReq;
        this.runeReqs = runeReqs;
    }
}
