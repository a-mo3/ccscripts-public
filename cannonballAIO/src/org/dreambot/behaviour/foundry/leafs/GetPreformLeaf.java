package org.dreambot.behaviour.foundry.leafs;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.foundry.data.FoundryLoadouts;
import org.dreambot.behaviour.foundry.data.MouldHelper;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetPreformLeaf extends Fractal {
    private static final Tile START_TILE = new Tile(3362, 3148);
    private static final Area START_AREA = START_TILE.getArea(4);
    private static final int SWORD_TYPE_ONE = 13907;
    Supplier<Integer> tab = () -> PlayerSettings.getBitValue(13909); // 0 forte, 1 blades, 2 tips
    Supplier<Boolean> noForteSelected = () -> PlayerSettings.getBitValue(13910) == 0;
    Supplier<Boolean> noBladeSelected = () -> PlayerSettings.getBitValue(13911) == 0;
    Supplier<Boolean> noTipSelected = () -> PlayerSettings.getBitValue(13912) == 0;
    // todo there is probably a better solution to know if the mould is set
    Supplier<Boolean> tipsSelected = () -> !noBladeSelected.get() && !noTipSelected.get() && !noForteSelected.get();
    private static final int REPUTATION = 3436;

    public GetPreformLeaf() {
        this.paintArraySupplier = () -> new String[]{
                "Forte selected " + !noForteSelected.get(),
                "Blade selected " + !noBladeSelected.get(),
                "Tip selected " + !noTipSelected.get(),
                "Tab " + tab.get(),
                "Rep " + PlayerSettings.getConfig(REPUTATION) + "/2000"
        };
    }

    @Override
    public boolean isValid() {
        return !Equipment.contains("Preform");
    }

    @Override
    public int onLoop() {
        setStatus("Getting preform");
        NPC kovac = NPCs.closest("Kovac");

        // ONCE YOU HAVE OVER 2K REP AND NO TASK, BUY THE DOUBLE AMMO MOULD
        if (PlayerSettings.getConfig(REPUTATION) >= 2000) {
            // todo getText was getRealName
            if (Inventory.isFull()) {
                new BankAllInventoryEvent().execute();
            }

            WidgetChild doubleCBallsMould = Widgets.get(x -> x.getText().equals("Double ammo mould"));
            if (doubleCBallsMould == null || !doubleCBallsMould.isVisible()) {
                if (kovac != null && kovac.interact("Shop")) {
                    Sleep.sleep(600, 1800);
                }
                return ReactionGenerator.getNormal();
            }
            if (doubleCBallsMould.interact("Buy 1")) {
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.DOUBLE_AMMO_MOULD), 5000);
            }
            return ReactionGenerator.getNormal();
        }
        if (kovac == null) {
            Logger.info(START_AREA.contains(Players.getLocal()) + "");
            if (START_AREA.contains(Players.getLocal())) {
                GameObject enterance = GameObjects.closest("Cave");
                if (enterance != null && enterance.interact("Enter")) {
                    return 3000;
                }
            }
            if (Walking.shouldWalk(6)) Walking.walk(START_TILE); // TODO ADD EATING CONDITION ON THIS
            return 600;
        }


        // if this is 0 you have no blade type and probably need to get a commission
        if (PlayerSettings.getBitValue(SWORD_TYPE_ONE) == 0) {
            // todo talk to kovac and get commisson
            if (Dialogues.inDialogue()) {
                Dialog.solve("Yes.", "commission");
                return ReactionGenerator.getNormal();
            }

            kovac = NPCs.closest("Kovac");
            if (kovac != null && kovac.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }
        // SET MOULD
        Widget mould = Widgets.getWidget(718);
        if (!tipsSelected.get() && (mould == null || !mould.isVisible())) {
            // todo open tab here
            GameObject mouldJig = GameObjects.closest(x -> x.getName().contains("Mould jig"));
            if (mouldJig != null && mouldJig.interact("Setup")) {
                Sleep.sleepUntil(() -> Widgets.getWidget(718) != null, 5000);
            }
            return ReactionGenerator.getNormal();
        }

        if (noForteSelected.get()) {
            if (tab.get() != 0) {
                WidgetChild tabWidget = Widgets.get(x -> x.getText().contains("Forte"));
                if (tabWidget != null && tabWidget.interact("View")) {
                    Sleep.sleepUntil(() -> tab.get() == 0, 5000);
                }
                return ReactionGenerator.getNormal();
            }
            MouldHelper.selectBest();
            return ReactionGenerator.getNormal();
        }

        if (noBladeSelected.get()) {
            if (tab.get() != 1) {
                WidgetChild tabWidget = Widgets.get(x -> x.getText().contains("Blades"));
                if (tabWidget != null && tabWidget.interact("View")) {
                    Sleep.sleepUntil(() -> tab.get() == 0, 5000);
                }
                return ReactionGenerator.getNormal();
            }
            MouldHelper.selectBest();
            return ReactionGenerator.getNormal();
        }

        if (noTipSelected.get()) {
            if (tab.get() != 2) {
                WidgetChild tabWidget = Widgets.get(x -> x.getText().contains("Tips"));
                if (tabWidget != null && tabWidget.interact("View")) {
                    Sleep.sleepUntil(() -> tab.get() == 2, 5000);
                }
                return ReactionGenerator.getNormal();
            }
            MouldHelper.selectBest();
            return ReactionGenerator.getNormal();
        }

        // mould is configured, set the mould!
        WidgetChild setMouldButton = Widgets.get(x -> x.hasAction("Set") && x.getText().equals("Mould"));
        if (setMouldButton != null && setMouldButton.isVisible()) {
            setMouldButton.interact("Set");
            return 600;
        }

        // FILL CRUCIBLE
        // todo maybe just replace these with the iron and steel varbits,
        // steel : 13933 needs to be 20
        // iron : needs to be 8
        Logger.info("getting crucible");
        // need to check for this or else it just starts trying to fill the shit up
        GameObject fullMouldJig = GameObjects.closest("Mould jig (Poured metal)");
        if (fullMouldJig != null) {
            Logger.info("mould jig is full");
            if (!Inventory.contains(ItemID.BUCKET_OF_WATER)) {
                Item bucket = Inventory.get(ItemID.BUCKET);
                if (bucket == null) {
                    // todo perhaps buy remainder here
                    Logger.info("bucket " + new WithdrawLoadoutEvent(FoundryLoadouts.BUCKET, null)
                            .executed());
                    // todo get bucket without normal loadouts, normal loadouts goes to GE
//                    LoadoutExecutor.setBuyRemainder(false);
//                    LoadoutExecutor.execInvLoadout(FoundryLoadouts.BUCKET);
//                    LoadoutExecutor.setBuyRemainder(true);
                    return ReactionGenerator.getNormal();
                }
                GameObject waterfall = GameObjects.closest("Waterfall");
                if (waterfall != null) {
                    if (Bank.isOpen()) Bank.close();
                    bucket.useOn(waterfall);
                    Sleep.sleepUntil(() -> Inventory.contains(ItemID.BUCKET_OF_WATER), 8000);
                }
                return ReactionGenerator.getNormal();
            }
            Item waterBucket = Inventory.get(ItemID.BUCKET_OF_WATER);
            if (waterBucket != null) {
                waterBucket.useOn(fullMouldJig);
                return 3400;
            }
            return ReactionGenerator.getNormal();
        }

        // todo replace steel and iron platebodies with bars
        GameObject cooledMould = GameObjects.closest("Mould jig (Cooled metal)");
        if (cooledMould != null && cooledMould.interact("Pick-up")) {
            Sleep.sleepUntil(() -> Equipment.contains(x -> x.getName().equals("Preform")), 5000);
            return ReactionGenerator.getNormal();
        }

        GameObject emptyCrucible = GameObjects.closest("Crucible (empty)");
        if (emptyCrucible != null) {
            Logger.info("getting loadout");
            if (!FoundryLoadouts.FOUNDRY_BARS.isFulfilled()) {
                // todo get bars without using normal loadouts, they will go to ge when you shouldnt
//                LoadoutExecutor.execInvLoadout(FoundryLoadouts.FOUNDRY_BARS);
                Logger.info("Bars " + new WithdrawLoadoutEvent(FoundryLoadouts.FOUNDRY_BARS, null).executed());
                return ReactionGenerator.getNormal();
            }
            // use 5 steel platebodies then 2 iron platebodies
            Item steelBars = Inventory.get(ItemID.STEEL_BAR);
            if (steelBars != null && emptyCrucible.interact("Fill")) {
                Sleep.sleepUntil(ItemProcessing::isOpen, 5000);
                if (ItemProcessing.isOpen()) {
                    ItemProcessing.makeAll(ItemID.STEEL_BAR);
                    Sleep.sleepUntil(() -> !Inventory.contains(ItemID.STEEL_BAR), 5000);
                }
            }
            return ReactionGenerator.getNormal();
        }

        GameObject partiallyFullCrucible = GameObjects.closest("Crucible (partially full)");
        if (partiallyFullCrucible != null) {
            Item ironBar = Inventory.get(ItemID.IRON_BAR);
            if (ironBar != null && partiallyFullCrucible.interact("Fill")) {
                Sleep.sleepUntil(ItemProcessing::isOpen, 5000);
                if (ItemProcessing.isOpen()) {
                    ItemProcessing.makeAll(ItemID.IRON_BAR);
                    Sleep.sleepUntil(() -> !Inventory.contains(ItemID.IRON_BAR), 5000);
                }
            }
            return ReactionGenerator.getNormal();
        }

        GameObject fullCrucible = GameObjects.closest("Crucible (full)");
        if (fullCrucible != null && fullCrucible.interact("Pour")) {
            return 3400;
        }

        return ReactionGenerator.getNormal();
    }
}
