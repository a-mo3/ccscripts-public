package org.dreambot.behaviour.method.moonsofperil;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.moonsofperil.eclipsemoon.EclipseMoonClonesPhase;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * This serves to eat fish and keep track of the attack tick count down
 */
public class MoonsOfPerilEat extends TickDecision {
    @Override
    public boolean evaluate() {
        if (Skill.HITPOINTS.getBoostedLevel() > 25) {
            // consider potting up
            int defBoost = Skill.DEFENCE.getBoostedLevel() - Skill.DEFENCE.getLevel();
            Item pot = ItemVariants.MOONLIGHT_POTION.getItem();
            if ((defBoost < 4 || Skill.PRAYER.getBoostedLevel() < 5) && pot != null && MoonsOfPerilKitUp.BOSS_ROOMS.contains(Players.getLocal())) {
                pot.interact();
            }
            return false;
        }

        if (!Inventory.contains(ItemID.COOKED_BREAM)) {
            // todo escape the fight
            GameObject escape = GameObjects.closest(x -> x.hasAction("Quick-escape"));
            if (escape == null) {
                log("Failed to find quick escape");
                return false;
            }
            // in clones phase you cannot move, so we teleport out in that situation
            if (EclipseMoonClonesPhase.ECLIPSE_CENTER.equals(Players.getLocal().getTile())) {
                log("TP out to GE because we cant move in clone phase.");
                Walking.walk(BankLocation.GRAND_EXCHANGE);
                return true;
            }

            log("Trying to leave fight before we die.");
            escape.interact();
            return true;
        }
        // cooked bream heals 1/3 the lower of the cooking or fishing level of the player
        int healAmount = (int) (Math.min(Skill.COOKING.getLevel(), Skill.FISHING.getLevel()) * 0.3);
        // we are safe'ing high here, we could safe low instead and heal to full when gathering supplies for a run
        // would be more efficient
        // could also low safe here and high safe in no DPS phases
        int missingHealth = Skill.HITPOINTS.getLevel() - Skill.HITPOINTS.getBoostedLevel();
        if (healAmount >= missingHealth) {
            // dont need to heal
            return false;
        }

        Inventory.interact(ItemID.COOKED_BREAM);
        return false;
    }
}
