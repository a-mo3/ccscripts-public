package org.dreambot.behaviour.quests.animalmagnetism.behaviour.undeadchickens;


import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.quests.animalmagnetism.util.SpecialWalker;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.loadout.events.WithdrawLoadoutEvent;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

public class StartAnimalMag extends Fractal {
    InventoryLoadout requirements = new InventoryLoadout()
            .addItem(ItemID.MITHRIL_AXE)
            .addItem(ItemID.IRON_BAR, 5)
            .addItem(ItemID.GHOSTSPEAK_AMULET)
            .addItem(ItemID.HAMMER)
            .addItem(ItemID.HARD_LEATHER)
            .addItem(ItemID.HOLY_SYMBOL)
            .addItem(ItemID.POLISHED_BUTTONS);

    EquipmentLoadout teleports = new EquipmentLoadout()
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
            .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
            .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET);

    @Override
    public boolean isValid() {
        return !PaidQuest.ANIMAL_MAGNETISM.isStarted();
    }

    @Override
    public int onLoop() {
        if (Inventory.contains(ItemID.SALMON)) {
            Inventory.dropAll(ItemID.SALMON);
            return ReactionGenerator.getNormal();
        }

        if (Inventory.contains(ItemID.VIAL)) {
            Inventory.dropAll(ItemID.VIAL);
            return ReactionGenerator.getNormal();
        }

        //Client.setInteractionMode(InteractionMode.INSTANT);
        if (Equipment.contains(ItemID.GHOSTSPEAK_AMULET)) {
            //Client.setInteractionMode(InteractionMode.INSTANT);
            if (Inventory.isFull()) {
                Log.info("my inventory is full and ausbot wasnt sure what to drop when he wrote this :(");
                Log.info("need ghostspeak amutlet in inv not in equipment");
                return ReactionGenerator.getNormal();
            }
            Equipment.unequip(EquipmentSlot.AMULET);
            return ReactionGenerator.getNormal();
        }

        if (!requirements.isFulfilled() || !teleports.isFulfilled()) {
            new WithdrawLoadoutEvent(requirements, teleports)
                    .setBuyRemainder(true)
                    .executed();
            return ReactionGenerator.getNormal();
        }

        if (!SpecialWalker.enterAvasRoom()) {
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            Dialog.solve("happy to make your home a better", "Yes.");
            //Dialogues.solve("happy to make your home a better", "Yes.");
            return ReactionGenerator.getNormal();
        }

        NPC ava = NPCs.closest("AVa");
        if (ava != null && ava.interact("Talk-to")) {
            Sleep.sleepUntil(Dialogues::inDialogue, 2400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
