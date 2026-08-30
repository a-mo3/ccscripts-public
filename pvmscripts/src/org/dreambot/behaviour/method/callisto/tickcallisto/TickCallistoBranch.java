package org.dreambot.behaviour.method.callisto.tickcallisto;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.method.callisto.CallistoData;
import org.dreambot.behaviour.misc.tickcombat.decisions.GenericTickEat;
import org.dreambot.behaviour.misc.tickcombat.decisions.TickDrinkPotions;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.CallistoSettings;

import java.util.HashMap;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Callisto fight but using tick decisions for more optimal gameplay.
 */
public class TickCallistoBranch extends TickFractal {
    public TickCallistoBranch(Supplier<Boolean> acceptCondition, CallistoSettings settings) {
        super(acceptCondition);

        HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
        potions.put(ItemVariants.BLIGHTED_SUPER_RESTORE, () -> Skill.PRAYER.getBoostedLevel() < 10);
        potions.put(ItemVariants.STAMINA_POTION, () -> Walking.getRunEnergy() < 5);
        potions.put(ItemVariants.ANTI_DOTE_PP, () -> Combat.isPoisoned() || Combat.isEnvenomed());
        potions.put(ItemVariants.MAGIC_POTION, () -> Skill.MAGIC.getBoostedLevel() - Skill.MAGIC.getLevel() < 3);

        NPC call = NPCs.closest(CallistoData.CALLISTO_NAME);
        this.paintArraySupplier = () -> new String[]{

        };

        addChildren(
                new CallistoTickPrayer(settings.flickPrayers),
//                new VetionTickEat(),
                new TickDrinkPotions(potions),
//                new CallistoTickEat(),
                new GenericTickEat(),
//                new VetionTickPotion(),
//                new VetionTickLightning(),
                new CallistoTickAttack(),
                new CallistoGetGap(),
                new CallistoTickLoot(settings).setSimpleName("Loot")
        );
    }
}
