package org.dreambot.behaviour.dragons;

import org.dreambot.BrutalBlues;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.SmartLootEvent;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class KillBrutalReds extends Fractal implements ItemContainerListener {
    Tile safeSpot = new Tile(1624, 10073);

    public KillBrutalReds(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        Client.getInstance().addEventListener(this);
    }

    final Area RED_DRAGON_AREA = new Area(1607, 10083, 1622, 10069);
    final Area ALL_DRAGONS = new Area(1607, 10108, 1636, 10069);
    //    public static final Timer lastDragonAtk = new Timer(500);
    boolean getRedAgro = false;

    @Override
    public int onLoop() {
        if (Bank.isOpen()) Bank.close();

        // todo decant after looting
        Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
        if (Skills.getBoostedLevel(Skill.PRAYER) < 5) {
            prayerPot.interact("Drink");
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) > 5, 2400);
        }

        if (safeSpot.distance() < 10 || ALL_DRAGONS.contains(Players.getLocal())) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
        }

        if (Combat.getHealthPercent() < 100 && Skills.getBoostedLevel(Skill.HITPOINTS) < ScriptSettings.getSettingsData().eatAbove) {
            Inventory.interact(Restock.FOOD);
            return ReactionGenerator.getQuick();
        }

        Character interacting = Players.getLocal().getInteractingCharacter();
        if (interacting != null && interacting.getName().toLowerCase().contains("red")) {
            getRedAgro = false;
        }

//        if (getRedAgro ||
//                (interacting != null && interacting.getName().toLowerCase().contains("red"))) {
//            getRedAgro = true;
//            if (Walking.shouldWalk()) Walking.walk(RED_DRAGON_AREA);
//            return ReactionGenerator.getQuick();
//        }

        if (!safeSpot.equals(Players.getLocal().getTile())) {
            if (Walking.shouldWalk(8)) Walking.walkExact(safeSpot);
            return ReactionGenerator.getQuick();
        }

        if (!Combat.isAutoRetaliateOn()) {
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getQuick();
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
            return ReactionGenerator.getQuick();
        }

        Item antiFire = ItemVariants.ANTI_FIRE_POTION.getItem();
        if (antiFire != null && PlayerSettings.getBitValue(3981) < 3) {
            Logger.info("Drinking antifire");
            antiFire.interact("Drink");
            return ReactionGenerator.getQuick();
        }

        Item rangePot = ItemVariants.RANGE_POTION.getItem();
        if (getBoost(Skill.RANGED) < ScriptSettings.getSettingsData().minBoost && rangePot != null) {
            rangePot.interact();
            Sleep.sleepUntil(() -> getBoost(Skill.RANGED) > ScriptSettings.getSettingsData().minBoost, 2400);
        }

        // todo high alch

        if (GroundItems.closest(x -> RED_DRAGON_AREA.contains(x) && x.getItem().getLivePrice() > 300) != null) {
            Logger.info("Loot event: " + new SmartLootEvent(() -> GroundItems.all(x -> RED_DRAGON_AREA.contains(x) && x.getItem().getLivePrice() > 300),
                    ItemID.VIAL, ItemID.JUG, ItemID.JUG_OF_WINE
            ).executed());
            RechargePrayer.shouldRecharge = true;
            return ReactionGenerator.getQuick();
        }

        if (Players.getLocal().isInCombat()) {
            return ReactionGenerator.getQuick();
        }

        NPC dragon = NPCs.closest(x -> x.getName().equals("Brutal red dragon") && !x.isInCombat());
        if (dragon != null && dragon.interact()) {
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
        }
        return ReactionGenerator.getQuick();
    }

    private int getBoost(Skill skill) {
        return Skills.getBoostedLevel(skill) - Skills.getRealLevel(skill);
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (RED_DRAGON_AREA.contains(Players.getLocal())) {
            Logger.info("+ " + item.getName() + " " + item.getLivePrice());
            BrutalBlues.grossGp += item.getLivePrice();
        }
    }
}
