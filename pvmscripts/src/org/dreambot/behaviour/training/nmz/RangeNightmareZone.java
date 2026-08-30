package org.dreambot.behaviour.training.nmz;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * does the actual NMZ, getting potions, getting into the instance, manage absorbs
 */
public class RangeNightmareZone extends Fractal {
    public RangeNightmareZone(Supplier<Boolean> acceptCondition, EquipmentLoadout loadout) {
        super(acceptCondition);
        this.equipmentLoadout = loadout;
        setSimpleName("Range nmz");
    }

    private final String DOMINIC = "Dominic Onion";
    Area NMZ_ENTRANCE = new Area(2600, 3119, 2610, 3112);
    Timer tabTimer = new Timer(5 * 60 * 1000);
    @Getter
    @Setter
    public Supplier<CombatStyle> styleSupplier = () -> {
        int atk = Skills.getRealLevel(Skill.ATTACK);
        int str = Skills.getRealLevel(Skill.STRENGTH);
        int def = Skills.getRealLevel(Skill.DEFENCE);
        if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
        if (atk <= def) return CombatStyle.ATTACK;
        return CombatStyle.DEFENCE;
    };

    @Override
    public int onLoop() {
        if (Client.isDynamicRegion()) {
            slowLog("Doing NMZ");
            if (!Combat.isAutoRetaliateOn()) {
                log("Turn on auto realiate");
                Combat.toggleAutoRetaliate(true);
                return ReactionGenerator.getNormal();
            }

            if (tabTimer.finished()) {
                log("Tab thing");
                Tabs.open(Tab.values()[Calculations.random(0, 4)]);
                tabTimer.reset();
            }

            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayerPot != null && !Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) {
                log("Toggle prot melee nmz");
                Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            }

            if (prayerPot != null && Skills.getBoostedLevel(Skill.PRAYER) < 5) {
                log("Drink prayer");
                prayerPot.interact("Drink");
                return ReactionGenerator.getNormal();
            }

            Item rangePotion = ItemVariants.RANGE_POTION.getItem();
            if (rangePotion != null && Skills.getBoostedLevel(Skill.RANGED) <= Skills.getRealLevel(Skill.RANGED)) {
                log("Boost range");
                rangePotion.interact("Drink");
                return ReactionGenerator.getNormal();
            }

            if (activeAbsorption() < 50 && ItemVariants.NMZ_ABSORB_POTION.getInventoryCount() > 0) {
                log("Mass sip absorbs");
                // we can interact with them all quickly to sip mad many ya her
                Inventory.all(x -> ItemVariants.NMZ_ABSORB_POTION.contains(x.getId()))
                        .forEach(Inventory::interact);
                return ReactionGenerator.getNormal();
            }

            int rangeBoost = Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel();
            if (rangeBoost < 4) {
                Item rangePot = ItemVariants.NMZ_RANGING_POTION.getItem();
                if (rangePot != null) {
                    log("Drink nmz range");
                    rangePot.interact();
                }
            }

            if (Skills.getBoostedLevel(Skill.HITPOINTS) > 1) {
                log("Guzzle " + activeAbsorption() + " over: " + activeRangeBoost());
                Inventory.interact(ItemID.DWARVEN_ROCK_CAKE_7510, "Guzzle");
            }

            if (Combat.getCombatStyle() != styleSupplier.get()) {
                log("Switch style");
                Combat.setCombatStyle(styleSupplier.get());
            }

            return ReactionGenerator.getNormal();
        }

//        if (!Bank.isCached()) {
//            log("Get bank cache");
//            if (Bank.isOpen()) Bank.updateCache();
//            if (Walking.shouldWalk()) Bank.open();
//            return ReactionGenerator.getNormal();
//        }

        int totalPoints = getNMZPoints();

        boolean useAbsorbs = false;
        int absorbs = storedAbsorbCount() + ownedDoses("Absorption");
        if (absorbs < ABSORB_DOSE_TARGET) {
            log("Does not own enough absorb doses");
            int requiredPoints = (ABSORB_DOSE_TARGET - absorbs) * ABSORB_DOSE_COST;
            useAbsorbs = requiredPoints < totalPoints;
            log("Requires absorb points:  " + requiredPoints + " Can afford? " + useAbsorbs + " " + getNMZPoints());

            // if we have used points to decide to buy this take that from the total points counter
            if (useAbsorbs) totalPoints -= requiredPoints;
        } else {
            useAbsorbs = true;
        }


