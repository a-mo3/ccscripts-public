package org.dreambot.behaviour.magearenaone;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class FightKolodion extends Fractal {
    @Override
    public boolean isValid() {
        return MageArenaOneBranch.MAGE_ARENA.contains(Players.getLocal());
    }

    @Override
    public int onLoop() {
        if (!Players.getLocal().isInCombat()) {
//            if (Players.getLocal().getCharactersInteractingWithMe() != null) {
//                if (!Prayers.isActive(Prayer.PROTECT_ITEM)) Prayers.toggle(true, Prayer.PROTECT_ITEM);
//                log("IM BEING ATTACKED!");
//            }

            NPC kolodion = NPCs.closest("Kolodion");
            if (kolodion != null && kolodion.interact("Attack")) {
                Sleep.sleepUntil(() -> Players.getLocal().getCharacterInteractingWithMe() != null, 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (Magic.isAutocasting()) {
            Magic.setAutocastSpell(Normal.WATER_BLAST);
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) < 10) {
            Item prayerPot = ItemVariants.PRAYER_POTION.getItem();
            if (prayerPot != null) {
                Inventory.interact(prayerPot, "Drink");
            }
            return ReactionGenerator.getNormal();
        }

        Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);

        if (!Combat.isAutoRetaliateOn()) {
            Combat.toggleAutoRetaliate(true);
            return ReactionGenerator.getNormal();
        }

        if (Combat.getHealthPercent() < 75) {
            Inventory.interact(ItemID.SALMON, "Eat");
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
