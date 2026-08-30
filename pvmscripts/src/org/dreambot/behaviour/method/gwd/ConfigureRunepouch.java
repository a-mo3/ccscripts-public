package org.dreambot.behaviour.method.gwd;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.EmptyDeathsCoffer;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfigureRunepouch extends Fractal {
    static int[] POUCH_SLOT_QUANTITIES = new int[]{1624, 1625, 1626};
    // these varbits hold an int with a bit flipped correlating to the type of rune in a slot, this is slot 1, 2, 3
    static int[] POUCH_RUNE_VARBITS = new int[]{29, 1622, 1623};

    // there are
    static Map<Integer, Integer> runesToVarbitID = new HashMap<>();

    static {
        runesToVarbitID.put(ItemID.NATURE_RUNE, 10);
        runesToVarbitID.put(ItemID.LAW_RUNE, 11);
        runesToVarbitID.put(ItemID.SMOKE_RUNE, 20);
    }

    static Area UNDER_FEROX = new Area(3118, 10050, 3211, 9985);

    public ConfigureRunepouch() {
        // check Y so we dont leave GWD after alching during the fight
//        super(() -> true);
        super(() -> Bank.isCached()
                && (Players.getLocal().getY() < 3650 || UNDER_FEROX.contains(Players.getLocal()))
                && (!OwnedItems.contains(ItemID.RUNE_POUCH) || count(ItemID.NATURE_RUNE) < 40 || count(ItemID.SMOKE_RUNE) < 40 || count(ItemID.LAW_RUNE) < 40));

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.RUNE_POUCH)
                .setEnabledCondition(() -> OwnedItems.contains(ItemID.RUNE_POUCH))
                .addItem(ItemID.RUNE_POUCH_NOTE)
                .setEnabledCondition(() -> !OwnedItems.contains(ItemID.RUNE_POUCH))
                .addItem(ItemID.NATURE_RUNE, 300)
                .setEnabledCondition(() -> count(ItemID.NATURE_RUNE) < 40)
                .addItem(ItemID.LAW_RUNE, 300)
                .setEnabledCondition(() -> count(ItemID.LAW_RUNE) < 40)
                .addItem(ItemID.SMOKE_RUNE, 300)
                .setEnabledCondition(() -> count(ItemID.SMOKE_RUNE) < 40)
        ;
    }

    int[] desiered = new int[]{ItemID.NATURE_RUNE, ItemID.LAW_RUNE, ItemID.SMOKE_RUNE};

    @Override
    public int onLoop() {
        // get the rune pouch
        if (!OwnedItems.contains(ItemID.RUNE_POUCH)) {
            if (Widgets.isOpen()) Widgets.closeAll();
            if (Dialogues.inDialogue()) {
                String dialogue = Dialogues.getNPCDialogue();
                if (dialogue != null && dialogue.contains("already have one")) {
                    log("Already have a rune pouch, force a coffer empty");
                    EmptyDeathsCoffer.forceEmpty = true;
                    return ReactionGenerator.getNormal();
                }

                log("Talk to");
                Dialog.solve("Yes", "");
                return ReactionGenerator.getNormal();
            }

            if (BankLocation.GRAND_EXCHANGE.distance(Players.getLocal().getTile()) > 15) {
                if (Walking.shouldWalk()) Walking.walk(BankLocation.GRAND_EXCHANGE);
                return ReactionGenerator.getNormal();
            }

            log("Hand in note for a rune pouch");
            NPC banker = NPCs.closest("Banker");
            if (banker != null) {
                log("Use note on banker");
                Item note = Inventory.get(ItemID.RUNE_POUCH_NOTE);
                if (note != null) note.useOn(banker);
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

        if (isFull() && !containsAll(ItemID.NATURE_RUNE, ItemID.LAW_RUNE, ItemID.SMOKE_RUNE)) {
            log("Pouch is full but does not contain all our runes, empty it");
            Inventory.interact(ItemID.RUNE_POUCH, "Empty");
            return ReactionGenerator.getNormal();
        }

        if (Widgets.isOpen()) Widgets.closeAll();

        for (int id : desiered) {
            if (Inventory.contains(id)) {
                Inventory.combine(id, ItemID.RUNE_POUCH);
                Sleep.sleep(600, 1800);
            } else {
                log("Missing rune " + id);
            }
        }

        return ReactionGenerator.getNormal();
    }

    public static boolean isFull() {
        return Arrays.stream(POUCH_SLOT_QUANTITIES).allMatch(x -> PlayerSettings.getBitValue(x) > 0);
    }

    public static boolean containsAll(int... ids) {
        return Arrays.stream(ids).allMatch(ConfigureRunepouch::contains);
    }

    public static boolean contains(int id) {
        for (int i : POUCH_RUNE_VARBITS) {
            int result = PlayerSettings.getBitValue(i);
            if (result == runesToVarbitID.get(id)) return true;
        }
        return false;
    }

    public static int count(int id) {
        for (int i = 0; i < POUCH_SLOT_QUANTITIES.length; i++) {
            int result = PlayerSettings.getBitValue(POUCH_RUNE_VARBITS[i]);
            if (result == runesToVarbitID.get(id)) {
                // rune is in this pouch
                return PlayerSettings.getBitValue(POUCH_SLOT_QUANTITIES[i]);
            }
        }
        return 0;
    }
}