        boolean useRangePots = false;
        int ranging = storedRangedCount() + ownedDoses("Super ranging");
        if (ranging < RANGING_DOSE_TARGET) {
            log("Does not own enough ranged doses");
            int requiredPoints = (RANGING_DOSE_TARGET - ranging) * RANGING_DOSE_COST;
            useRangePots = requiredPoints < totalPoints;
            log("Requires ranging " + requiredPoints + " Can afford? " + useRangePots);

            // if we have used points to decide to buy this take that from the total points counter
            if (useRangePots) totalPoints -= requiredPoints;
        } else {
            useRangePots = true;
        }

        if (getGPInCoffer() < 30_000) {
            log("More GP in coffer " + getGPInCoffer());
            if (!NMZ_ENTRANCE.contains(Players.getLocal())) {
                log("Walk to NMZ");
                if (Walking.shouldWalk()) Walking.walk(NMZ_ENTRANCE);
                return ReactionGenerator.getNormal();
            }

            if (Inventory.count(ItemID.COINS_995) < 30_000) {
                log("Get coins for coffer");
                log("Result: " + new WithdrawLoadoutEvent(new InventoryLoadout().addItem(ItemID.COINS_995, 60_000), this.equipmentLoadout)
                        .executed());
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.canEnterInput()) {
                log("enter 120k");
                Keyboard.type("120k", true);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("Deposit", "");
                return ReactionGenerator.getNormal();
            }

            GameObject coffer = GameObjects.closest("Dominic's coffer");
            if (coffer != null) {
                coffer.interact("Use");
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
                return ReactionGenerator.getNormal();
            }
            log("no coffer found");
            return ReactionGenerator.getNormal();
        }

        // if we cant afford one of the absorbs or overloads just do a prayer | combats run
        if (!useRangePots || !useAbsorbs) {
            InventoryLoadout potsLoadout = new InventoryLoadout()
                    .addItem(ItemID.PRAYER_POTION4, 14)
                    .addItem(ItemID.RANGING_POTION4, 8);
            if (!potsLoadout.isFulfilled()) {
                log("Grab pots");
                new WithdrawLoadoutEvent(potsLoadout, this.equipmentLoadout)
                        .executed();
                return ReactionGenerator.getNormal();
            }
            return getIntoNMZ();
        }

        if (ItemVariants.RANGING_POTION.getItem() != null || ItemVariants.PRAYER_POTION.getItem() != null) {
            log("Bank potions");
            new BankAllInventoryEvent().execute();
            return ReactionGenerator.getNormal();
        }

        if (dosesInInventory("Super ranging") < RANGING_DOSE_TARGET) {
            log("Get more ranging potions");
            if (ItemVariants.NMZ_RANGING_POTION.getInventoryCount() < RANGING_DOSE_TARGET / 4 && Bank.contains(x -> ItemVariants.NMZ_RANGING_POTION.contains(x.getId()))) {
                log("Withdraw banked ranged");
                if (!Bank.isOpen()) {
                    log("Open bank");
                    if (Walking.shouldWalk()) Bank.open();
                    return ReactionGenerator.getNormal();
                }

                Bank.withdraw(x -> ItemVariants.NMZ_RANGING_POTION.contains(x.getId()), 8 - ItemVariants.NMZ_RANGING_POTION.getInventoryCount());
                return ReactionGenerator.getNormal();
            }
            // purchase doses if required
            if (storedRangedCount() < (RANGING_DOSE_TARGET - dosesInInventory("Super ranging"))) {
                log("Needs to buy ranged potion");
                buyDoses("Super ranging");
                return ReactionGenerator.getNormal();
            }
            // withdraw from barrel
            if (Dialogues.canEnterInput()) {
                int doses = RANGING_DOSE_TARGET - dosesInInventory("Super ranging");
                log("Withdrawing potion doses");
                Keyboard.type(doses, true);
                return ReactionGenerator.getNormal();
            }

            if (!NMZ_ENTRANCE.contains(Players.getLocal())) {
                log("Walk to nmz");
                if (Walking.shouldWalk()) Walking.walk(NMZ_ENTRANCE);
                return ReactionGenerator.getNormal();
            }

            GameObject rangingBarrel = GameObjects.closest("Super ranging potion");
            if (rangingBarrel == null) {
                log("Cant find super ranging barrel");
                return ReactionGenerator.getNormal();
            }
            rangingBarrel.interact("Take");
            Sleep.sleepUntil(Dialogues::canEnterInput, 2400);
            return ReactionGenerator.getNormal();
        }

