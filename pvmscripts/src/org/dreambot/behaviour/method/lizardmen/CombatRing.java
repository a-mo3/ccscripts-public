package org.dreambot.behaviour.method.lizardmen;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.misc.CombatLoadouts;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CombatRing extends Fractal {
    final int IN_FIGHT_VARBIT = 4904; // when in fight, value is the tier of soldier you are fighting
    final Area COMBAT_RING = new Area(1538, 3628, 1548, 3618);

    public CombatRing(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 5)
                .addItem(ItemVariants.PRAYER_POTION, 1, 10)
                .addItem(ItemVariants.SUPER_COMBAT_POTION, 1, 5)
                .addItem(ItemVariants.SKILLS_NECKLACE);
        this.equipmentLoadout = CombatLoadouts.SCIMITAR_LOADOUT_P2P;
        setSimpleName("Combat ring");
    }

    @Override
    public int onLoop() {
        if (!Bank.isCached()) new BankAllInventoryEvent().execute();
        if (!COMBAT_RING.contains(Players.getLocal())) {
            log("Go to combat ring");
            if (Walking.shouldWalk()) Walking.walk(COMBAT_RING);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.isFull()) new BankAllInventoryEvent().execute();
        if (Inventory.contains(ItemID.VIAL)) Inventory.dropAll(ItemID.VIAL);

        if (Skill.HITPOINTS.getBoostedLevel() < 20) {
            log("Eat");
            Inventory.interact(ItemID.SHARK);
        }

        GroundItem armour = GroundItems.closest(x -> x.getName().contains("Shayzien"));
        if (armour != null) {
            // take shayzien armour reward
            log("Take shayzien");
            armour.interact("Take");
            Sleep.sleepUntil(() -> !armour.exists(), 2400);
            return ReactionGenerator.getNormal();
        }

        if (!inFight()) {
            log("Get into combat ring fight");

            if (Dialogues.inDialogue()) {
                Dialog.solve("Sure, let's fight.",
                        "I reckon I can take you",
                        "Let's fight")
                ;
                return ReactionGenerator.getNormal();
            }

            NPC soldier = getSoldier();
            if (soldier != null && soldier.interact("Talk-to")) {
                log("Talk to soldier");
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
                return ReactionGenerator.getNormal();
            }

        } else {
            log("enable prot melee");
            if (Skills.getBoostedLevel(Skill.PRAYER) == 0) ItemVariants.PRAYER_POTION.interact("Drink");
            Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);


            NPC soldier = (NPC) HintArrow.getPointed();
            if (soldier != null && soldier.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().isInCombat(), 2400);
            }
        }


        return ReactionGenerator.getNormal();
    }

    private NPC getSoldier() {
        if (!hasTierOne.get()) return NPCs.closest("Soldier (tier 1)");
        if (!hasTierTwo.get()) return NPCs.closest("Soldier (tier 2)");
        if (!hasTierThree.get()) return NPCs.closest("Soldier (tier 3)");
        if (!hasTierFour.get()) return NPCs.closest("Soldier (tier 4)");
        return NPCs.closest("Soldier (tier 5)");
    }

    final Supplier<Boolean> hasTierOne = () -> OwnedItems.containsAll(
            ItemID.SHAYZIEN_BOOTS_1,
            ItemID.SHAYZIEN_GLOVES_1,
            ItemID.SHAYZIEN_PLATEBODY_1,
            ItemID.SHAYZIEN_GREAVES_1,
            ItemID.SHAYZIEN_HELM_1
    );

    final Supplier<Boolean> hasTierTwo = () -> OwnedItems.containsAll(
            ItemID.SHAYZIEN_BOOTS_2,
            ItemID.SHAYZIEN_GLOVES_2,
            ItemID.SHAYZIEN_PLATEBODY_2,
            ItemID.SHAYZIEN_GREAVES_2,
            ItemID.SHAYZIEN_HELM_2
    );

    final Supplier<Boolean> hasTierThree = () -> OwnedItems.containsAll(
            ItemID.SHAYZIEN_BOOTS_3,
            ItemID.SHAYZIEN_GLOVES_3,
            ItemID.SHAYZIEN_PLATEBODY_3,
            ItemID.SHAYZIEN_GREAVES_3,
            ItemID.SHAYZIEN_HELM_3
    );

    final Supplier<Boolean> hasTierFour = () -> OwnedItems.containsAll(
            ItemID.SHAYZIEN_BOOTS_4,
            ItemID.SHAYZIEN_GLOVES_4,
            ItemID.SHAYZIEN_PLATEBODY_4,
            ItemID.SHAYZIEN_GREAVES_4,
            ItemID.SHAYZIEN_HELM_4
    );

    public static final Supplier<Boolean> hasTierFive = () -> OwnedItems.containsAll(
            ItemID.SHAYZIEN_BOOTS_5,
            ItemID.SHAYZIEN_GLOVES_5,
            ItemID.SHAYZIEN_BODY_5,
            ItemID.SHAYZIEN_GREAVES_5,
            ItemID.SHAYZIEN_HELM_5
    );

    boolean inFight() {
        return PlayerSettings.getBitValue(IN_FIGHT_VARBIT) != 0;
    }
}
