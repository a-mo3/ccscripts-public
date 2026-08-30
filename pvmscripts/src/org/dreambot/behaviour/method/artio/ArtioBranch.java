package org.dreambot.behaviour.method.artio;

import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
//import org.dreambot.behaviour.method.calvarion.melee.LootCalvarion;
import org.dreambot.behaviour.method.spindel.AntiCrashWildyBosses;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class ArtioBranch extends Fractal {
    public ArtioBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        setSimpleName("Calvarion");

        addChildren(
                new AntiCrashWildyBosses().setSimpleName("Leave - Crashed"),
//                new ArtioEat(() -> Combat.getHealthPercent() < 67).setSimpleName("Eat"),
                new ArtioDrinkPrayer(() -> Skills.getBoostedLevel(Skill.PRAYER) < 10).setSimpleName("Drink Prayer"),
//                new CalvarionManagePrayers(() -> {
//                    NPC calv = NPCs.closest("Calvar'ion");
//                    return calv != null
//                            && ((SettingsRepository.findInstanceOf(new CalvarionSettings()).boostPray && !Prayers.isActive(CalvarionManagePrayers.getBestMeleePray())
//                            || (Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) == (NPCs.closest(x -> x.getName().contains("hound")) == null)));
//                }).setSimpleName("Manage prayer"),
                new LootArtio().setSimpleName("Loot"),
//                new ArtioBoostPot(() -> Skills.getBoostedLevel(Skill.STRENGTH) < Skills.getRealLevel(Skill.STRENGTH) + 4)
//                        .setSimpleName("Boost"),

                // fight artio
                new MagicFightArtio(() -> true).setSimpleName("Magic fight artio")

        );
    }
    // todo death tracker
    // todo kill tracker
}