        // get out absorbs
        if (dosesInInventory("Absorption") < ABSORB_DOSE_TARGET) {
            log("Get more absorbs");
            if (ItemVariants.NMZ_ABSORB_POTION.getInventoryCount() < ABSORB_DOSE_TARGET / 4 && Bank.contains(x -> ItemVariants.NMZ_ABSORB_POTION.contains(x.getId()))) {
                log("Withdraw banked absorbs");
                if (!Bank.isOpen()) {
                    log("Open bank");
                    if (Walking.shouldWalk()) Bank.open();
                    return ReactionGenerator.getNormal();
                }

                Bank.withdraw(x -> ItemVariants.NMZ_ABSORB_POTION.contains(x.getId()), (ABSORB_DOSE_TARGET / 4) - ItemVariants.NMZ_ABSORB_POTION.getInventoryCount());
                return ReactionGenerator.getNormal();
            }
            // purchase doses if required
            if (storedAbsorbCount() < (ABSORB_DOSE_TARGET - dosesInInventory("Absorption"))) {
                log("Needs to buy absorb potion");
                buyDoses("Absorption");
                return ReactionGenerator.getNormal();
            }
            // withdraw from barrel
            if (Dialogues.canEnterInput()) {
                int doses = ABSORB_DOSE_TARGET - dosesInInventory("Absorption");
                log("Withdrawing potion doses");
                Keyboard.type(doses, true);
                return ReactionGenerator.getNormal();
            }

            if (!NMZ_ENTRANCE.contains(Players.getLocal())) {
                log("Walk to nmz");
                if (Walking.shouldWalk()) Walking.walk(NMZ_ENTRANCE);
                return ReactionGenerator.getNormal();
            }

            GameObject absorptionbarrel = GameObjects.closest("Absorption potion");
            if (absorptionbarrel == null) {
                log("Cant find absorb barrel");
                return ReactionGenerator.getNormal();
            }
            absorptionbarrel.interact("Take");
            Sleep.sleepUntil(Dialogues::canEnterInput, 2400);
            return ReactionGenerator.getNormal();
        }

        if (!Inventory.contains(ItemID.DWARVEN_ROCK_CAKE_7510)) {
            log("Withdraw rock cake");
            if (Inventory.isFull()) {
                log("Make space for rock cake");
                new BankAllInventoryEvent().execute();
                return ReactionGenerator.getNormal();
            }
            if (!Bank.isOpen()) {
                if (Walking.shouldWalk()) Bank.open();
                return ReactionGenerator.getNormal();
            }

            Bank.withdraw(ItemID.DWARVEN_ROCK_CAKE_7510);
            return ReactionGenerator.getNormal();
        }

