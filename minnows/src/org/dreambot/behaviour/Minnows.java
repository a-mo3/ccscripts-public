package org.dreambot.behaviour;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.data.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Minnows extends Fractal {
    public static final Area MINNOWS_PLATFORM = new Area(2605, 3448, 2623, 3439);
    final Area KYLIE_MINNOW_AREA = new Area(2599, 3426, 2600, 3422);

    final List<Integer> minnowSpots = new ArrayList<Integer>() {{
        add(7730);
        add(7731);
        add(7732);
        add(7733);
    }};

    public Minnows() {
        setSimpleName("Minnows");
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.CHEST, ItemID.ANGLER_TOP)
                .addItem(EquipmentSlot.HAT, ItemID.ANGLER_HAT)
                .addItem(EquipmentSlot.FEET, ItemID.ANGLER_BOOTS)
                .addItem(EquipmentSlot.LEGS, ItemID.ANGLER_WADERS)
                .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
        ;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SMALL_FISHING_NET)
                .strictIgnore(
                        ItemID.MINNOW,
                        ItemID.RAW_SHARK,
                        ItemID.RAW_SHARK + 1,
                        ItemID.CLUE_BOTTLE_BEGINNER,
                        ItemID.CLUE_BOTTLE_EASY,
                        ItemID.CLUE_BOTTLE_ELITE,
                        ItemID.CLUE_BOTTLE_HARD,
                        ItemID.CLUE_BOTTLE_MEDIUM
                )
                .setStrict(true);
        DecimalFormat df = new DecimalFormat("###,###,###");
        this.paintArraySupplier = () -> {
            int minnowCount = Inventory.count(ItemID.MINNOW);
            int sharks = minnowCount / 40;
            long gp = (long) sharks * LivePrices.get(ItemID.RAW_SHARK);

            return new String[]{
                    String.format("Minnows: %s", df.format(minnowCount)),
                    String.format("Redeemable sharks: %s", df.format(sharks)),
                    String.format("Valued: %s", df.format(gp)),
                    String.format("Time remaining until mule off: %s", formatTime(MuleOff.timer.remaining())),
            };
        };
    }

    @Override
    public boolean isValid() {
        return OwnedItems.containsAll(
                ItemID.ANGLER_BOOTS,
                ItemID.ANGLER_HAT,
                ItemID.ANGLER_TOP,
                ItemID.ANGLER_WADERS
        );
    }

    int blacklist = 5;

    @Override
    public int onLoop() {
        if (!Walking.isRunEnabled() && Walking.getRunEnergy() > 30) {
            Walking.toggleRun();
            return ReactionGenerator.getNormal();
        }

        if (!MINNOWS_PLATFORM.contains(Players.getLocal())) {
            if (Bank.contains(ItemID.MINNOW)) {
                Logger.info("Getting minnow");
                if (Walking.shouldWalk() && Bank.open()) {
                    Bank.withdrawAll(ItemID.MINNOW);
                }
                return ReactionGenerator.getNormal();
            }

            if (!KYLIE_MINNOW_AREA.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(KYLIE_MINNOW_AREA);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            if (!hasUnlockedPlatform()) {
                NPC kylie = NPCs.closest("Kylie minnow");
                if (kylie != null) {
                    kylie.interact();
                }
                return ReactionGenerator.getNormal();
            }

            GameObject rowBoat = GameObjects.closest("Row boat");
            if (rowBoat != null && rowBoat.interact("Travel to platform")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        NPC minnowSpot = NPCs.closest(x -> minnowSpots.contains(x.getID()) && x.getID() != blacklist);
        if (minnowSpot != null) {
            if (minnowSpot.getRenderableHeight() == 1000) {
                blacklist = minnowSpot.getID();
                return ReactionGenerator.getQuick();
            }
            minnowSpot.interact("Small Net");
            Sleep.sleepUntil(() -> minnowSpot.distance() <= 1, 2400);
            Sleep.sleepUntil(() -> minnowSpot.distance() > 1 || minnowSpot.getRenderableHeight() == 1000,
                    () -> Players.getLocal().isAnimating(), 2400, 100);
        }
        return ReactionGenerator.getNormal();
    }


    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }

    public boolean hasUnlockedPlatform() {
        return PlayerSettings.getBitValue(5669) == 2;
    }
}
