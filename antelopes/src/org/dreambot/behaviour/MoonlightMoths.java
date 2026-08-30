package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MoonlightMoths extends Fractal implements ItemContainerListener {

    private final Area MOTH_AREA = new Area(
            new Tile(1575, 9448, 0),
            new Tile(1567, 9444, 0),
            new Tile(1564, 9440, 0),
            new Tile(1551, 9441, 0),
            new Tile(1546, 9443, 0),
            new Tile(1543, 9438, 0),
            new Tile(1552, 9434, 0),
            new Tile(1566, 9434, 0),
            new Tile(1573, 9440, 0));

    private final Area HUNTER_BANK = new Tile(1544, 3041).getArea(4);

    private final Area HUNTER_SHOP = new Tile(1561, 3060).getArea(3);

    public static int earned = 0;

    public MoonlightMoths(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.BUTTERFLY_NET);

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COINS_995, 10_000)
                .addItem(ItemVariants.STAMINA_POTION, 1, 4)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.COINS_995))
                .setStrictSupplier(() -> !Inventory.contains(ItemID.COINS_995));

//        WebFinder webFinder = WebFinder.getWebFinder();
//
//        webFinder.createAndAddNode(new Tile(1575, 3049));
//        webFinder.createAndAddNode(new Tile(1568, 3047));
//        webFinder.createAndAddNode(new Tile(1565, 3053));
//        webFinder.createAndAddNode(new Tile(1561, 3059)); // store
//
//        webFinder.createAndAddNode(new Tile(1559, 3046));
//        webFinder.createAndAddNode(new Tile(1555, 3040));
//        webFinder.createAndAddNode(new Tile(1548, 3040));
//        webFinder.createAndAddNode(new Tile(1544, 3041)); // bank
//
//        // entrace nodes
//        EntranceWebNode guildEntrance = new EntranceWebNode(1556, 3048, 0, "Stairs", "Climb-down");
//        EntranceWebNode guildExit = new EntranceWebNode(1557, 9448, 0, "Stairs", "Climb-up");
//
//        guildExit.addDualConnections(guildEntrance);
//
//        webFinder.getNearest(guildEntrance).addDualConnections(guildEntrance);
//
//
//        BasicWebNode afterExit = new BasicWebNode(1558, 9455, 0);
//        afterExit.addDualConnections(guildExit);
//        webFinder.addWebNode(afterExit);
//
////        Logger.info("Test " + test);
//        webFinder.createAndAddNode(new Tile(1554, 9456));
//        webFinder.createAndAddNode(new Tile(1547, 9456));
//        webFinder.createAndAddNode(new Tile(1544, 9450));
//        webFinder.createAndAddNode(new Tile(1546, 9441));
//        webFinder.createAndAddNode(new Tile(1553, 9436));
//        webFinder.createAndAddNode(new Tile(1562, 9437));
//
//        // antelope
//        webFinder.createAndAddNode(new Tile(1561, 9429));
//        webFinder.createAndAddNode(new Tile(1560, 9422));

        Client.getInstance().addEventListener(this);
        Walking.setRunThreshold(30);
    }

    @Override
    public int onLoop() {
        if (Walking.getRunEnergy() < 10) {
            Item stam = ItemVariants.STAMINA_POTION.getItem();
            if (stam != null) {
                log("Drink stam");
                stam.interact();
            }
        }
        if (Walking.getRunEnergy() > 30 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        if (!Inventory.contains(ItemID.BUTTERFLY_JAR)) {
            if (!Inventory.contains(ItemID.MOONLIGHT_MOTH_JARRED) && Bank.count(ItemID.BUTTERFLY_JAR) < 27) {
                if (Shop.isOpen()) {
                    Logger.info("buy 50 jars");
                    Shop.purchase(ItemID.BUTTERFLY_JAR, 50);
                    return ReactionGenerator.getNormal();
                }

                NPC imia = NPCs.closest("Imia");
                Logger.info("trade imia");
                if (imia != null && imia.interact("Trade")) {
                    Sleep.sleepUntil(Shop::isOpen, 2400);
                    return ReactionGenerator.getNormal();
                }

                if (!HUNTER_SHOP.contains(Players.getLocal())) {
                    Logger.info("Going to hunter shop");
                    if (Walking.shouldWalk()) Walking.walk(HUNTER_SHOP);
                }
                return ReactionGenerator.getNormal();
            }

            // deposit moonlight moths
            if (!HUNTER_BANK.contains(Players.getLocal())) {
                // walk to bank area first because its not a bank location
                Logger.info("Going to hunter bank");
                if (Walking.shouldWalk()) Walking.walk(HUNTER_BANK);
                return ReactionGenerator.getNormal();
            }

            if (Bank.open()) {
                Logger.info("deposit all");
                Bank.depositAllExcept(x -> x.getID() == ItemID.COINS_995);
            }

            // get jars from bank
            if (Bank.isOpen() && Bank.count(ItemID.BUTTERFLY_JAR) >= 27) {
                Logger.info("withdrawing all jars");
                Bank.withdrawAll(ItemID.BUTTERFLY_JAR);
                return ReactionGenerator.getNormal();
            }
            return ReactionGenerator.getNormal();
        }

        if (Bank.isOpen()) {
            Logger.info("closing bank");
            Widgets.closeAll();
        }

        // catch moths
        if (!MOTH_AREA.contains(Players.getLocal())) {
            Logger.info("Walking to moth");
            if (Walking.shouldWalk()) Walking.walk(MOTH_AREA);
            return ReactionGenerator.getNormal();
        }

        NPC moth = NPCs.closest(x -> x.getName().equals("Moonlight moth") && x.canReach());
        Logger.info("Catching " + moth);
        if (moth != null && moth.interact("Catch")) {
            Sleep.sleepUntil(() -> !moth.exists(), 5400);
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        boolean isMoth = incoming.getID() == ItemID.MOONLIGHT_MOTH_JARRED;
        boolean wasJar = existing.getID() == ItemID.BUTTERFLY_JAR;
        Logger.info("is moth " + isMoth + " was jar " + wasJar);
        if (isMoth && wasJar) earned += LivePrices.get(ItemID.MOONLIGHT_MOTH_JARRED);
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        boolean isMoth = incoming.getID() == ItemID.MOONLIGHT_MOTH_JARRED;
        boolean wasJar = outgoing.getID() == ItemID.BUTTERFLY_JAR;
        Logger.info("is moth " + isMoth + " was jar " + wasJar);
        if (isMoth && wasJar) earned += LivePrices.get(ItemID.MOONLIGHT_MOTH_JARRED);
    }
}
