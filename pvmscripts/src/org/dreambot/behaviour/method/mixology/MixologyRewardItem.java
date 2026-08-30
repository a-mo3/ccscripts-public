package org.dreambot.behaviour.method.mixology;


import lombok.Getter;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.behaviour.method.mixology.data.PotionReagent;
import org.dreambot.fractals.data.ItemID;

import static org.dreambot.behaviour.method.mixology.data.PotionReagent.*;

public enum MixologyRewardItem {
    //    NONE("None", 0, false, 0, 0, 0),
//    APPRENTICE_POTION_PACK("Apprentice Potion Pack", ItemID.APPRENTICE_POTION_PACK, true, 420, 70, 30),
//    ADEPT_POTION_PACK("Adept Potion Pack", ItemID.ADEPT_POTION_PACK, true, 180, 440, 70),
//    EXPERT_POTION_PACK("Expert Potion Pack", ItemID.EXPERT_POTION_PACK, true, 410, 320, 480),
//    PRESCRIPTION_GOGGLES("Prescription Goggles", ItemID.PRESCRIPTION_GOGGLES, false, 8600, 7000, 9350),
    //    ALCHEMIST_LABCOAT("Alchemist Labcoat", ItemID.ALCHEMIST_LABCOAT, false, 2250, 2800, 3700),
//    ALCHEMIST_PANTS("Alchemist Pants", ItemID.ALCHEMIST_PANTS, false, 2250, 2800, 3700),
//    ALCHEMIST_GLOVES("Alchemist Gloves", ItemID.ALCHEMIST_GLOVES, false, 2250, 2800, 3700),
//    REAGENT_POUCH("Reagent Pouch", ItemID.REAGENT_POUCH, false, 13800, 11200, 15100),
//    POTION_STORAGE("Potion Storage", ItemID.POTION_STORAGE, false, 7750, 6300, 8950),
    CHUGGING_BARREL("Chugging barrel (disassembled)", ItemID.CHUGGING_BARREL, false, 17250, 14000, 18600),
    //    ALCHEMISTS_AMULET("Alchemist's Amulet", ItemID.ALCHEMISTS_AMULET, false, 6900, 5650, 7400),
    ALDARIUM("Aldarium", ItemID.ALDARIUM, true, 80, 60, 90);

    private final String itemName;
    private final int itemId;
    @Getter
    private final boolean repeatable;
    private final int[] componentCost = new int[PotionReagent.ENTRIES.length];

    MixologyRewardItem(String itemName, int itemId, boolean repeatable, int moxResinCost, int agaResinCost, int lyeResinCost) {
        this.itemName = itemName;
        this.itemId = itemId;
        this.repeatable = repeatable;
        this.componentCost[MOX.ordinal()] = moxResinCost;
        this.componentCost[AGA.ordinal()] = agaResinCost;
        this.componentCost[LYE.ordinal()] = lyeResinCost;
    }

    public String itemName() {
        return itemName;
    }

    public int itemId() {
        return itemId;
    }

    public int componentCost(PotionReagent component) {
        return componentCost[component.ordinal()];
    }

    public int claimableQuantity() {
        int aga = PlayerSettings.getConfig(4415) / componentCost(AGA);
        int mox = PlayerSettings.getConfig(4416) / componentCost(MOX);
        int lye = PlayerSettings.getConfig(4414) / componentCost(LYE);
        return Math.min(Math.min(aga, mox), lye);
    }
}