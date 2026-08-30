package org.dreambot.behaviour.quests;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;

public class SheepShearer extends Fractal {
    public SheepShearer() {
        this.acceptCondition = () -> !FreeQuest.SHEEP_SHEARER.isFinished();
        Area farmerFredPoint = new Tile(3190, 3273, 0).getArea(3);
        addChildren(
                new TalkToFractal(() -> true, farmerFredPoint, () -> NPCs.closest("Fred the farmer"))
                        .setDialogueOptions("quest.", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.BALL_OF_WOOL, 1, 20))
                        .setSimpleName("Give wool.")
        );
    }
}
