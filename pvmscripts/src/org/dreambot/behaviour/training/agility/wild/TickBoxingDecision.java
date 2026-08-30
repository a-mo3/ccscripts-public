package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.listener.HitSplatListener;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.comms.impl.agility.BoxingClient;
import org.dreambot.fractals.TickDecision;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TickBoxingDecision extends TickDecision implements HitSplatListener {
    final WildernessAgilityMode mode;
    public static final Timer timeSinceBoxed = new Timer(3 * 1000);

    public TickBoxingDecision(WildernessAgilityMode mode) {
        this.mode = mode;
        setSimpleName("Box");
        Client.getInstance().addEventListener(this);
    }

    String teamMateName;
    List<Integer> ignoreAnimations = Arrays.asList(
            749, // pipe
            741, // lava stones
            751 // rope swing
    );

    @Override
    public boolean evaluate() {
        if (mode != WildernessAgilityMode.BOXING) return false;
        if (!timeSinceBoxed.finished()) return false;
        teamMateName = BoxingClient.getInstance().teamMate;
        Player p = Players.closest(teamMateName);
        if (p == null) {
            log("No teammate to box");
            return false;
        }

        // todo check if combat with someone else
//        if (p.equals(Players.getLocal().getInteractingCharacter()) || p.equals(Players.getLocal().getCharacterInteractingWithMe())) {
//            log("Am boxing partner");
//            timeSinceBoxed.reset();
//            return false;
//        }

        String teamMateName = BoxingClient.getInstance().teamMate;
        Player teammate = Players.closest(teamMateName);
        int teamMateIndex = 0;
        int ourIndex = 0;
        if (teammate != null) {
            log("Teammate present");
            for (int i = 0; i < WildyCourseDecision.teammateConditions.size(); i++) {
                if (WildyCourseDecision.teammateConditions.get(i).test(teammate)) {
                    log("Teammate at " + i);
                    teamMateIndex = i;
                }
                if (WildyCourseDecision.teammateConditions.get(i).test(Players.getLocal())) {
                    log("We're at index " + i);
                    ourIndex = i;
                }
            }
        } else {
            log("No teammate present");
        }

        // tunnel animation
        if (ignoreAnimations.contains(p.getAnimation())) {
            log("Teammate animation skip " + p.getAnimation());
            return false;
        }

        if (teamMateIndex == ourIndex) {
            log("Attack teammate");
            if (p.canReach()) p.interact("Attack");
        } else {
            log("Uneven index skip");
            return false;
        }
        return true;
    }

    @Override
    public void onHitSplatAdded(Entity entity, int type, int damage, int id, int special, int gameCycle) {
        if (teamMateName == null) return;
        Player p = Players.closest(teamMateName);
        if (p == null) return;
        if (entity.equals(Players.getLocal())) {
            log("Local hit splat");
            if (Players.getLocal().getCharactersInteractingWithMe().stream().anyMatch(x -> x.equals(p))) {
                log("Am boxing partner");
                timeSinceBoxed.reset();
                return;
            }
        }


        Character target = Players.getLocal().getInteractingCharacter();
        if (Objects.equals(entity.getName(), teamMateName)
                && target != null
                && Objects.equals(target.getName(), teamMateName)) {
            log("Teammate splat");
            log("Am boxing partner");
            timeSinceBoxed.reset();
        }
    }
}
