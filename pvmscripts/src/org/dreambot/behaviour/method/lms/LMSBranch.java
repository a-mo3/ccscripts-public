package org.dreambot.behaviour.method.lms;

import org.dreambot.api.Client;
import org.dreambot.api.methods.cs2.RuneScriptEvent;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.event.impl.ExperienceEvent;
import org.dreambot.api.script.event.impl.ScriptPrefiredEvent;
import org.dreambot.api.script.listener.*;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.lms.deathdot.FreezeDecision;
import org.dreambot.behaviour.method.lms.deathdot.LMSCounter;
import org.dreambot.behaviour.method.lms.deathdot.LMSEat;
import org.dreambot.behaviour.method.lms.deathdot.LMSSwitchDecision;
import org.dreambot.fractals.TickFractal;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LMSBranch extends TickFractal implements AnimationListener {
    private static String lastEnemyName;

    public static Player getEnemy() {
        if (lastEnemyName != null) {
            // cache last enemy because you get targeted often
            // the way we cache this isnt perfect, interacting does not exactly equal fighting, especially in the 12 second start
            Player last = Players.closest(lastEnemyName);
            if (last != null) return last;
        }

        List<Player> attackingMe = Players.getLocal()
                .getCharactersInteractingWithMe()
                .stream()
                .filter(x -> x instanceof Player)
                .map(x -> (Player) x)
                .collect(Collectors.toList());
        Player p = attackingMe.isEmpty() ? null : attackingMe.get(0);
        if (p != null && p.equals(Players.getLocal().getInteractingCharacter())) lastEnemyName = p.getName();
        return p;
    }

    public LMSBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("LMS");
        Client.getInstance().addEventListener(this);

        paintArraySupplier = () -> {
            Player first = getEnemy();

            return new String[]{
                    "Tick " + Client.getGameTick(),
                    "Attacked by " + first,
                    "Enemy overhead " + (first == null ? "-" : first.getOverheadIcon()),
                    "Enemy model height " + (first == null ? "-" : first.getRenderableHeight()),
                    "Enemy Gear " + (first == null ? "-" : Arrays.toString(first.getEquipment().toArray())),
                    "Our counter " + LMSCounter.actionCounter,
                    "Enemy counter " + LMSCounter.enemyActionCounter,
            };
        };

        addChildren(
                new LMSCounter(),
                new LMSEat(),
                new FreezeDecision().setSimpleName("Freeze"),
                new LMSSwitchDecision().setSimpleName("Switch")
        );
    }
}
