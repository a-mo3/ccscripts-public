package org.dreambot.behaviour.quests.demonslayer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashMap;
import java.util.function.Supplier;

public class FightDelrith extends Fractal {
    public static final int DELRITH_INCANTATION_1 = 2562;
    public static final int DELRITH_INCANTATION_2 = 2563;
    public static final int DELRITH_INCANTATION_3 = 2564;
    public static final int DELRITH_INCANTATION_4 = 2565;
    public static final int DELRITH_INCANTATION_5 = 2566;

    private final HashMap<Integer, String> words = new HashMap<Integer, String>() {{
        put(0, "Carlem");
        put(1, "Aber");
        put(2, "Camerinthum");
        put(3, "Purchai");
        put(4, "Gabindo");
    }};

    public FightDelrith(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Kill Delrith");
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.SILVERLIGHT)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
        ;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.SHARK, 1, 20)
        ;
    }

    int wordCounter = 0;
    Area MAGE_CIRCLE  = new Area(3225, 3371, 3228, 3368);

    @Override
    public int onLoop() {
        // get into instance, walk to the circle
        if (!Client.isDynamicRegion()) {
            log("Go to mage circle");
            if (Walking.shouldWalk()) Walking.walk(MAGE_CIRCLE);
            return ReactionGenerator.getNormal();
        }

        // say the curse the
        if (Dialogues.inDialogue()) {
            if (!Dialogues.areOptionsAvailable()) {
                log("No options only continue");
                Dialogues.continueDialogue();
                return ReactionGenerator.getNormal();
            }

            if (wordCounter > 4) {
                log("Word counter reset");
                wordCounter = 0;
            }
            int worldVal = PlayerSettings.getBitValue(DELRITH_INCANTATION_1 + wordCounter);
            String word = words.get(worldVal);
            log("Incantation " + word);
            Dialogues.chooseFirstOption(word);
            wordCounter++;
            return ReactionGenerator.getNormal() + 1000;
        }

        // eat
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < 10) {
            log("Eat a shark");
            Inventory.interact(ItemID.SHARK);
        }

        // handle being attacked by a dark wizard before delrith
        Character attackingMe = Players.getLocal().getCharacterInteractingWithMe();
        Character target = Players.getLocal().getInteractingCharacter();
        if (attackingMe != null) {
            log("Attacking me");
            if (!attackingMe.equals(target)) {
                log("Retaliate");
                attackingMe.interact();
            }
            return ReactionGenerator.getNormal();
        } else {
            NPC del = NPCs.closest("Delrith");
            if (del != null) {
                del.interact();
            } else {
                log("Failed to find delrith");
            }
        }


        return ReactionGenerator.getNormal();
    }
}
