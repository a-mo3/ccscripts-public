package org.dreambot;

import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Timer;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.behaviour.method.MLMMining;
import org.dreambot.behaviour.method.MLMTopFloor;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;

import java.text.DecimalFormat;


public class MLMPaint implements PaintInfo {
    int cachedLootValue = 0;
    Timer lootValueCacheTimer = new Timer(60 * 1000);
    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        if (lootValueCacheTimer.finished()) {
            cachedLootValue = 0;
            for (int id : MuleOff.itemsToMule) {
                cachedLootValue += OwnedItems.count(id) * LivePrices.get(id);
            }
            lootValueCacheTimer.reset();
        }


        return new String[]{
                "Ore in sack: " + PlayerSettings.getBitValue(MLMMining.ORE_IN_SACK_VARBIT),
                "Nuggets: " + OwnedItems.count(ItemID.GOLDEN_NUGGET),
                "Mining level: " + Skills.getRealLevel(Skill.MINING),
                "Ore value: " + df.format(cachedLootValue),
                "Top floor: " + MLMTopFloor.topFloorState,
                "Top Hopper: " + MLMTopFloor.topFloorHopperState
        };
    }
}
