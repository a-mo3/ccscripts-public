package org.dreambot.behaviour.training.hunter;

import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.HashSet;

public class BlackChinAntiPkNode extends Fractal implements SpawnListener {

    public static int avoidCount = 0;

    public BlackChinAntiPkNode() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public boolean isValid() {
        return Combat.isInWild() && getThreat() != null;
    }


    @Override
    public int onLoop() {
        if (Players.getLocal().isInCombat() || Players.getLocal().isHealthBarVisible()) {
            if (Skills.getRealLevel(Skill.HITPOINTS) != Skills.getBoostedLevel(Skill.HITPOINTS)
                    && Inventory.contains(ItemID.BLIGHTED_MANTA_RAY)) Inventory.interact(ItemID.BLIGHTED_MANTA_RAY);

            if (Combat.getWildernessLevel() > 30) {
                Logger.info("Attempting to run away");
                Walking.walk(Players.getLocal().getTile().translate(0, -10));
                return ReactionGenerator.getQuick() + 250;
            }

            Logger.info("Attempting to go ge");
            Walking.walk(BankLocation.GRAND_EXCHANGE);
            return ReactionGenerator.getQuick();
        }

        Client.setIdleTime(30_000_000);
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
        Player threat = Players.closest(x -> !x.equals(Players.getLocal())
                && (PKTrie.checkString(x.getName()) || x.isSkulled())
                && canAttackMe(x));
        if (threat != null) {
            Logger.info("Blacklisted played: " + threat.getName());
            return threat;
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
