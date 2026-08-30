package org.dreambot.behaviour.training.slayer;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.BankUtil;

import java.util.Arrays;
import java.util.List;

public class Helper {
    public static final Area MAIN_MAP = new Area(1159, 4163, 3935, 2491);
    /**
     *
     * @param s skill
     * @param high high exclusive
     * @param low low inclusive
     * @return if between the two
     */
    public static boolean skillBetween(Skill s, int high, int low) {
        return Skills.getRealLevel(s) < high && Skills.getRealLevel(s) >= low;
    }

    // list of areas where bank closest trys to walk to something that is 'close' but is actually another dungeon
    public static List<Area> bustedDungeons = Arrays.asList(
            new Area(1593, 10115, 1743, 9977), // kourend catacombs
            new Area(1851, 5255, 1919, 5181) // vault of war
    );

    public static boolean safeOpenBank() {
        Logger.info("safe bank open");
        if (bustedDungeons.stream().noneMatch(x -> x.contains(Players.getLocal()))) {
            Logger.info("safe bank open - in main");
            return BankUtil.openClosest();
        }

        Logger.info("safe bank open - GE");
        return Bank.open(BankLocation.GRAND_EXCHANGE);
    }
}
