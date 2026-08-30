package org.dreambot.behaviour.childrenofthesun;

import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class ChildrenOfTheSun extends Fractal {
    public static final int GUARD_ID = 12661;

    final int GUARD_1_CHANGE_VARBIT = 9633;
    final int GUARD_2_CHANGE_VARBIT = 9634;
    final int GUARD_3_CHANGE_VARBIT = 9635;
    final int GUARD_4_CHANGE_VARBIT = 9636;

    private boolean isNotMarked(int varbit) {
        return PlayerSettings.getBitValue(varbit) != 2;
    }

    public ChildrenOfTheSun() {
        super(() -> !PaidQuest.CHILDREN_OF_THE_SUN.isFinished());

        paintArraySupplier = () -> new String[]{
                "State: " + getState()
        };

        addChildren(
                new TalkToFractal(() -> (getState() == 4 || getState() >= 8) && Dialogues.inDialogue(), new Tile(1, 1), () -> null)
                        .setSimpleName("Handle Cutscene")
                        .setAppendLogic(() -> {
                            Dialog.solve("");
                            return true;
                        }),
                new FollowGuardCOS(() -> getState() == 6 && Client.isDynamicRegion()).setSimpleName("Trail Guard"),
                new TalkToFractal(() -> getState() <= 6 && !Client.isDynamicRegion(), new Tile(3225, 3426), () -> NPCs.closest("Alina"))
                        .setDialogueOptions("delegation arrive?", "Yes.")
                        .setSimpleName("Start COS"),

                // talk to guard
                new TalkToFractal(() -> getState() == 10, new Tile(3211, 3437), () -> NPCs.closest("Sergeant Tobyn"))
                        .setSimpleName("Talk to tobyn"),

                // mark guards
                new Fractal(() -> getState() == 12)
                        .setSimpleName("Mark Guard ")
                        .addChildren(
                                new TalkToFractal(() -> isNotMarked(GUARD_1_CHANGE_VARBIT), new Tile(3208, 3422), () -> NPCs.closest(6923))
                                        .setInteraction("Mark")
                                        .setSimpleName("1"),
                                new TalkToFractal(() -> isNotMarked(GUARD_2_CHANGE_VARBIT), new Tile(3221, 3430), () -> NPCs.closest(6924))
                                        .setInteraction("Mark")
                                        .setSimpleName("2"),
                                new TalkToFractal(() -> isNotMarked(GUARD_3_CHANGE_VARBIT), new Tile(3246, 3429), () -> NPCs.closest(6925))
                                        .setInteraction("Mark")
                                        .setSimpleName("3"),
                                new TalkToFractal(() -> isNotMarked(GUARD_4_CHANGE_VARBIT), new Tile(3237, 3427), () -> NPCs.closest(7083))
                                        .setInteraction("Mark")
                                        .setSimpleName("4"),
                                new TalkToFractal(() -> true, new Tile(3211, 3437), () -> NPCs.closest("Sergeant Tobyn"))
                                        .setSimpleName("Talk to tobyn")
                        )
        );
    }

    private int getState() {
        return PaidQuest.CHILDREN_OF_THE_SUN.getConfigValue();
    }

    @Override
    public int onLoop() {
        return ReactionGenerator.getNormal();
    }
}
