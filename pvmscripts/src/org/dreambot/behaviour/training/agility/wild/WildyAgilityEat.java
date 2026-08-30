package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

/**
 * eat depending of suicide or normal
 */
public class WildyAgilityEat extends TickDecision {
    final WildernessAgilityMode mode;

    public WildyAgilityEat(WildernessAgilityMode mode) {
        this.mode = mode;
        setSimpleName("Wild agil eat");
    }

    List<Integer> foods = Arrays.asList(
            ItemID.SARDINE,
            ItemID.JUG_OF_WINE,
            ItemID.BLIGHTED_KARAMBWAN,
            ItemID.BLIGHTED_MANTA_RAY,
            ItemID.BLIGHTED_KARAMBWAN
    );

    @Override
    public boolean evaluate() {
        if (Inventory.contains(ItemID.JUG)) Inventory.dropAll(ItemID.JUG);
        if (mode == WildernessAgilityMode.SUICIDE) {
            int hp = Skills.getBoostedLevel(Skill.HITPOINTS);
            if (hp <= 2) {
                log("Heal");
                if (Inventory.contains(x -> foods.contains(x.getId())) && Combat.getHealthPercent() < 40) {
                    log("eat");
                    Inventory.interact(x -> foods.contains(x.getId()));
                }

                return false;
            }
            if (hp > 4) {
                log("Rock cake");
                Inventory.interact(ItemID.DWARVEN_ROCK_CAKE_7510, "Guzzle");
            }
            return false;
        }

        if (Inventory.contains(x -> foods.contains(x.getId())) && Combat.getHealthPercent() < 80) {
            log("eat");
            Inventory.interact(x -> foods.contains(x.getId()));
        }
        return false;
    }
}
