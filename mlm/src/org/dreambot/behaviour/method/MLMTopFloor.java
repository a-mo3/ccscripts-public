package org.dreambot.behaviour.method;

import org.dreambot.api.Client;
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
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Locatable;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;

public class MLMTopFloor extends Fractal implements ChatListener {
    /*
    used for if you have unlocked the top floor and the top floor hopper
     */
    public enum AccessState {
        LOCKED,
        UNKNOWN,
        UNLOCKED,
    }

    public MLMTopFloor() {
        Client.getInstance().addEventListener(this);
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

    public static AccessState topFloorState = AccessState.UNKNOWN;
    public static AccessState topFloorHopperState = AccessState.UNKNOWN;


    @Override
    public boolean isValid() {
        if (ScriptSettings.getSettingsData().isDisableTopFloor()) return false;
        if (ALLOWED_TOPLEVEL_VEINS.contains(Players.getLocal())) {
            topFloorState = AccessState.UNLOCKED;
        }

        if (!MLMMining.MLM_INNER.contains(Players.getLocal())
                && !ALLOWED_TOPLEVEL_VEINS.contains(Players.getLocal())) return false;

        // todo if you are already on the top level set to unlocked,
        if (Skills.getRealLevel(Skill.MINING) < 57) {
            return false;
        }

        if (topFloorState != AccessState.LOCKED) {
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
        if (topFloorState == AccessState.LOCKED && OwnedItems.count(ItemID.GOLDEN_NUGGET) >= 100) {
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
            String[] options =  Dialogues.getOptions();
            if (options != null && Arrays.stream(options).anyMatch(x -> x.contains("nuggets") && Arrays.stream(options).noneMatch(i -> i.contains("100")))) {
                topFloorState = AccessState.UNLOCKED;
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

        if (topFloorHopperState== AccessState.LOCKED && OwnedItems.count(ItemID.GOLDEN_NUGGET) >= 50) {
            if (Inventory.count(ItemID.GOLDEN_NUGGET) < 50) {
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
            if (Dialogues.inDialogue()) {
                log("Chat to percy");
                Dialog.solve("50", "unlock", "Yes");
                return ReactionGenerator.getNormal();
            }

            GameObject topLadder = GameObjects.closest(MLMTopFloor.UPPER_LEVEL_LADDER);
            if (topLadder != null && topLadder.getSurrounding().stream().anyMatch(Locatable::canReach)) {
                log("Get off top");
                topLadder.interact("Climb");
                Sleep.sleepUntil(() -> topLadder.getSurrounding().stream().noneMatch(Locatable::canReach), 2400);
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
                topFloorState = AccessState.LOCKED;
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
            lowerLadder.interact();
            Sleep.sleepUntil(() -> !lowerLadder.canReach(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (topFloorHopperState == AccessState.UNKNOWN) {
            log("Checking top floor hopper state");
            GameObject topHop = GameObjects.closest(26674);
            if (topHop == null) {
                log("Cant find the top hopper");
                return ReactionGenerator.getNormal();
            }

            topHop.interact();
            return ReactionGenerator.getNormal();
        }

        GameObject vein = GameObjects.closest(x -> x.getName().equals("Ore vein") && ALLOWED_TOPLEVEL_VEINS.contains(x));
        if (vein != null && vein.interact("Mine")) {
            Sleep.sleepUntil(Inventory::isFull, () -> Players.getLocal().isAnimating(), 2000, 100);
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        Logger.info("msg " + message.getMessage());
/*
11:21:INFO] msg Mercy|I don't think you should be using that without speaking to Percy first.
 */
        if (message.getMessage().contains("be using that without speaking to Percy f")) {
            log("Top floor hopper locked");
            topFloorHopperState = AccessState.LOCKED;
        }
        // 11:36:: [INFO] msg You pay Percy 50 nuggets.
//        11: [INFO] msg You don't have any pay-dirt to put in the hopper.

        if (message.getMessage().contains("You pay Percy 50 nuggets") || message.getMessage().contains("have any pay-dirt to put in the hopper")) {
            log("Unlocked top hopper");
            topFloorHopperState = AccessState.UNLOCKED;
        }

        // todo no paydirt message
    }
}
