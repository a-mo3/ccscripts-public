package org.dreambot.behaviour.quests.pandemonium;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.generic.TalkToFractal;

import java.util.function.Supplier;

/**
 * tutorial quest for sailing
 */
public class Pandemonium extends Fractal {
    public Pandemonium() {
//        super(() -> !PaidQuest.PANDEMONIUM.isFinished());
        super(() -> true);
        setSimpleName("Pandemonium");

        paintArraySupplier = () -> new String[]{
                "State " + getState(),
                ""
        };

        // varbits for your heading once you align to that direction
//        4:41:18 AM: Varbit 19142: 64 -> 0
//        4:41:18 AM: Varbit 19141: 96 -> 32

        addChildren(
                // talk to will to start
                new TalkToFractal(() -> getState() <= 4,
                        new Tile(3024, 3208),
                        () -> NPCs.closest("Will"))
                        .setDialogueOptions("Yes", "I guess")
                        .setSimpleName("Start quest"),
                // todo sail and such once its released
                // 6: is intro dialogue while on ship, 14532, 10820, not instance

                // 8: help arrow on rudder

                // 12: 14532, 10821

                // 14 after deploy hook we go into instance cut scene

                // 16 end of cutscene you wake up on pandemonium, 3050, 2968
                new TalkToFractal(() -> getState() == 16,
                        new Tile(3051, 2968),
                        () -> NPCs.closest("'Squawking' Steve Beanie"))
                        .setDialogueOptions("tell me")
                        .setSimpleName("Talk to Steve"),

                // 20 after you dialogue with squawking steve beanie, talk to ribs at 3051, 2973
                // tell me about him
                new TalkToFractal(() -> getState() == 20,
                        new Tile(3051, 2973),
                        () -> NPCs.closest("Ribs"))
                        .setDialogueOptions("tell me")
                        .setSimpleName("Talk to ribs"),

                // 22 talk to steve again
                new TalkToFractal(() -> getState() == 22,
                        new Tile(3051, 2968),
                        () -> NPCs.closest("'Squawking' Steve Beanie"))
                        .setDialogueOptions("About", "What exactly")
                        .setSimpleName("Talk to Steve"),

                // 24 talk to junior jim at

                new TalkToFractal(() -> getState() <= 26,
                        new Tile(3057, 2979),
                        () -> NPCs.closest("Junior Jim"))
                        .setDialogueOptions("tell me")
                        .setSimpleName("Talk to jim"),

                // 28 you need to keep going through dialogues but you go into a task to build ships. 2083, 2721
                // build cargo hold

                // 30, todo update area here, it starts at boat dock and transport you back to jim
                new TalkToFractal(() -> getState() == 30,
                        new Tile(3057, 2979),
                        () -> NPCs.closest("Junior Jim"))
                        .setDialogueOptions("tell me")
                        .setSimpleName("Talk to jim"),

                // 34 board your raft

                // 36 sail your raft home

                // 38 back at port sarim, take cargo from ledger table
                /*
                ame: Ledger table ID: 60322 Real ID: 60320 Tile: (3028, 3194, 0) Local X/Y: 52, 34
                 Actions: [Take-cargo, Take-last-cargo, null, null, null] Index: -4503536375017164 Orientation: 0
                 */
                // cargo goes in your hand slot, id 32807 (Crate of ship parts), you need empty hands

                // 40 back on your ship
                // 40-42  take cargo out of boat and bring it to ledger table
                // 44 talk to jim
                new TalkToFractal(() -> getState() == 44,
                        new Tile(3057, 2979),
                        () -> NPCs.closest("Junior Jim"))
                        .setDialogueOptions("tell me")
                        .setSimpleName("Talk to jim"),

                // 46 talk to steve again, you need space
                new TalkToFractal(() -> getState() <= 50,
                        new Tile(3051, 2968),
                        () -> NPCs.closest("'Squawking' Steve Beanie"))
                        .setDialogueOptions("About", "What exactly")
                        .setSimpleName("Talk to Steve")


                );

    }

    int getState() {
        return PaidQuest.PANDEMONIUM.getConfigValue();
    }
}
