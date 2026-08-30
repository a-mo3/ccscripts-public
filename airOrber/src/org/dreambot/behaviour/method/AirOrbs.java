package org.dreambot.behaviour.method;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.MuleOff;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.SellAllEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class AirOrbs extends Fractal implements ItemContainerListener {
    final Area AIR_OBELISK = new Area(3082, 3575, 3092, 3565);
    final Area AIR_LADDER = new Area(3086, 9973, 3090, 9969);
    final Timer runtime = new Timer();
    int orbsMade = 0;
    DecimalFormat df = new DecimalFormat("###,###,###");

    public AirOrbs(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
        this.appendLogic = () -> {
            InventoryLoadoutItem missingItem = this.inventoryLoadout.getMissingItem();
            if (missingItem != null) {
                if (!OwnedItems.contains(missingItem.getItemId())) {
                    if (missingItem.getVariant() != null) {
                        if (OwnedItems.contains(missingItem.getVariant())) return false;
                    }
                    int cost = missingItem.getBuyPrice() * missingItem.getRefill();
                    int owned = OwnedItems.count(ItemID.COINS_995);
                    Logger.format("Cost: %d OwnedCoins: %d ", cost, owned);
                    if (cost > owned) {
                        // you cant afford the restock
                        Logger.info("Cant afford restock, selling all items");
                        new SellAllEvent(MuleOff.itemsToMule).execute();
                        if (owned <= OwnedItems.count(ItemID.COINS_995)) {
                            Logger.info("no coins gained from sell event, mule requesting cost * 1.1");
                            new MuleRequestEvent(ScriptManager.getScriptManager().getCurrentScript().getSDNName())
                                    .addRequiredItem(ItemID.COINS_995, (int) (cost * 1.1))
                                    .execute();
                        }
                        return true;
                    }
                }
            }

            if (Inventory.contains(ItemID.AIR_ORB) && !Inventory.contains(ItemID.UNPOWERED_ORB)) {
                if (!Bank.isOpen()) {
                    if (Walking.shouldWalk()) Bank.open(BankLocation.EDGEVILLE);
                    return true;
                }

                Bank.depositAllItems();
                return true;
            }
            return false;
        };

        this.paintArraySupplier = () -> new String[]{
                String.format("Orbs made: %s (%s)", df.format(orbsMade),
                        df.format(runtime.getHourlyRate(orbsMade))),
                String.format("Gp: %s (%s)", df.format((long) orbsMade * LivePrices.get(ItemID.AIR_ORB)),
                        df.format(runtime.getHourlyRate(orbsMade * LivePrices.get(ItemID.AIR_ORB)))),
                String.format("Time remaining until mule off: %s", formatTime(MuleOff.timer.remaining()))
        };

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(8)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.UNPOWERED_ORB, 1, 26 - (ScriptSettings.getEnergyPotions() + ScriptSettings.getSalmons())).setRefill(1200)
                .addItem(ItemID.COSMIC_RUNE, 3, 75).setRefill(2000)
                .setMuleRequestAmount(100_000)
                .setSellItems(MuleOff.itemsToMule);

        if (ScriptSettings.getEnergyPotions() > 0) {
            this.inventoryLoadout.addItem(ItemVariants.ENERGY_POTION, 1, ScriptSettings.getEnergyPotions())
                    .setEnabledCondition(() -> Players.getLocal().getY() < 4000 && !Combat.isInWild()) // only if u havent started run
                    .setRefill(100);
        }

        if (ScriptSettings.getSalmons() > 0) {
            this.inventoryLoadout.addItem(ScriptSettings.useSharks() ? ItemID.SHARK : ItemID.SALMON, 1, ScriptSettings.getSalmons())
                    .setEnabledCondition(() -> Players.getLocal().getY() < 4000 && !Combat.isInWild()) // only if u havent started run
                    .setRefill(500);
        }
    }

    Timer eatTimer = new Timer(1800);
    Timer energyTimer = new Timer(1800);
    Timer runEnable = new Timer(800);

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.COINS_995)) {
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.depositAll(ItemID.COINS_995);
            return ReactionGenerator.getNormal();
        }

        if (eatTimer.finished() && Skills.getBoostedLevel(Skill.HITPOINTS) < 17 && !Bank.isOpen()) {
            if (Inventory.interact(x -> x.getID() == ItemID.SHARK || x.getID() == ItemID.SALMON, "Eat"))
                eatTimer.reset();
        }

        if (energyTimer.finished() && Walking.getRunEnergy() < 30) {
            if (ItemVariants.ENERGY_POTION.interact("Drink")) energyTimer.reset();
        }

        if ((Walking.getRunEnergy() >= 5 && runEnable.finished()) || Players.getLocal().isInCombat()) {
            if (!Walking.isRunEnabled()) {
                Walking.toggleRun();
                runEnable.reset();
            }
        }

        if (!AIR_OBELISK.contains(Players.getLocal())) {
            if (!AIR_LADDER.contains(Players.getLocal())) {
                if (Walking.shouldWalk(6)) Walking.walk(AIR_LADDER);
                return ReactionGenerator.getNormal();
            }

            GameObject ladder = GameObjects.closest("Ladder");
            if (ladder != null) {
                ladder.interact("Climb-up");
            }
            return ReactionGenerator.getNormal();
        }

        if (ItemProcessing.isOpen()) {
            ItemProcessing.makeAll(ItemID.AIR_ORB);
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.UNPOWERED_ORB),
                    () -> Players.getLocal().isAnimating(),
                    1800,
                    100
            );
            return ReactionGenerator.getNormal();
        }

        GameObject obelisk = GameObjects.closest("Obelisk of Air");
        if (obelisk != null) {
            Magic.castSpellOn(Normal.CHARGE_AIR_ORB, obelisk);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        Logger.info("added " + item.getName());
        if (!Combat.isInWild()) return;
        if (item.getID() == ItemID.AIR_ORB) orbsMade++;
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (!Combat.isInWild()) return;
        if (incoming.getID() == ItemID.AIR_ORB) orbsMade++;
    }

    @Override
    public void onInventoryItemSwapped(Item incoming, Item outgoing) {
        if (!Combat.isInWild()) return;
        if (incoming.getID() == ItemID.AIR_ORB) orbsMade++;
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
}
