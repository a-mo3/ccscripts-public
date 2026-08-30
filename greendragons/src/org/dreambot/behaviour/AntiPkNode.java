package org.dreambot.behaviour;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.settings.script.AntiPkMode;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AntiPkNode extends Fractal {
    public static boolean shouldHop;
    public static HashSet<Integer> blackList;

    @Override
    public boolean isValid() {
        return Combat.isInWild() && getThreat() != null;
//        return true;
    }

    public static int avoided = 0;

    @Override
    public int onLoop() {
        if (ScriptSettings.getSettingsData().runAway && Players.getLocal().isHealthBarVisible()
                && Equipment.contains(x -> x.getName().toLowerCase().contains("glory"))) {
            if (Combat.getWildernessLevel() > 30) {
                Walking.walk(Players.getLocal().getTile().translate(0, -10));
                return ReactionGenerator.getQuick();
            }

            Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }

        // no hopped basically always in combat and can tp out
        Walking.walk(BankLocation.GRAND_EXCHANGE);
        return ReactionGenerator.getQuick();
//        if (ScriptSettings.getSettingsData().quickHop) {
//            Logger.info("hop event - " + new HopEvent().executed());
//            avoided++;
//        } else {
//            if (WorldHopper.hopWorld(Worlds.getRandomWorld(x -> !x.isF2P()
//                    && x.getMinimumLevel() < Skills.getTotalLevel()
//                    && x.isNormal()))) avoided++;
//        }
    }

    public static boolean canAttackMe(Player threat) {
        if (threat.getName().equals(Players.getLocal().getName())) return false;
        int threatLvl = threat.getLevel();
        int mylvl = Combat.getCombatLevel();
        int wildernessLvl = Combat.getWildernessLevel();
        return threatLvl >= (mylvl - wildernessLvl) && threatLvl <= (wildernessLvl + mylvl);
    }

    /**
     * @return a player that matches the anti pk settings threat detection
     */
    public static Player getThreat() {
        AntiPkMode antiPkMode = ScriptSettings.getSettingsData().getAntiPKMode();
        if (antiPkMode == null) return null;
        Player threat = Players.closest(x -> canAttackMe(x) && PKTrie.checkString(x.getName()));
        if (threat != null) return threat;

        if (blackList == null) {
            Logger.info("Init blacklist set");
            blackList = Arrays.stream(ScriptSettings.getSettingsData().blackListedEquipment)
                    .boxed()
                    .collect(Collectors.toCollection(HashSet::new));
        }


        int[] whitelisted = ScriptSettings.getSettingsData().whitelistedEquipment;
        if (ScriptSettings.getSettingsData().useWhiteList) {
            Player op = Players.closest(x -> canAttackMe(x) && x.getEquipment().stream().noneMatch(i -> Arrays.stream(whitelisted).anyMatch(w -> w == i.getID())));
            if (op != null) {
                return op;
            }
        }

        if (ScriptSettings.getSettingsData().useBlackList) {
            Player op = Players.closest(x -> canAttackMe(x)
                    && x.getEquipment().stream().anyMatch(e -> blackList.contains(e.getID())));
            if (op != null) {
                Logger.info("Threat was found " + op.getName());
                return op;
            }
        }

        if (antiPkMode == AntiPkMode.SKULLED || antiPkMode == AntiPkMode.SKULLED_OR_EQUIPMENT) {
            return Players.closest(x -> x.isSkulled() && canAttackMe(x));
        }
        return null;
    }
}
