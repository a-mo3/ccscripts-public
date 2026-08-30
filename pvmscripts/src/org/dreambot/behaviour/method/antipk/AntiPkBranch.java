package org.dreambot.behaviour.method.antipk;

import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.behaviour.method.spindel.SpindelDrinkPrayer;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatUtil;
import org.dreambot.fractals.util.PrayerUtils;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public class AntiPkBranch extends Fractal implements ConfigurableFractal<AntiPKSettings> {
    // counts how many times a pker has been marked so the survival percentage can be tracked
    @Setter
    @Getter
    private static int pkEventCounter = 0;
    @Setter
    @Getter
    private static String attackerName = null;
    // manage teleblock state


    public AntiPkBranch() {
        super(() -> attackerName != null);

//        spotAnimationTimings.put(179, 14400L); // entangle, 14.4 seconds
//        spotAnimationTimings.put(180, 9600L); // snare 9.6 seconds
//        spotAnimationTimings.put(181, 4800L); // bind 4.8 seconds

        Client.getInstance().addEventListener(this);

        paintArraySupplier = () -> new String[]{
                "TB world " + CombatUtil.get().getTeleblockedWorld(),
                "isTB'ed " + CombatUtil.get().isTeleblocked(),
                "Entangled? " + CombatUtil.get().isEntangled(),
                "Entangled time left " + (CombatUtil.get().isEntangled() ? CombatUtil.get().msLeftOnEntangle() : "-"),
                "Combat " + CombatUtil.get().isInCombat(),
                "is on tb timer " + CombatUtil.get().isOnSpecialTPTimer(),
                "Pker " + attackerName
        };


        setSimpleName("Experimental AntiPK");
        addChildren(
                // hop world if you are not in combat and TB'd or not in wildy
                new AntiPkWorldHop(() -> (!CombatUtil.get().isInCombat() && CombatUtil.get().getTeleblockedWorld() == Worlds.getCurrentWorld()) || !Combat.isInWild(),
                        getSettings().reportPkers)
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1 && Skills.getRealLevel(Skill.PRAYER) >= 43) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                                if (!Prayers.isActive(Prayer.PROTECT_ITEM))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_ITEM);
                            }
                            return false;
                        })
                        .setSimpleName("World hop"),

                // tp - should be redundant after combatutils enables / disables teleports & inventory teleports are 1click
//                new AntiPkTeleport(() -> !CombatUtil.get().isOnSpecialTPTimer() && Combat.isInWild()
//                        && !CombatUtil.get().isTeleblocked() && Combat.getWildernessLevel() <= 30)
//                        .setPrependLogic(() -> {
//                            if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC) && Skills.getBoostedLevel(Skill.PRAYER) > 1)
//                                Prayers.toggle(true, Prayer.PROTECT_FROM_MAGIC);
//                            if (!Prayers.isActive(Prayer.PROTECT_ITEM) && Skills.getBoostedLevel(Skill.PRAYER) > 1)
//                                Prayers.toggle(true, Prayer.PROTECT_ITEM);
//                            return false;
//                        })
//                        .setSimpleName("Teleport out"),

                // eat & brew
                new AntiPkEating()
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1 && Skills.getRealLevel(Skill.PRAYER) >= 43) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                                if (!Prayers.isActive(Prayer.PROTECT_ITEM))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_ITEM);
                            }
                            return false;
                        })
                        .setSimpleName("Tank"),
                new SpindelDrinkPrayer(() -> ItemVariants.BLIGHTED_SUPER_RESTORE.getItem() != null
                        && Skills.getRealLevel(Skill.PRAYER) - Skills.getBoostedLevel(Skill.PRAYER) > 10)
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1 && Skills.getRealLevel(Skill.PRAYER) >= 43) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                                if (!Prayers.isActive(Prayer.PROTECT_ITEM))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_ITEM);
                            }
                            return false;
                        })
                        .setSimpleName("Drink prayer"),

                // entangle enemy if you are entangled and about to be set free
                // null check on best root implicitly is a check for anti pk loadout selected
                new AntiPkFightBack(() -> AntiPkFightBack.bestRootSpell() != null
                        && Inventory.contains(ItemID.BLIGHTED_ENTANGLE_SACK)
                        && (CombatUtil.get().isEntangled() && CombatUtil.get().msLeftOnEntangle() < 3200))
                        .setSimpleName("Entangle")
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1 && Skills.getRealLevel(Skill.PRAYER) >= 43) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                                if (!Prayers.isActive(Prayer.PROTECT_ITEM))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_ITEM);
                            }
                            return false;
                        }),

                // run
                new AntiPkLeaveBosses()
                        .setPrependLogic(() -> {
                            if (Skills.getBoostedLevel(Skill.PRAYER) > 1 && Skills.getRealLevel(Skill.PRAYER) >= 43) {
                                if (!Prayers.isActive(Prayer.PROTECT_FROM_MAGIC))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_FROM_MAGIC);
                                if (!Prayers.isActive(Prayer.PROTECT_ITEM))
                                    PrayerUtils.toggle(true, Prayer.PROTECT_ITEM);
                            }
                            return false;
                        })
                        .setSimpleName("Run away")
        );
    }

    @Override
    public AntiPKSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new AntiPKSettings());
    }

    @Override
    public String settingName() {
        return "antiPK";
    }
}