        return getIntoNMZ();
    }

    /**
     * go to, open, switch tab, buy 10 of the potion in question
     *
     * @param potionName eg Overload
     * @return
     */
    private int buyDoses(String potionName) {
        if (Bank.isOpen() || GrandExchange.isOpen()) {
            log("buy dose, close bank/ge");
            Widgets.closeAll();
            return ReactionGenerator.getNormal();
        }

        Widget parent = Widgets.getWidget(206);
        if (parent == null || !parent.isVisible()) {
            log("Open rewards chest");
            if (!NMZ_ENTRANCE.contains(Players.getLocal())) {
                log("Walk to nmz");
                if (Walking.shouldWalk()) Walking.walk(NMZ_ENTRANCE);
                return ReactionGenerator.getNormal();
            }

            GameObject rewardChest = GameObjects.closest("Rewards chest");
            if (rewardChest == null) {
                log("Failed to find reward chest");
                return ReactionGenerator.getNormal();
            }

            log("Search reward chest");
            rewardChest.interact("Search");
            Sleep.sleepUntil(Widgets::isOpen, 4400);
            return ReactionGenerator.getNormal();
        }

        WidgetChild openBenefitTabButton = Widgets.get(x -> x.hasAction("Benefits"));
        if (openBenefitTabButton != null) {
            log("Open benefits");
            openBenefitTabButton.interact("Benefits");
            return ReactionGenerator.getNormal() + 1200;
        }

        // buy pots
        WidgetChild buyPotion = Widgets.get(x -> x.getName().contains(potionName) && x.hasAction("Buy-10"));
        if (buyPotion == null) {
            log("Failed to find buy potion");
            return ReactionGenerator.getNormal();
        }
        log("Buy 10");
        buyPotion.interact("Buy-10");
        return ReactionGenerator.getNormal();
    }

    /**
     * money will already be in coffer, talk to guy to get up rumble normal
     * interact with potion and enter dream
     *
     * @return sleep
     */
    private int getIntoNMZ() {
        if (!NMZ_ENTRANCE.contains(Players.getLocal())) {
            log("Walk to NMZ");
            if (Walking.shouldWalk()) Walking.walk(NMZ_ENTRANCE);
            return ReactionGenerator.getNormal();
        }

        if (!hasConfiguredRumble()) {
            // talk to Domi, choose rumble normal
            NPC dominic = NPCs.closest(DOMINIC);
            if (dominic == null) {
                log("Can't find Dominic");
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                log("Choose dream");
                Dialog.solve("choose a dream", "Yes", "Customisable Rumble (normal", "Rumble", "Customisable - normal");
                return ReactionGenerator.getNormal();
            }

            dominic.interact("Dream");
            return ReactionGenerator.getNormal();
        }

        WidgetChild acceptButton = Widgets.get(x -> x.getText().contains("Accept"));
        if (Widgets.isOpen() && acceptButton != null) {
            log("Accept dream");
            acceptButton.interact();
            return ReactionGenerator.getNormal();
        }

        GameObject potion = GameObjects.closest(26291);
        if (potion != null) {
            log("Drink dream potion");
            potion.interact("Drink");
            Sleep.sleepUntil(Widgets::isOpen, 4400);
        }
        log("Couldnt find potion");
        return ReactionGenerator.getNormal();
    }

    /**
     * @return owned does of a potion, in bank or in inventory
     */
    private int ownedDoses(String corePotionName) {
        int banked = Bank.all(x -> x.getName().contains("(") && x.getName().contains(corePotionName))
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getName().replaceAll("\\D", "")))
                .sum();

        int inv = Inventory.all(x -> x.getName().contains("(") && x.getName().contains(corePotionName))
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getName().replaceAll("\\D", "")))
                .sum();

        return inv + banked;
    }

    private int dosesInInventory(String corePotionName) {
        return Inventory.all(x -> x.getName().contains("(") && x.getName().contains(corePotionName))
                .stream()
                .mapToInt(x -> Integer.parseInt(x.getName().replaceAll("\\D", "")))
                .sum();
    }

    // 14 slots full of absorbs is the goal, 14 * 4 is 56 doses 56 * 50 = 2800 HP tanked, 2800 hits taken w/ rockcake
    private final int ABSORB_DOSE_TARGET = 72;
    // goal is 8 overloads, 8 * 4, 32 - 5 min * 32 = 160 minutes of overload time
    private final int RANGING_DOSE_TARGET = 32;

    private final int ABSORB_DOSE_COST = 1000;
    private final int RANGING_DOSE_COST = 250;

    private int storedAbsorbCount() {
        return PlayerSettings.getBitValue(3954);
    }

    private int storedRangedCount() {
        return PlayerSettings.getBitValue(3951);
    }

    private boolean hasConfiguredRumble() {
        // 61 rumble normal
        return PlayerSettings.getBitValue(3946) == 61;
    }

    private int getGPInCoffer() {
        return PlayerSettings.getBitValue(3948) * 1000;
    }

    public static int getNMZPoints() {
        return PlayerSettings.getConfig(1060);
    }

    private int activeAbsorption() {
        return PlayerSettings.getBitValue(3956);
    }

    private int activeRangeBoost() {
        return Skills.getBoostedLevel(Skill.RANGED) - Skills.getRealLevel(Skill.RANGED);
    }
}
