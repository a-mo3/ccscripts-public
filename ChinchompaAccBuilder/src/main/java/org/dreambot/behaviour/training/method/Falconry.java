package org.dreambot.behaviour.training.method;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.hint.HintArrow;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.CatchBirdsFractal;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.NpcID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class Falconry extends Fractal {
    Area FALCONRY_GUY = new Area(2368, 3613, 2376, 3604);
    Area KEBBITS = new Area(2372, 3593, 2379, 3581);

    public Falconry(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COINS_995, 100, 1000);
    }

    @Override
    public int onLoop() {
        if (!Equipment.contains(ItemID.FALCONERS_GLOVE) && !Equipment.contains(ItemID.FALCONERS_GLOVE_10024)) {
            if (!Equipment.isSlotEmpty(EquipmentSlot.HANDS)
                    || !Equipment.isSlotEmpty(EquipmentSlot.SHIELD)
                    || !Equipment.isSlotEmpty(EquipmentSlot.WEAPON)) {
                if (Inventory.isFull()) {
                    CatchBirdsFractal.buryBones();

                    Inventory.dropAll(ItemID.BIRD_SNARE, ItemID.RAW_BIRD_MEAT, ItemID.BONES, ItemID.RAW_DASHING_KEBBIT, ItemID.KEBBITY_TUFT, ItemID.BIRD_SNARE);
                    return ReactionGenerator.getNormal();
                }

                Equipment.unequip(EquipmentSlot.HANDS);
                Equipment.unequip(EquipmentSlot.SHIELD);
                Equipment.unequip(EquipmentSlot.WEAPON);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve("have a go with your", "that seems", "please");
                return ReactionGenerator.getNormal();
            }

            if (!FALCONRY_GUY.contains(Players.getLocal())) {
                if (Walking.shouldWalk(6)) Walking.walk(FALCONRY_GUY.getCenter());
                return ReactionGenerator.getNormal();
            }

            if (!Dialogues.inDialogue()) {
                NPC guy = NPCs.closest(NpcID.MATTHIAS_1341);
                if (guy != null && guy.interact("Talk-to")) {
                    Sleep.sleepUntil(Dialogues::inDialogue, 4500);
                }
            }
            return ReactionGenerator.getNormal();
        }

        if (Inventory.emptySlotCount() < 3) {
            Inventory.dropAll(ItemID.SPOTTED_KEBBIT_FUR, ItemID.DARK_KEBBIT_FUR, ItemID.DASHING_KEBBIT_FUR, ItemID.BONES, ItemID.RAW_DASHING_KEBBIT, ItemID.KEBBITY_TUFT, ItemID.BIRD_SNARE);
            return ReactionGenerator.getNormal();
        }

        NPC myBird = getArrowNpc();
        if (myBird == null && Equipment.contains(ItemID.FALCONERS_GLOVE)) {
            if (!FALCONRY_GUY.contains(Players.getLocal())) {
                if (Walking.shouldWalk(6)) Walking.walk(FALCONRY_GUY.getRandomTile());
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            NPC guy = NPCs.closest(NpcID.MATTHIAS_1341);
            if (guy != null && guy.interact("Talk-to")) {
                Sleep.sleepUntil(Dialogues::inDialogue, 4500);
            }
            return ReactionGenerator.getNormal();
        }

        if (myBird != null && myBird.interact("Retrieve")) {
            Sleep.sleepUntil(() -> getArrowNpc() == null, 3000);
            return ReactionGenerator.getNormal();
        }

        NPC kebbit = NPCs.closest(getKebbitId());
        if (kebbit == null) {
            if (Walking.shouldWalk()) Walking.walk(KEBBITS);
            return ReactionGenerator.getNormal();
        }


        if (kebbit.interact("Catch")) {
            Sleep.sleepUntil(() -> getArrowNpc() != null, 3000);
        }


        return ReactionGenerator.getNormal();
    }

    private int getKebbitId() {
        if (Skills.getRealLevel(Skill.HUNTER) < 57) return NpcID.SPOTTED_KEBBIT;
        if (Skills.getRealLevel(Skill.HUNTER) < 69) return NpcID.DARK_KEBBIT;
        return NpcID.DASHING_KEBBIT;
    }

    private NPC getArrowNpc() {
        Character c = HintArrow.getPointed();
        if (c instanceof NPC) {
            return (NPC) c;
        }
        return null;
    }
}
