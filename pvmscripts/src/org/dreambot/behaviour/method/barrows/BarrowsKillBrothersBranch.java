package org.dreambot.behaviour.method.barrows;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.behaviour.method.barrows.killbrothers.BarrowsFirstTimeDialogue;
import org.dreambot.behaviour.method.barrows.killbrothers.BarrowsGoToBrother;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BarrowsEat;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BarrowsPotion;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.BrotherAttack;
import org.dreambot.behaviour.method.barrows.killbrothers.decisions.KillBrotherSetPrayers;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Handle killing 4/5 brothers
 */
public class BarrowsKillBrothersBranch extends Fractal {
    // brother that the tunnel is for entering
    public static BarrowsBrother tunnelBrother = null;
    final BarrowsLoadout loadout;

    public static final Area OLD_MAN_ROAM = new Area(
            new Tile(3552, 3296, 0),
            new Tile(3563, 3304, 0),
            new Tile(3580, 3300, 0),
            new Tile(3581, 3275, 0),
            new Tile(3566, 3274, 0),
            new Tile(3549, 3283, 0)
    );

    public BarrowsKillBrothersBranch(Supplier<Boolean> acceptCondition, BarrowsLoadout loadout) {
        super(acceptCondition);
        this.loadout = loadout;
        setSimpleName("Kill brothers");

        addChildren(
                // Handle old guy first time dialogue
                new BarrowsFirstTimeDialogue()
                        .setSimpleName("Old man"),

                new UseOnFractal(
                        () -> Players.getLocal().getZ() == 0 && Inventory.contains(ItemID.STRANGE_ICON),
                        () -> Inventory.get(ItemID.STRANGE_ICON),
                        () -> NPCs.closest("Strange Old Man"), true)
                        .setArea(OLD_MAN_ROAM)
                        .setDialogueOptions("").setReturnAfterDialogues(true)
                        .setSleepCondition(Dialogues::inDialogue)
                        .setSleepTimeout(7500)
                        .setSimpleName("Hand in icon"),

                // Enter correct barrows brother
                new BarrowsGoToBrother(() -> Arrays.stream(BarrowsBrother.values()).noneMatch(x -> x.tombArea.contains(Players.getLocal())))
                        .setSimpleName("Go to tomb"),


                // Kill brother
                new TickFractal()
                        .addChildren(
                                new BarrowsEat(),
                                new BarrowsPotion(),
                                new KillBrotherSetPrayers(),
                                new BrotherAttack(loadout)
                        )
                        .setSimpleName("Fight")
        );
    }
}
