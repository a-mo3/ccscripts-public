package org.dreambot.behaviour.magearenaone;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class StartMageArena extends Fractal {
    // these are only used in this leaf, not the fight so dont need to worry about max / min
    InventoryLoadout mageArenaInv = new InventoryLoadout()
            .addItem(ItemID.PRAYER_POTION4, 2)
            .addItem(ItemID.SALMON, 20)
            .addItem(ItemID.STAMINA_POTION4, 1)
            .setEnabledCondition(() -> !Inventory.contains(ItemVariants.STAMINA_POTION) && !Combat.isInWild())
            .addItem(ItemID.KNIFE, 1)
            .addItem(ItemID.DEATH_RUNE, 150)
            .addItem(ItemID.AIR_RUNE, 450);

    EquipmentLoadout mageArenaEquipment = new EquipmentLoadout()
            .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .addItem(EquipmentSlot.HANDS, ItemID.MYSTIC_GLOVES)
            .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER) // lvl 1 atk
            ;

    public StartMageArena(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = mageArenaInv;
        this.equipmentLoadout = mageArenaEquipment;
    }

    @Override
    public int onLoop() {
        if (!Magic.isAutocasting()) {
            Magic.setAutocastSpell(Normal.WATER_BLAST);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getWildernessLevel() < 1 && !MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal())) {
            if (!MageArenaOneBranch.EDGEVILLE_SWITCH.contains(Players.getLocal())) {
                Walking.walk(MageArenaOneBranch.EDGEVILLE_SWITCH.getCenter());
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("brave");
                return ReactionGenerator.getNormal();
            }


            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // enter mage bank
        if (!MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal())) {

            GameObject web = GameObjects.closest("Web");
            if (web != null && web.distance() < 5 && web.interact("Slash")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            }

            if (!MageArenaOneBranch.MAGE_BANK_SWITCH.contains(Players.getLocal())) {
                Walking.walk(MageArenaOneBranch.MAGE_BANK_SWITCH.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(() -> MageArenaOneBranch.MAGE_ARENA_BANK.contains(Players.getLocal()), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        // talk to kolodion and start fight
        if (Dialogues.inDialogue()) {
            Dialog.solve("Can I fight here?", "Yes indeedy", "Okay, let's fight.");
            return ReactionGenerator.getNormal();
        }

        NPC kolodion = NPCs.closest("Kolodion");
        if (kolodion != null && kolodion.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
        }
        return ReactionGenerator.getNormal();
    }
}
