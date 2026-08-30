package org.dreambot.behaviour.training.quests;


import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;

public class CooksAssistant extends Fractal {
    final Area COOKS_AREA = new Area(3205, 3217, 3210, 3212);
    public CooksAssistant() {
        setSimpleName("Cooks Assistant");
        this.acceptCondition = () -> !FreeQuest.COOKS_ASSISTANT.isFinished();
        addChildren(
                new TalkToFractal(
                        () -> true,
                        COOKS_AREA,
                        () -> NPCs.closest("Cook"),
                        "Talk-to",
                        "")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.POT_OF_FLOUR)
                                .addItem(ItemID.EGG)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .setStrict(true))
                        .setSimpleName("Talk to chef")
        );
    }
}
