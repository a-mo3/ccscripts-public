package org.dreambot.behaviour.dragons;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.muling.Log;
import org.dreambot.pktrie.PKTrie;
import org.dreambot.settings.script.AntiPkMode;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

public class AntiPk extends Fractal {

    // reset when not in wildy, when attacked and cant log out run away, could be a timer but
    // i dont want to google what the logout timer is
    boolean bookIt = false;
    public static int dodgedCount = 0;
    Area HELLHOUNDS = new Area(3167, 3960, 3193, 3948);

    @Override
    public boolean isValid() {
        if (HELLHOUNDS.contains(Players.getLocal())) return false;

        Player threat = getThreat();
        if (!Combat.isInWild() || threat == null) {
            bookIt = false;
        }

        return Combat.isInWild() && threat != null;
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().isHealthBarVisible() && getThreat() != null) {
            Logger.info("Book it");
            bookIt = true;
        }

        if (bookIt && ScriptSettings.getSettingsData().runaway) {
            if (!ExitDragon.WILDY_EXIT.contains(Players.getLocal())) {
                Logger.info("Trying to run away");
                if (Walking.shouldWalk(8)) Walking.walk(ExitDragon.WILDY_EXIT.getRandomTile());
                return ReactionGenerator.getQuick();
            }
//      if (Walking.shouldWalk(6)) Walking.walk(BankLocation.GRAND_EXCHANGE.getTile());
            // check if your inventory has glory rather than if you are wearing glory
            if (ItemVariants.AMULET_OF_GLORY.getItem() != null) {
                Logger.info("Occult mode teleport out");
                if (Walking.shouldWalk(6)) Walking.walk(BankLocation.EDGEVILLE.getTile());
                Sleep.sleepUntil(() -> !Combat.isInWild(), 4400);
                return ReactionGenerator.getQuick();
            }
            Equipment.interact(EquipmentSlot.AMULET, "Edgeville");
            Sleep.sleepUntil(() -> !Combat.isInWild(), 4400);
            return ReactionGenerator.getQuick();
        }

        Player threat = getThreat();
        if (ScriptSettings.getSettingsData().quickHop) {
            new HopEvent().setIgnoreGlobalCondition(true).executed();
            if (threat != null) {
                Logger.info(String.format("hopped from %s Skulled: %b", threat.getName(), threat.isSkulled()));
                for (Item item : threat.getEquipment()) {
                    Log.info(String.format("%s ", item.getName()));
                }
            }
        } else {
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> !x.isF2P()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && x.isNormal()));
        }
        dodgedCount++;
        return ReactionGenerator.getNormal();
    }

    public static boolean canAttackMe(Player threat) {
        if (threat.getName().equals(Players.getLocal().getName())) return false;
        int threatLvl = threat.getLevel();
        int mylvl = Combat.getCombatLevel();
        int wildernessLvl = Combat.getWildernessLevel();
        if (threat.distance() > 48) return false;
        return threatLvl >= (mylvl - wildernessLvl) && threatLvl <= (wildernessLvl + mylvl);
    }

    /**
     * @return a player that matches the anti pk settings threat detection
     */
    public static Player getThreat() {
        if (!Combat.isInWild()) return null;
        Player threat = Players.closest(x -> PKTrie.checkString(x.getName()) && canAttackMe(x));
        if (threat != null) {
            Logger.info("Blacklisted played: " + threat.getName());
            return threat;
        }
        AntiPkMode antiPkMode = ScriptSettings.getSettingsData().antiPKMode;
        if (antiPkMode == null) return null;

        if (antiPkMode == AntiPkMode.SKULLED_EQUIPMENT_BLACKLIST) {
            return Players.closest(p -> {
                if (!canAttackMe(p)) return false;
                if (p.isSkulled()) return true;

                if (p.getEquipment().size() > 4 && p.getEquipment().stream().noneMatch(x -> x.getID() == ItemID.TRIDENT_OF_THE_SEAS)) {
                    return true;
                }

                return false;
            });
        }

        if (antiPkMode == AntiPkMode.SKULLED_OR_EQUIPMENT) {
            return Players.closest(x -> canAttackMe(x) && !x.getName().equals(Players.getLocal().getName())
                    && (x.isSkulled() || x.getEquipment().stream().noneMatch(i -> botWeapons.contains(i.getID()))));
        }

        if (antiPkMode == AntiPkMode.SKULLED) {
            return Players.closest(x -> x.isSkulled() && !x.getName().equals(Players.getLocal().getName()) && canAttackMe(x));
        }

        if (antiPkMode == AntiPkMode.EQUIPMENT) {
            return Players.closest(x -> canAttackMe(x) && !x.getName().equals(Players.getLocal().getName())
                    && x.getEquipment().stream().noneMatch(i -> botWeapons.contains(i.getID())));
        }

        return null;
    }

    private static final List<Integer> botWeapons = Arrays.asList(
            ItemID.STAFF_OF_FIRE,
            ItemID.STAFF_OF_AIR,
            ItemID.TRIDENT_OF_THE_SEAS_FULL,
            ItemID.TRIDENT_OF_THE_SWAMP,
            ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.UNCHARGED_TRIDENT,
            ItemID.UNCHARGED_TOXIC_TRIDENT,
            ItemID.CRAWS_BOW,
            ItemID.CRAWS_BOW_U,
            ItemID.WEBWEAVER_BOW_U,
            ItemID.TOXIC_STAFF_UNCHARGED,
            ItemID.TOXIC_STAFF_OF_THE_DEAD,
            ItemID.ACCURSED_SCEPTRE_U,
            ItemID.ACCURSED_SCEPTRE,

            ItemID.THAMMARONS_SCEPTRE,
            ItemID.THAMMARONS_SCEPTRE_U,
            ItemID.WEBWEAVER_BOW
    );
}
