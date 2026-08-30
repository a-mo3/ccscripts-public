package org.dreambot.behaviour.dragons;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.script.StaffMode;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class ExitDragon extends Fractal {
    // right side
//    final Area WILDY_EXIT = new Area(3226, 3749, 3243, 3756);
    // left/chin side
    public static final Area WILDY_EXIT = new Area(3162, 3759, 3170, 3753);

    private static Supplier<Boolean> cond = () -> {
        if (!Combat.isInWild()) return false; // this parts pretty important turns out :3
        SettingsData settings = ScriptSettings.getSettingsData();
        int magicLvl = Skills.getRealLevel(Skill.MAGIC);

        // leave wild if you are out of food
        if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
            Logger.info("Leaving wild no food");
            return true;
        }
        // leave wild to recharge sceptre
        if (Equipment.contains(ItemID.UNCHARGED_TRIDENT) || RechargeSceptre.shouldRecharge) {
            Logger.info("Leaving wild recharge trident");
            return true;
        }
        // leave wild with expensive loot
        if (Inventory.contains(ItemID.DRACONIC_VISAGE, ItemID.ONYX_BOLT_TIPS)) {
            Logger.info("Leaving wild valuable loot");
            return true;
        }
        // leave wild if you dont have the correct runes
        if (Equipment.contains(ItemID.STAFF_OF_WATER) && !Inventory.contains(ItemID.AIR_RUNE)) {
            Logger.info("Leaving wild no air runes");
            return true;
        }
        int castRunes = Inventory.count(ItemID.MIND_RUNE);
        if (magicLvl >= 27) castRunes += Inventory.count(ItemID.CHAOS_RUNE);
        if (magicLvl >= 47) castRunes += Inventory.count(ItemID.DEATH_RUNE);
        if (magicLvl >= 65) castRunes += Inventory.count(ItemID.BLOOD_RUNE);
        if (Equipment.contains(ItemID.STAFF_OF_WATER) && castRunes == 0) {
            Logger.info("Leaving wild no mind/blood runes");
            return true;
        }
        if (settings.staffMode == StaffMode.TRIDENT && magicLvl >= 75 && !Equipment.contains(ItemVariants.TRIDENT.getIds())) {
            Logger.info("Leaving wild No trident");
            return true;
        }
        if (settings.staffMode == StaffMode.SCEPTRE && magicLvl >= 75 && !Equipment.contains(ItemVariants.SCEPTRE.getIds())) {
            Logger.info("Leaving wild to get sceptre");
            return true;
        }

        return false;
    };

    public ExitDragon() {
        super(cond);
//        super(() -> Combat.isInWild() && (!Inventory.contains(ItemID.JUG_OF_WINE)
//                || RechargeSceptre.shouldRecharge
//                || Inventory.contains(ItemID.ONYX_BOLT_TIPS) || Equipment.contains(ItemID.UNCHARGED_TRIDENT) || Inventory.contains(ItemID.DRACONIC_VISAGE)
//                || ((Inventory.count(ItemID.AIR_RUNE) < 5 || (Inventory.count(ItemID.MIND_RUNE) + (Skills.getRealLevel(Skill.MAGIC) >= 75 ? Inventory.count(ItemID.BLOOD_RUNE) : 0)) < 1)
//                && (!Equipment.contains(ItemVariants.TRIDENT.getIds()) && !Equipment.contains(ItemVariants.SCEPTRE.getIds())))
//        ));
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().isHealthBarVisible() && !Walking.isRunEnabled() && Walking.getRunEnergy() > 5) {
            Walking.toggleRun();
        }

        if (!WILDY_EXIT.contains(Players.getLocal())) {
            if (Walking.shouldWalk(8)) Walking.walk(WILDY_EXIT.getRandomTile());
            return ReactionGenerator.getQuick();
        }
//      if (Walking.shouldWalk(6)) Walking.walk(BankLocation.GRAND_EXCHANGE.getTile());
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
}
