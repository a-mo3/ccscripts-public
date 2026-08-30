package org.dreambot.behaviour.quests;


import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;

public class RomeoAndJulietBranch extends Fractal {
    final Area ROMEO_FOUNTAIN = new Area(3206, 3436, 3221, 3422);
    final Area JULIET_HOUSE = new Area(3154, 3426, 3161, 3425, 1);
    final Area CHURCH = new Area(3252, 3484, 3259, 3471);
    final Area APOTHECARY = new Area(3192, 3406, 3198, 3402);

    public RomeoAndJulietBranch() {
        this.acceptCondition = () -> !FreeQuest.ROMEO_AND_JULIET.isFinished();

        this.paintArraySupplier = () -> new String[]{
                "State: " + FreeQuest.ROMEO_AND_JULIET.getConfigValue()
        };

        final int LOVE_LETTER = 755;

        addChildren(
                new TalkToFractal(
                        () -> {
                            int state = FreeQuest.ROMEO_AND_JULIET.getConfigValue();
                            return state == 0 || state == 20 || state == 60;
                        },
                        ROMEO_FOUNTAIN,
                        () -> NPCs.closest("Romeo"),
                        "Talk-to",
                        "Yes, I have seen her actually!",
                        "Yes, ok, I'll let her know.", "Yes.")
                        .setSleepTimeout(12400) //extra long because sometimes romeo is in the store
                        .setSimpleName("Talk to romeo")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(LOVE_LETTER)
                                .setEnabledCondition(() -> OwnedItems.contains(LOVE_LETTER))
                                .setStrict(true)
                        )
                        .setPrependLogic(() -> {
                            Sleep.sleep(600);
                            if (Client.isInCutscene()) {
                                Dialog.solve();
                                return true;
                            }
                            return false;
                        }),
                new TalkToFractal(
                        () -> FreeQuest.ROMEO_AND_JULIET.getConfigValue() == 10 || (FreeQuest.ROMEO_AND_JULIET.getConfigValue() == 50 && Inventory.contains("Cadava potion")),
                        JULIET_HOUSE,
                        () -> NPCs.closest("Juliet"),
                        "Talk-to",
                        "Ok, thanks.",
                        "Talk about something else",
                        "Talk about Romeo"
                ).setSimpleName("Talk to Juliet"),
                new TalkToFractal(
                        () -> FreeQuest.ROMEO_AND_JULIET.getConfigValue() == 30,
                        CHURCH,
                        () -> NPCs.closest(NpcID.FATHER_LAWRENCE),
                        "Talk-to",
                        "Ok, thanks."
                ).setSimpleName("Talk to Lawrence"),
                new TalkToFractal(
                        () -> FreeQuest.ROMEO_AND_JULIET.getConfigValue() >= 40 && (!Inventory.contains("Cadava potion")),
                        APOTHECARY,
                        () -> NPCs.closest(NpcID.APOTHECARY),
                        "Talk-to",
                        "Ok, thanks.",
                        "Talk about something else",
                        "Talk about Romeo")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CADAVA_BERRIES)
                                .setStrict(true))
                        .setSimpleName("Potion")
        );

    }
}
