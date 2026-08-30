package org.dreambot.behaviour.antelopes;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.settings.timing.ReactionGenerator;

public class EatFood extends Fractal {
    @Override
    public boolean isValid() {
        Logger.info("eat food isvalid");
        return (Skills.getBoostedLevel(Skill.HITPOINTS) < 7 || Combat.getHealthPercent() < 50) && Inventory.contains(ItemID.JUG_OF_WINE);
    }

    @Override
    public int onLoop() {
        Inventory.interact(ItemID.JUG_OF_WINE);
        Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.HITPOINTS) > 7, 800);
        return ReactionGenerator.getNormal();
    }
}
