package org.dreambot.behaviour.training.sleepinggiants;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.behaviour.foundry.FoundryBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.Operation;
import org.dreambot.fractals.quest.VarbitRequirement;

public class SleepingGiants extends Fractal {
    private static final Tile START_TILE = new Tile(3362, 3147);
    final Area NULL_AREA = null; // giving talk to fractal a null area it will just skip walking somewhere
    VarbitRequirement grindstoneFixed = new VarbitRequirement(13905, 2, Operation.EQUAL);
    VarbitRequirement polishingStoneFixed = new VarbitRequirement(13906, 2, Operation.EQUAL);
    VarbitRequirement hammerFixed = new VarbitRequirement(13904, 2, Operation.EQUAL);


    VarbitRequirement commissionReceived = new VarbitRequirement(13903, 10, Operation.GREATER_EQUAL);
    VarbitRequirement crateSearched = new VarbitRequirement(13903, 15, Operation.GREATER_EQUAL);
    VarbitRequirement crucibleFilled = new VarbitRequirement(13903, 25, Operation.GREATER_EQUAL);
    VarbitRequirement talkedToKovacAboutMould = new VarbitRequirement(13903, 30, Operation.GREATER_EQUAL);
    // REMOVE
    VarbitRequirement shouldSetMould = new VarbitRequirement(13903, 30, Operation.GREATER_EQUAL);
    //REMOVE?
    VarbitRequirement mouldSet = new VarbitRequirement(13903, 35, Operation.GREATER_EQUAL);
    VarbitRequirement talkedToKovacAboutPouringMetal = new VarbitRequirement(13903, 40, Operation.GREATER_EQUAL);
    VarbitRequirement metalPoured = new VarbitRequirement(13903, 45, Operation.GREATER_EQUAL);
    VarbitRequirement preformObtained = new VarbitRequirement(13903, 50, Operation.GREATER_EQUAL);

    public SleepingGiants() {
        this.acceptCondition = () -> !PaidQuest.SLEEPING_GIANTS.isFinished();
        this.paintArraySupplier = () -> new String[]{
                "Sleeping giants: " + PaidQuest.SLEEPING_GIANTS.getState(),
                "Commission state: " + PlayerSettings.getBitValue(13903)
        };
        addChildren(
                new TalkToFractal(() -> PaidQuest.SLEEPING_GIANTS.getConfigValue() <= 5, START_TILE,
                        () -> NPCs.closest(x -> x.getName().equals("Hill Giant") && x.hasAction("Strike")))
                        .setInteraction("Strike")
                        .setDialogueOptions("Yes.")
                        .setSimpleName("Starting sleeping giants")
                        .setInventoryLoadout(
                                new InventoryLoadout()
                                        .addItem(ItemID.OAK_LOGS, 3)
                                        .addItem(ItemID.WOOL).setBuyPrice(1000)
                                        .addItem(ItemID.IRON_NAILS, 10)
                                        .addItem(ItemID.HAMMER)
                                        .addItem(ItemID.CHISEL)
                                        .addItem(ItemID.BUCKET_OF_WATER)
                                        .setStrict(true)
                        )
                        .setEquipmentLoadout(
                                new EquipmentLoadout()
                                        .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                        .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                        )
                        .setAppendLogic(() -> {
                            if (!Equipment.isSlotEmpty(EquipmentSlot.WEAPON) || !Equipment.isSlotEmpty(EquipmentSlot.SHIELD)) {
                                if (!Bank.isOpen()) {
                                    if (Walking.shouldWalk()) Bank.open();
                                    return true;
                                }

                                Bank.depositAllEquipment();
                                return true;
                            }
                            return false;
                        }),
                new Fractal(() -> PaidQuest.SLEEPING_GIANTS.getConfigValue() <= 20).addChildren(
                        new TalkToFractal(polishingStoneFixed::isNotComplete, NULL_AREA, () -> GameObjects.closest("Broken polishing wheel"))
                                .setDoReachCheck(false)
                                .setSleepTimeout(4400).setInteraction("Repair").setDialogueOptions("Yes.").setSimpleName("Fix polishing stone"),
                        new TalkToFractal(grindstoneFixed::isNotComplete, NULL_AREA, () -> GameObjects.closest("Broken grindstone"))
                                .setDoReachCheck(false)
                                .setSleepTimeout(4400).setInteraction("Repair").setDialogueOptions("Yes.").setSimpleName("Fix grindstone"),
                        new TalkToFractal(hammerFixed::isNotComplete, NULL_AREA, () -> GameObjects.closest("Broken trip hammer"))
                                .setDoReachCheck(false)
                                .setSleepTimeout(4400).setInteraction("Repair").setDialogueOptions("Yes.").setSimpleName("Fix trip hammer"),
                        new TalkToFractal(() -> true, NULL_AREA, () -> NPCs.closest("Kovac")).setSimpleName("Talk to kovac after repairs")
                ).setSimpleName("Fixing machines"),
                // do the first commission

                // these arent in null areas but u should just be there anyway and im being mad lazy
                new TalkToFractal(commissionReceived::isNotComplete, NULL_AREA, () -> NPCs.closest("Kovac"))
                        .setSimpleName("Talk to Kovac to get commissiobn"),
                new TalkToFractal(crateSearched::isNotComplete, NULL_AREA, () -> GameObjects.closest("Crate"))
                        .setInteraction("Search").setDialogueOptions("Yes.").setSimpleName("Searching crate"),
                // fill crucible with bars
                new TalkToFractal(() -> crucibleFilled.isNotComplete() && Inventory.contains(x -> x.getName().contains("bar")),
                        NULL_AREA,
                        () -> GameObjects.closest(x -> x.getName().contains("Crucible")))
                        .setInteraction("Fill")
                        .setAppendLogic(() -> {
                            if (ItemProcessing.isOpen()) {
                                ItemProcessing.makeAll(Inventory::contains);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Filling crucible"),
                new UseOnFractal(crucibleFilled::isNotComplete,
                        () -> Inventory.get(x -> x.getName().contains("Iron") || x.getName().contains("Bronze")),
                        () -> GameObjects.closest(x -> x.getName().contains("Crucible")))
                        .setDialogueOptions("All").setSleepTimeout(3600).setSimpleName(""),

                new TalkToFractal(talkedToKovacAboutMould::isNotComplete, NULL_AREA, () -> NPCs.closest("Kovac"))
                        .setSimpleName("Talk to Kovac to get mould"),
                // todo set mould?
                // here u should just do the minigame
                // let this branch do the mould
                new FoundryBranch(() -> PlayerSettings.getBitValue(13903) == 30).setSimpleName("Doing foundry"),
                new TalkToFractal(talkedToKovacAboutPouringMetal::isNotComplete, NULL_AREA, () -> NPCs.closest("Kovac"))
                        .setSimpleName("Talk to Kovac about pouring metal"),
                new FoundryBranch(() -> true).setSimpleName("Doing foundry")
        );
    }
}
