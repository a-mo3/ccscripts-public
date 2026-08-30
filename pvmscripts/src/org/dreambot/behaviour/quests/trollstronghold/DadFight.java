package org.dreambot.behaviour.quests.trollstronghold;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.walking.pathfinding.impl.obstacle.impl.PassableObstacle;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class DadFight extends Fractal {
    static Area DAD_SAFESPOT = new Area(2897, 3619, 2898, 3617);

    public DadFight(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.AIR_RUNE, 1000)
                .setEnabledCondition(() -> DAD_SAFESPOT.distance(Players.getLocal().getTile()) > 50)
                .addItem(ItemID.MIND_RUNE, 1000)
                .setEnabledCondition(() -> DAD_SAFESPOT.distance(Players.getLocal().getTile()) > 50)
        ;

        setSimpleName("Dad fight");

        // add walker passable objects for the rocks
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Rocks", "Climb"));
        LocalPathFinder.getLocalPathFinder().addObstacle(new PassableObstacle("Arena Entrance", "Open"));
    }


    @Override
    public int onLoop() {
        // enter arena
        if (!DAD_SAFESPOT.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(DAD_SAFESPOT);
            return ReactionGenerator.getNormal();
        }

        if (Dialogues.inDialogue()) {
            log("Dialog solve");
            Dialog.solve();
            return ReactionGenerator.getNormal();
        }

        // attack dad
        if (!Magic.isAutocasting() || Magic.getAutocastSpell() != Normal.FIRE_STRIKE) {
            Magic.setAutocastSpell(Normal.FIRE_STRIKE);
            return ReactionGenerator.getNormal();
        }

        NPC dad = NPCs.closest("Dad");
        if (dad != null && Players.getLocal().getInteractingCharacter() == null) {
            Logger.info("Atk dad");
            dad.interact("Attack");
        }
        return ReactionGenerator.getNormal();
    }
}
