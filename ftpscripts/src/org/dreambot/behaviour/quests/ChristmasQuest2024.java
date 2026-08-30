package org.dreambot.behaviour.quests;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;

public class ChristmasQuest2024 extends Fractal {
    public ChristmasQuest2024() {
        super(() -> getState() < 60);
        setSimpleName("2024 Christmas Event");

        addChildren(
                new TalkToFractal(() -> getState() < 20,
                        new Tile(2991, 3385, 0),
                        () -> NPCs.closest("Cecilia"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Start quest"),

                new TalkToFractal(() -> getState() < 25,
                        new Tile(3054, 3373, 0),
                        () -> NPCs.closest("Party Pete"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Ask to host w/ party pete"),


                new TalkToFractal(() -> getState() < 30,
                        new Tile(2991, 3385, 0),
                        () -> NPCs.closest("Cecilia"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Get invites"),
                // hand out invitations
                new TalkToFractal(() -> !Inventory.contains("Invitations") && getState() < 40,
                        new Tile(2991, 3385, 0),
                        () -> NPCs.closest("Cecilia"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Re-get invites"),

                new TalkToFractal(() -> getInviteState() < 1,
                        new Tile(2959, 3338, 2),
                        () -> NPCs.closest("Sir Amik Varze"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Invite Sir Amik"),

                new TalkToFractal(() -> getInviteState() < 3,
                        new Tile(2943, 3377, 0),
                        () -> NPCs.closest("Hairdresser"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Invite Hairdresser"),

                new TalkToFractal(() -> getInviteState() < 7,
                        new Tile(3039, 3293, 0),
                        () -> NPCs.closest("Sarah"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Invite Sarah"),

                new TalkToFractal(() -> getInviteState() < 15,
                        new Tile(3156, 3413, 0),
                        () -> NPCs.closest("Gertrude"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Invite Gertrude"),

                new TalkToFractal(() -> getInviteState() < 31,
                        new Tile(3209, 3391, 0),
                        () -> NPCs.closest("Charlie the Tramp"))
                        .setDialogueOptions("Christmas", "Yes")
                        .setSimpleName("Invite Charlie"),
                // Tell people which dog to take

                new TalkToFractal(() -> getDogState() < 1,
                        new Tile(3046, 3376, 0),
                        () -> NPCs.closest("Sir Amik Varze"))
                        .setDialogueOptions("Corgi")
                        .setSimpleName("Corgi for Amik Varze"),

                new TalkToFractal(() -> getDogState() < 3,
                        new Tile(3046, 3376, 0),
                        () -> NPCs.closest("Hairdresser"))
                        .setDialogueOptions("Yorkie")
                        .setSimpleName("Yorkie Hairdresser"),

                new TalkToFractal(() -> getDogState() < 7,
                        new Tile(3046, 3376, 0),
                        () -> NPCs.closest("Sarah"))
                        .setDialogueOptions("Shepherd")
                        .setSimpleName("Shepherd Sarah"),

                new TalkToFractal(() -> getDogState() < 15,
                        new Tile(3046, 3376, 0),
                        () -> NPCs.closest("Gertrude"))
                        .setDialogueOptions("unidentified")
                        .setSimpleName("??? Gertrude"),

                new TalkToFractal(() -> true,
                        new Tile(3046, 3376, 0),
                        () -> NPCs.closest("Cecilia"))
                        .setDialogueOptions("")
                        .setSimpleName("Get Loot")

//                new TalkToFractal(() -> getDogState() < 31,
//                        new Tile(3046, 3376, 0),
//                        () -> NPCs.closest("Charlie the Tramp"))
//                        .setDialogueOptions("Christmas", "Yes")
//                        .setSimpleName("Invite Charlie")

                // collect loot
        );
    }

    private static int getState() {
        return PlayerSettings.getBitValue(6275);
    }

    private int getInviteState() {
        // each invite is one of the bits but this wont be public so idc
        return PlayerSettings.getBitValue(15800);
    }

    private int getDogState() {
        // this is another packed int i dont care to split into single requirements
        return PlayerSettings.getBitValue(15905);
    }
}
