package org.dreambot.behaviour.runemysteries;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.ArrayList;

public class RuneMysteries extends Fractal {
    public static final Area DUKE_HORACIO = new Area(3213, 3218, 3208, 3225, 1);
    public static final Area SEDRIDOR_ROOM = new Area(3096, 9574, 3107, 9566, 0);
    public static final Area AUBURY = new Area(3252, 3404, 3254, 3399, 0);
    final ArrayList<Integer> AUBURY_STATES = new ArrayList<Integer>() {{
        add(3);
        add(4);
    }};
    //    final ImmutableList<Integer> SEDRIDOR_STATES = Arrays.asList(1, 2, 5);
    final ArrayList<Integer> SEDRIDOR_STATES = new ArrayList<Integer>() {{
        add(1);
        add(2);
        add(5);
    }};

    public RuneMysteries() {
        this.acceptCondition = () -> !FreeQuest.RUNE_MYSTERIES.isFinished();
        addChildren(
                new TalkToFractal(() -> !FreeQuest.RUNE_MYSTERIES.isStarted(), DUKE_HORACIO, () -> NPCs.closest("Duke Horacio"))
                        .setDialogueOptions("any quests for me?", "Yes.")
                        .setSimpleName("Talking to Duke Horacio")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemVariants.COMBAT_BRACLET)
                                        .addItem(ItemVariants.AMULET_OF_GLORY)
                                        .setStrict(true)
                        ),
                new TalkToFractal(() -> SEDRIDOR_STATES.contains(FreeQuest.RUNE_MYSTERIES.getConfigValue()), SEDRIDOR_ROOM, () -> NPCs.closest("Archmage Sedridor"))
                        .setDialogueOptions("quest", "Go ahead", "head wizard", "here you are", "Yes")
                        .setSimpleName("Talking to Sedridor"),
                new TalkToFractal(() -> AUBURY_STATES.contains(FreeQuest.RUNE_MYSTERIES.getConfigValue()), AUBURY, () -> NPCs.closest("Aubury"))
                        .setDialogueOptions("package for you")
                        .setSimpleName("Talking to Aubury")
        );
        this.paintArraySupplier = () -> new String[]{
                "Rune mysteries state: " + FreeQuest.RUNE_MYSTERIES.getConfigValue()
        };
    }
}
