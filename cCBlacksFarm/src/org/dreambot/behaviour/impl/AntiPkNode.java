package org.dreambot.behaviour.impl;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.events.HopEvent;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.settings.script.AntiPkMode;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AntiPkNode extends Fractal implements SpawnListener {
    public static boolean shouldHop;
    public static HashSet<Integer> blackList;


    public AntiPkNode() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean isValid() {
        return Combat.isInWild() && getThreat() != null;
//        return true;
    }

    public static int avoided = 0;

    @Override
    public int onLoop() {
        shouldHop = true;
        if (Players.getLocal().isInCombat() || Players.getLocal().isHealthBarVisible()) {
            if (Combat.getWildernessLevel() > 30) {
                Logger.info("Attempting to run away");
                Walking.walk(Players.getLocal().getTile().translate(0, -10));
                return ReactionGenerator.getQuick() + 250;
            }

            Logger.info("Attempting to go ge");
            Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }

        if (ScriptSettings.getSettingsData().quickHop) {
            Logger.info("hop event - " + new HopEvent().executed());
            avoided++;
        } else {
            Client.setIdleTime(30_000_000);
        }
        return ReactionGenerator.getQuick();
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
        AntiPkMode antiPkMode = ScriptSettings.getSettingsData().getAntiPkMode();
        if (antiPkMode == null) return null;
        Player threat = Players.closest(x -> !x.equals(Players.getLocal())
                && (ScriptSettings.getSettingsData().getAntiPkMode() != AntiPkMode.LENIENT && PKTrie.checkString(x.getName())
                && canAttackMe(x)));
        if (threat != null) {
            Logger.info("Blacklisted played: " + threat.getName());
            return threat;
        }

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


    @Override
    public void onPlayerSpawn(Player entity) {
        if (!Combat.isInWild()) return;
        if (getThreat() != null) {
            log("Threat found " + getThreat());
            Client.setIdleTime(30_000_000);
        }
    }
}
