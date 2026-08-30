package org.dreambot.settings.script;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.data.ItemID;

public class ScriptSettings {
    @Setter
    @Getter
    private static SettingsData settingsData = new SettingsData();

    public static int getMinGP() {
        return settingsData.initalGp;
    }

    public static int getPrayerPotionCount() {
        return settingsData.prayerPotionCount > 4 ? settingsData.prayerPotionCount : 4;
    }


    public static int getPrayerTarget() {
        return settingsData.prayerTarget > 43 ? settingsData.prayerTarget : 43;
    }

    public static int getRewardTarget() {
        return settingsData.rewardTarget < 100 ? 750 : settingsData.rewardTarget;
    }

    public static int getMeleeTorso() {
        if (settingsData.meleeTorsoID < 1) {
            Logger.info("default to rune chainbody");
            return ItemID.RUNE_CHAINBODY;
        }
        Logger.info("using melee torso: " + new Item(settingsData.meleeTorsoID, 0).getName());
        return settingsData.meleeTorsoID;
    }

    public static int getMeleeLegs() {
        if (settingsData.meleeLegsID < 1) return ItemID.RUNE_PLATELEGS;
        Logger.info("using melee legs: " + new Item(settingsData.meleeLegsID, 0).getName());
        return settingsData.meleeLegsID;
    }

    static Area GE = BankLocation.GRAND_EXCHANGE.getArea(50);
    //
    static int lastRangeTorso = -1;

    public static int getRangeTorso() {
        if (settingsData.progressiveRangeGear) {
            if (GE.contains(Players.getLocal()) || lastRangeTorso < 0) {
                int rng = Skills.getRealLevel(Skill.RANGED);
                if (rng >= 70) {
                    lastRangeTorso = ItemID.BLACK_DHIDE_BODY;
                    return ItemID.BLACK_DHIDE_BODY;
                }
                if (rng >= 60) {
                    lastRangeTorso = ItemID.RED_DHIDE_BODY;
                    return ItemID.RED_DHIDE_BODY;
                }
                if (rng >= 50) {
                    lastRangeTorso = ItemID.BLUE_DHIDE_BODY;
                    return ItemID.BLUE_DHIDE_BODY;
                }
            } else {
                return lastRangeTorso;
            }
        }

        if (settingsData.rangeTorsoID < 1) return ItemID.BLUE_DHIDE_BODY;
        Logger.info("using range torso: " + new Item(settingsData.rangeTorsoID, 0).getName());
        return settingsData.rangeTorsoID;
    }


    // we do this caching to make sure we dont change what we are using during barrows and break the equipment switch
    static int lastRangeChaps = -1;

    public static int getRangeLegs() {
        if (settingsData.progressiveRangeGear) {
            // if no cache just assume we are using the level appropriate one.
            Logger.info("range chaps " + lastRangeChaps);
            if (GE.contains(Players.getLocal()) || lastRangeChaps < 0) {
                int rng = Skills.getRealLevel(Skill.RANGED);
                Logger.info("returning level");
                if (rng >= 70) {
                    lastRangeChaps = ItemID.BLACK_DHIDE_CHAPS;
                    return ItemID.BLACK_DHIDE_CHAPS;
                }
                if (rng >= 60) {
                    lastRangeChaps = ItemID.RED_DHIDE_CHAPS;
                    return ItemID.RED_DHIDE_CHAPS;
                }
                if (rng >= 50) {
                    lastRangeChaps = ItemID.BLUE_DHIDE_CHAPS;
                    return ItemID.BLUE_DHIDE_CHAPS;
                }
            } else {
                Logger.info("returning last");
                return lastRangeChaps;
            }
        }

        if (settingsData.rangeLegsID < 1) return ItemID.BLUE_DHIDE_CHAPS;
        Logger.info("using range legs: " + new Item(settingsData.rangeLegsID, 0).getName());
        return settingsData.rangeLegsID;
    }
}
