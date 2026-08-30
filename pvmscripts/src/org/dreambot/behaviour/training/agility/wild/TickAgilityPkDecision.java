package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;

/**
 * Escape a pker or box your partner safely
 */
public class TickAgilityPkDecision extends TickDecision {
    final WildernessAgilityMode mode;

    public TickAgilityPkDecision(WildernessAgilityMode mode) {
        this.mode = mode;
        setSimpleName("Tick pk");
    }

    @Override
    public boolean evaluate() {
        if (mode == WildernessAgilityMode.BH_RAG_WORLD) return false;
        if (mode == WildernessAgilityMode.SUICIDE) {
            Player attackingUs = Players.getLocal().getCharactersInteractingWithMe().stream()
                    .filter(x -> x instanceof Player)
                    .filter(Character::isInCombat)
                    .map(x -> (Player) x)
                    .findFirst().orElse(null);
            if (attackingUs != null) {
                RagList.report(attackingUs.getName());
            }
        }

        Player pker = RagList.getRagTarget();
        if (pker == null) return false;

        log("Pker " + pker);
        if (mode == WildernessAgilityMode.SUICIDE) {
            log("Kill self");
            if (Skill.HITPOINTS.getBoostedLevel() <= 4) Inventory.interact(ItemID.ARAXYTE_VENOM_SACK);
            return true;
        }

        String teamMateName = BoxingClient.getInstance().teamMate;
        Player teammate = Players.closest(teamMateName);
        Player attackingUs = Players.getLocal().getCharactersInteractingWithMe().stream()
                .filter(x -> x instanceof Player)
                .map(x -> (Player) x)
                .findFirst()
                .orElse(null);
        String attackingUsName = attackingUs == null ? "jmodchickenpenis" : attackingUs.getName();
        String teammateName = teammate == null ? "tmjmodchickenpenis" : teammate.getName();
        boolean boxingTeammate = attackingUsName.equals(teammateName);
        if (attackingUs != null) log("In combat with someone, teammate? " + boxingTeammate);

        // todo we need to grab threat here
        if (boxingTeammate) {
            log("We're boxing teammate so just keep doing that until threat leaves");
            return false;
        }

        // todo try and escape
        Client.setIdleTime(100_000);
        Client.logout();
        return false;
    }
}
