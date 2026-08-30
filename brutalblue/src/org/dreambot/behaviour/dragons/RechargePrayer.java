package org.dreambot.behaviour.dragons;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class RechargePrayer extends Fractal {
    public static boolean shouldRecharge = false;
    Area CATACOMBS = new Area(1601, 10115, 1644, 10064);
    Area CATABOMBS_EXIT= new Area(1613, 10105, 1620, 10098);
    Area ALTAR_AREA = new Area(1545, 3812, 1549, 3804);

    public RechargePrayer() {
        super(() -> shouldRecharge);
//        super(() -> true);

        EntranceWebNode hole =  new EntranceWebNode(new Tile(1563, 3791, 0), "Hole", "Enter");
        EntranceWebNode vine =  new EntranceWebNode(new Tile(1617, 10102, 0), "Vine", "Climb-up");

        WebFinder finder = WebFinder.getWebFinder();
        hole.addDualConnections(vine);
        finder.getNearest(hole, 20).addDualConnections(hole);
        finder.getNearest(vine, 20).addDualConnections(hole);
    }

    @Override
    public int onLoop() {
//        if (true) {
//            if (Walking.shouldWalk()) Walking.walk(CATACOMBS);
//            return ReactionGenerator.getNormal();
//        }
        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (Walking.getRunEnergy() < 10 && stamina != null) {
            stamina.interact();
        }

        if (Walking.getRunEnergy() > 5 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
            return ReactionGenerator.getNormal();
        }

        if (Skills.getBoostedLevel(Skill.PRAYER) > 40) {
            shouldRecharge = false;
            return ReactionGenerator.getQuick();
        }

        if (CATACOMBS.contains(Players.getLocal())) {
            if (!CATABOMBS_EXIT.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(CATABOMBS_EXIT);
                return ReactionGenerator.getQuick();
            }

            GameObject vine = GameObjects.closest("Vine");
            if (vine != null && vine.interact()) {
                Sleep.sleepUntil(() -> !CATACOMBS.contains(Players.getLocal()), 1200);
            }
            return ReactionGenerator.getQuick();
        }

        Prayers.toggle(false, Prayer.PROTECT_FROM_MAGIC);
        if (!ALTAR_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ALTAR_AREA);
            return ReactionGenerator.getQuick();
        }

        GameObject altar = GameObjects.closest("Altar");
        if (altar != null && altar.interact("Pray")) {
            Sleep.sleepUntil(() -> Skills.getBoostedLevel(Skill.PRAYER) < 40, 2400);
        }
        return ReactionGenerator.getQuick();
    }
}
