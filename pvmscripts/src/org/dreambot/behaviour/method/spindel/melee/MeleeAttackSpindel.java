package org.dreambot.behaviour.method.spindel.melee;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.behaviour.method.spindel.GoToSpindel;
import org.dreambot.behaviour.method.spindel.SpindelAntiPk;
import org.dreambot.behaviour.method.spindel.SpindelData;
import org.dreambot.behaviour.method.spindel.range.RangeAttackSpindel;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.LootingBag;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.fractals.util.UtilProvider;
import org.dreambot.scriptdata.SpindelSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * literally just hit it
 */
public class MeleeAttackSpindel extends Fractal {
    @Override
    public int onLoop() {
        UtilProvider.staminaUp();
        if (!RangeAttackSpindel.SPINDEL_ARENA.contains(Players.getLocal())) return ReactionGenerator.getQuick();
        NPC spindel = NPCs.closest(SpindelData.SPINDEL_ID);

        if (spindel == null) {
            PrayerUtils.disable(Prayer.PROTECT_FROM_MISSILES, Prayer.PROTECT_FROM_MAGIC, Prayer.AUGURY, Prayer.EAGLE_EYE, Prayer.PROTECT_FROM_MELEE, Prayer.PIETY, Prayer.ULTIMATE_STRENGTH);
            return ReactionGenerator.getQuick();
        }

        if (SettingsRepository.findInstanceOf(new SpindelSettings()).boostPray) {
            if (!Prayers.isActive(getBestMeleePray())) Prayers.toggle(true, getBestMeleePray());
        }
        // correct prayer
        MeleeSpindelBranch.prayCorrectly();

        // if you have darts equipped from spiderlings, equip whatever melee weapon you are using
        if (Equipment.contains(ItemID.ADAMANT_DART)) {
            Logger.info("Equip chainmace");
            Item chainMace = ItemVariants.URSINE_CHAINMACE.getItem();
            if (chainMace == null) chainMace = ItemVariants.VIGGORA_CHAINMACE.getItem();
            if (chainMace == null) chainMace = Inventory.get(ItemID.SARACHNIS_CUDGEL);
            if (chainMace != null) chainMace.interact();
            return ReactionGenerator.getQuick();
        }

        // hit spindel
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
                return SpindelAntiPk.walkOut();
            }

            if (Combat.getSpecialPercentage() > 50) {
                Combat.toggleSpecialAttack(true);
            }
            spindel.interact("Attack");
        }
        LootingBag.refreshLootBagCache();
        return ReactionGenerator.getQuick();
    }

    public static Prayer getBestMeleePray() {
        int lvl = Skills.getRealLevel(Skill.PRAYER);
        // im not sure if finished is enough to unlock but yolo
        if (lvl >= 70 && Skills.getRealLevel(Skill.DEFENCE) >= 70 && PlayerSettings.getBitValue(3909) == 8)
            return Prayer.PIETY;
        return Prayer.ULTIMATE_STRENGTH;
    }
}
