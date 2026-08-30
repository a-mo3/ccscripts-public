package org.dreambot.behaviour.method.tickantipk;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.behaviour.method.vetion.WildernessRunMode;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TickAntiPKBranch extends TickFractal {
    public static boolean lock;

    public TickAntiPKBranch(Supplier<Boolean> acceptCondition, WildernessRunMode runMode, Supplier<List<String>> pkersSupplier) {
        super(acceptCondition);

        setSimpleName("Anti PK " + runMode);

        Supplier<Player> topEnemy = () -> {
            List<String> pkerList = pkersSupplier.get();
            for (String s : pkerList) {
                Player found = Players.closest(s);
                if (found != null && canAttackMe(found)) return found;
            }
            return null;
        };

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 1);
        potions.put(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 1 && Combat.isInWild());
        potions.put(ItemVariants.DIVINE_SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);
        potions.put(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5);
        potions.put(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);

        addChildren(
                new TickAntiPKLock(),
                new TickDrinkPotions(potions)
                        .addPotion(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed()),
                new GenericTickEat().setMinMissingHP(40),
                new TickAntiPKPrayers(),

                // run if its run mode: RUN
                new TickAntiPKEscape(runMode, topEnemy),

                new TickPKEntangleDecision(topEnemy),
                new TickSpecDecision(topEnemy),
                new TickAttackDecision(topEnemy)
        );
    }

    private static boolean canAttackMe(Player threat) {
        if (threat.getName().equals(Players.getLocal().getName())) return false;
        int threatLvl = threat.getLevel();
        int mylvl = Combat.getCombatLevel();
        int wildernessLvl = Combat.getWildernessLevel();
        return threatLvl >= (mylvl - wildernessLvl) && threatLvl <= (wildernessLvl + mylvl);
    }
}
