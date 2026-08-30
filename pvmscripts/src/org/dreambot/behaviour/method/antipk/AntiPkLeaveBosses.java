package org.dreambot.behaviour.method.antipk;

import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.settings.timing.ReactionGenerator;

public class AntiPkLeaveBosses extends Fractal {
    public static final Area SPINDEL_EXIT_CAVE = new Area(1628, 11531, 1633, 11528, 2);
    public static final Area SPINDEL_CHASM = new Area(1617, 11567, 1645, 11528, 2);
    public static final Area REV_CAVES = new Area(3281, 10244, 3131, 10051);

    public static final Area TOMB_EXIT = new Area(1884, 11537, 1889, 11534, 1);
    public static final Area TOMB_ENTRANCE = new Area(3176, 3686, 3182, 3680);
    public static final Area CALVARION_ARENA = new Tile(1888, 11545, 1).getArea(50);

    @Override
    public int onLoop() {
        return leaveBosses();
    }

    public static int leaveBosses() {
        Logger.info("Leave bosses");
        UtilProvider.staminaUp();

        Player lp = Players.getLocal();
        if (!REV_CAVES.contains(lp) && lp.getY() > 5000) {
            GameObject exit = GameObjects.closest(x -> x.hasAction("Exit"));
            if (exit != null) {
                Logger.info("Exiting cave");
                exit.interact();
                return 350;
            }
        }

        // tp out and stuff should be handled by rest of pk tree
        // walk to edgeville we assume if we are here we are TB'd
        Logger.info("Run to edgeville");
        Walking.walk(BankLocation.EDGEVILLE);
        return ReactionGenerator.getQuick();
    }
}
