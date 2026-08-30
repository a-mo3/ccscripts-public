package org.dreambot.behaviour.method.vetion.tickvetion;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickSetCombatIndex;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.VetionSettings;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Vetion fight but using tick decisions for more optimal gameplay.
 */
public class TickVetionBranch extends TickFractal {
    public TickVetionBranch(Supplier<Boolean> acceptCondition, VetionSettings settings) {
        super(acceptCondition);

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 2);
        potions.put(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10);
        potions.put(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5);
        potions.put(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed());
//        potions.put(ItemVariants.ANTI_DOTE, () -> Combat.isPoisoned() || Combat.isEnvenomed());
        potions.put(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);

        addChildren(
                new TickSetCombatIndex()
                        .addWeapon(ItemID.ZOMBIE_AXE, 2)
                        .addWeapon(ItemID.GLACIAL_TEMOTLI, 1)
                        .addWeapon(ItemID.SARADOMIN_SWORD, 2)
                        .addWeapon(ItemID.SARACHNIS_CUDGEL, 1),
                
                new VetionTickPrayer(settings.flickPrayers),
//                new VetionTickEat(),
                new GenericTickEat(),

//                new VetionTickPotion(),
                new TickDrinkPotions(potions),

                new VetionTickLightning(),
                new VetionTickAttack(),
                new VetionTickLoot(settings).setSimpleName("Loot")
        );
    }
}
