package org.dreambot.behaviour.method.gwd;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class GWDRechargeAtFerox extends Fractal {
    static Area UNDER_FEROX = new Area(3118, 10050, 3211, 9985);

    public GWDRechargeAtFerox() {
        super(() -> !Combat.isInWild()
                && shouldRecharge()
                && (Players.getLocal().getY() < 3650 || UNDER_FEROX.contains(Players.getLocal()))// top of trollheim
        );

        this.loadoutCondition = () -> !Equipment.contains(x -> ItemVariants.RING_OF_DUELING.contains(x.getId()))
                && ItemVariants.RING_OF_DUELING.getItem() == null; // this is inv check

    }

    Area FEROX_POOL = new Area(3127, 3636, 3130, 3633);

    @Override
    public int onLoop() {
        if (!FEROX_POOL.contains(Players.getLocal())) {
            slowLog("Walk to ferox");
            if (Walking.shouldWalk()) Walking.walk(FEROX_POOL);
            return ReactionGenerator.getNormal();
        }

        GameObject pool = GameObjects.closest("Pool of Refreshment");
        if (pool != null && pool.interact("Drink")) {
            log("Recharging prayer");
            Antiban.sleepUntil(GWDRechargeAtFerox::shouldRecharge, 4200 + ReactionGenerator.getNormal());
        }
        return ReactionGenerator.getNormal();
    }

    private static boolean shouldRecharge() {
        return Skills.getBoostedLevel(Skill.PRAYER) < Skills.getRealLevel(Skill.PRAYER)
                || Skills.getBoostedLevel(Skill.STRENGTH) < Skills.getRealLevel(Skill.STRENGTH)
                ;
    }
}
