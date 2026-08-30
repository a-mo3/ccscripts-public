package org.dreambot.behaviour.method.venenatis;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.settings.timing.ReactionGenerator;

public class GoToVenenatis extends Fractal implements ChatListener {
    public static final Area CAVE_ENTRANCE = new Area(3316, 3800, 3323, 3791);
    Area FEROX_POOL = new Area(3127, 3636, 3130, 3633);
    public static final Area VENE_ARENA = new Area(3411, 10215, 3434, 10192, 2);

    public GoToVenenatis() {
        super(() -> !VENE_ARENA.contains(Players.getLocal()));
        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        if (!Combat.isInWild()) PrayerUtils.disableAll();

        // restore stats at ferox pool
        if (!areStatusFull() && !Combat.isInWild()) {
            if (!FEROX_POOL.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(FEROX_POOL);
                return ReactionGenerator.getNormal();
            }

            GameObject rejPool = GameObjects.closest("Pool of Refreshment");
            if (rejPool != null && !Players.getLocal().isMoving()) {
                rejPool.interact("Drink");
                Sleep.sleepUntil(GoToSpindel::areStatusFull, 2300);
            }
            return ReactionGenerator.getQuick();
        }

        if (Dialogues.inDialogue()) {
            if (Dialogues.canEnterInput()) Keyboard.type(" k", true);
            Dialog.solve("again", "Yes.");
            return ReactionGenerator.getQuick();
        }

        Item antiV = ItemVariants.ANTI_DOTE_PP.getItem();
        if (antiV != null && (Combat.isEnvenomed() || Combat.isPoisoned())) {
            log("Antidote");
            if (Inventory.isItemSelected()) Inventory.deselect();
            antiV.interact("Drink");
        }


        if (!CAVE_ENTRANCE.contains(Players.getLocal())) {
            UtilProvider.staminaUp();
            if (Walking.shouldWalk()) Walking.walk(CAVE_ENTRANCE);
            return ReactionGenerator.getQuick();
        }

        // we always crash this is a massing script
        GameObject entrance = GameObjects.closest(x -> x.hasAction("Enter"));
        if (entrance != null) {
            entrance.interact("Enter");
            Sleep.sleepUntil(() -> CAVE_ENTRANCE.contains(Players.getLocal()),
                    1200);
        }
        return ReactionGenerator.getQuick();
    }

    public static boolean areStatusFull() {
        if (Combat.isPoisoned() || Combat.isEnvenomed()) return false;
        if (Skills.getBoostedLevel(Skill.HITPOINTS) < Skills.getRealLevel(Skill.HITPOINTS)) return false;
        if (Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)) return false;
        return Walking.getRunEnergy() > 60;
    }

}
