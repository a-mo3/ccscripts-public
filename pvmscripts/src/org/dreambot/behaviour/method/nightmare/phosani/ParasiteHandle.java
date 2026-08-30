package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ParasiteHandle extends Fractal {
    public ParasiteHandle(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    public static final String PARASITE = "Parasite";

    @Override
    public int onLoop() {
        // todo ensure elder maul / crush weapon is equipped
        if (Inventory.contains(ItemID.ARMADYL_GODSWORD)) {
            Inventory.interact(ItemID.ARMADYL_GODSWORD);
            Sleep.sleep(ReactionGenerator.getQuick());
        }

        if (Combat.getCombatStyle() != null) {
            Logger.info("Switch combat style to str");
            Combat.setCombatStyle(null);
            return ReactionGenerator.getQuick();
        }

        NPC parasite = NPCs.closest(x -> x.getName().equals(PARASITE) && x.getHealthPercent() > 0);
        Character target = Players.getLocal().getInteractingCharacter();
        if (parasite != null && (target == null || !target.getName().equals(PARASITE))) {
            if (Combat.getSpecialPercentage() >= 50) Combat.toggleSpecialAttack(true);
            parasite.interact("Attack");
        }
        return ReactionGenerator.getNormal();
    }
}
