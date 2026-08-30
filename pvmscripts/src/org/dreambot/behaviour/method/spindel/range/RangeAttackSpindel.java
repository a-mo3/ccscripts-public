package org.dreambot.behaviour.method.spindel.range;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.spindel.*;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.scriptdata.SpindelSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * manage prayers and attack spindel
 */
public class RangeAttackSpindel extends Fractal {
    public static final Area SPINDEL_ARENA = new Area(1617, 11567, 1645, 11528, 2);

    @Override
    public int onLoop() {
        if (!SPINDEL_ARENA.contains(Players.getLocal())) return ReactionGenerator.getQuick();

        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);

        if (spindel != null) {
            if (SettingsRepository.findInstanceOf(new SpindelSettings()).boostPray) {
                if (!Prayers.isActive(getBestRangePray())) Prayers.toggle(true, getBestRangePray());
            }

            if (RangeSpindelBranch.getPhase() == SpindelPhase.RANGE_BENIGN || RangeSpindelBranch.getPhase() == SpindelPhase.RANGE_SPECIAL) {
                if (!Prayers.isActive(Prayer.PROTECT_FROM_MISSILES)) Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
            } else {
                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC)) Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
            }
        } else {
            PrayerUtils.disable(Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC, Prayer.AUGURY, Prayer.EAGLE_EYE, Prayer.PROTECT_FROM_MELEE, Prayer.PIETY, Prayer.ULTIMATE_STRENGTH);
        }

        Character interactingWith = Players.getLocal().getInteractingCharacter();
        if (interactingWith != null && interactingWith.getId() == SpindelData.SPINDEL_ID)
            return ReactionGenerator.getQuick();

        if (spindel != null) {
            Character fightingSpindel = spindel.getCharacterInteractingWithMe();
            Logger.info("Attacking spindel: " + fightingSpindel);
            if (fightingSpindel != null && !fightingSpindel.getName().equals(Players.getLocal().getName())) {
                // someone else is fighting spindel
                if (SettingsRepository.findInstanceOf(new SpindelSettings()).crash) {
                    // just dont bother attacking
                    Logger.info("Crash this guy");
                    return ReactionGenerator.getQuick();
                }

                // leave
                GoToSpindel.shouldHop = true;
                AntiCrashWildyBosses.hasToLeave = true;
                return SpindelAntiPk.leaveSpindel();
            }

            if (Combat.getSpecialPercentage() > 50) {
                Combat.toggleSpecialAttack(true);
            }

            spindel.interact("Attack");
        }

        LootingBag.refreshLootBagCache();
        return ReactionGenerator.getQuick();
    }

    public static final int RIGOUR_UNLOCKED = 5451;

    public static Prayer getBestRangePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        if (lvl >= 74 && PlayerSettings.getBitValue(RIGOUR_UNLOCKED) == 1) return Prayer.RIGOUR;
        return Prayer.EAGLE_EYE;
    }
}
