package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.scriptdata.LavaDragonSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import static org.dreambot.behaviour.method.lavadragons.KillLavaDragons.worldHopDisableLoadout;

/*
we dont want to exit with event logic because it wont include anti pk logic
we also want to decide with exit area to go to and teleport out of
 */
public class ExitLavaDragons extends Fractal {
    final LavaDragonSettings settings;

    public ExitLavaDragons(LavaDragonSettings settings) {
        super(() ->
//                worldHopDisableLoadout.finished() &&
                Combat.isInWild()
                && (!Inventory.contains(ItemID.JUG_OF_WINE) || Inventory.contains(ItemID.ONYX_BOLT_TIPS) || settings.lavaDragonLoadout.shouldExit.get()));
        this.settings = settings;
        this.setSimpleName("Exit");
        selectedExit = exits[ShuffleFractal.getLoginValue() % exits.length];
    }

    Area EXIT_LEFT_SIDE = new Area(3150, 3755, 3166, 3750);
    //    Area EXIT_RIGHT_SIDE = new Area(3260, 3747, 3267, 3740);
    Area[] exits = new Area[]{EXIT_LEFT_SIDE};
    Area selectedExit;

    Area SUICIDE_AREA = new Area(3205, 3840, 3212, 3832);

    @Override
    public int onLoop() {
        Player threat = Players.closest(x -> settings.antiPKStrategy.pkClassifier.test(x));
        if (threat != null) {
            log("Threat exists " + threat.getName());
            WorldHopper.hopWorld(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER).getWorld(), false);
            return ReactionGenerator.getQuick();
        }

        // if suicide, kill self
        if (settings.suicide) {
            NPC dragon = NPCs.closest("Lava dragon");
            if (dragon == null) {
                log("No dragon to kill self on, walking to isle");
                if (Walking.shouldWalk()) Walking.walk(SUICIDE_AREA);
                return ReactionGenerator.getQuick();
            } else {
                // stand next to a dragon
                log("Kill self");
                if (dragon.distance() > 3) {
                    Walking.walk(dragon);
                } else {
                    dragon.interact("Attack");
                }
            }
            return ReactionGenerator.getQuick();
        }

        // go to one of the exit areas and tp out
        LootingBag.close();
        if (Combat.getWildernessLevel() <= 30) {
            log("Go to bank");
            if (Walking.shouldWalk()) Walking.walk(BankLocation.EDGEVILLE);
        } else {
            log("go to <=30 wild");
            if (Walking.shouldWalk()) Walking.walk(selectedExit);
        }
        return ReactionGenerator.getQuick();
    }
}
