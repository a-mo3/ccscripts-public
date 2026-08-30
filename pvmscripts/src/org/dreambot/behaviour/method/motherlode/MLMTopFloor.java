package org.dreambot.behaviour.method.motherlode;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class MLMTopFloor extends Fractal {
    final boolean useTopFloor;
    public MLMTopFloor(boolean useTopFloor) {
        this.useTopFloor = useTopFloor;
    }

    enum TopFloorState {
        LOCKED,
        UNKNOWN,
        UNLOCKED,
    }

    public static final Area ALLOWED_TOPLEVEL_VEINS = new Area(
            new Tile(3745, 5683, 0),
            new Tile(3751, 5685, 0),
            new Tile(3754, 5685, 0),
            new Tile(3754, 5679, 0),
            new Tile(3755, 5677, 0),
            new Tile(3759, 5676, 0),
            new Tile(3763, 5674, 0),
            new Tile(3762, 5667, 0));

    public static final int UPPER_LEVEL_LADDER = 19045;
    public static final int LOWER_LEVEL_LADDER = 19044;

    public static TopFloorState topFloorState = TopFloorState.UNKNOWN;

    @Override
    public boolean isValid() {
        if (!useTopFloor) return false;
        if (ALLOWED_TOPLEVEL_VEINS.contains(Players.getLocal())) {
            topFloorState = TopFloorState.UNLOCKED;
        }

        if (!MLMMining.MLM_INNER.contains(Players.getLocal())
                && !ALLOWED_TOPLEVEL_VEINS.contains(Players.getLocal())) return false;

        // todo if you are already on the top level set to unlocked,
        if (Skills.getRealLevel(Skill.MINING) < 72) {
            return false;
        }

        if (topFloorState != TopFloorState.LOCKED) {
            // unlocked or unknown you have to find out!
            return true;
        }

        // its locked, but you can unlock it
        if (OwnedItems.count(ItemID.GOLDEN_NUGGET) >= 100) {
            return true;
        }
        return false;
    }

    @Override
    public int onLoop() {
        if (topFloorState == TopFloorState.LOCKED && OwnedItems.count(ItemID.GOLDEN_NUGGET) >= 100) {
            if (Inventory.count(ItemID.GOLDEN_NUGGET) < 100) {
                if (Inventory.isFull()) {
                    Inventory.dropAll(ItemID.PAYDIRT);
                    return ReactionGenerator.getNormal();
                }

                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open(BankLocation.MOTHERLODE_MINE);
                    return ReactionGenerator.getQuick();
                }

                Bank.withdraw(ItemID.GOLDEN_NUGGET, Bank.count(ItemID.GOLDEN_NUGGET));
                return ReactionGenerator.getNormal();
            }

            if (Bank.isOpen()) Bank.close();
            String[] options = Dialogues.getOptions();
            if (options != null && Arrays.stream(options).anyMatch(x -> x.contains("nuggets") && Arrays.stream(options).noneMatch(i -> i.contains("100")))) {
                topFloorState = TopFloorState.UNLOCKED;
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("100", "unlock", "Yes");
                return ReactionGenerator.getNormal();
            }

            // todo might have to walk to him but probably not,
            //  this should only happen when you just got shit out of sack or just started script
            NPC percy = NPCs.closest("Prospector Percy");
            if (percy != null) {
                percy.interact();
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        String dialogue = Dialogues.getNPCDialogue();
        if (Dialogues.inDialogue() && dialogue != null) {
            Logger.info("Dialogue " + dialogue);
            if (dialogue.contains("Mining first") || dialogue.contains("Gimme 100")) {
                topFloorState = TopFloorState.LOCKED;
            }
        }

        int oreInSack = PlayerSettings.getBitValue(MLMMining.ORE_IN_SACK_VARBIT);
        if (oreInSack > 54) {
            Log.info("Empty event: " + new EmptyBagEvent().executed());
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) {
            Log.info("Deposit event: " + new DepositPaydirtEvent().executed());
            return ReactionGenerator.getNormal();
        }

        GameObject lowerLadder = GameObjects.closest(LOWER_LEVEL_LADDER);
        Logger.info("Lower ladder " + lowerLadder);
        if (lowerLadder != null && lowerLadder.getSurrounding().stream().anyMatch(Locatable::canReach)) {
            // in lower level, climp up ladder
            if (!Menu.isMenuManipulationActive() && lowerLadder.distance() > 7) {
                log("Walk toward ladder menu manip off");
                if (Walking.shouldWalk()) Walking.walk(lowerLadder);
                return ReactionGenerator.getNormal();
            }
            lowerLadder.interact();
            Sleep.sleepUntil(() -> !lowerLadder.canReach(), 2400);
            return ReactionGenerator.getNormal();
        }

        GameObject vein = GameObjects.closest(x -> x.getName().equals("Ore vein") && ALLOWED_TOPLEVEL_VEINS.contains(x));
        if (vein != null && vein.interact("Mine")) {
            Sleep.sleepUntil(Inventory::isFull, () -> Players.getLocal().isAnimating(), 2000, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
