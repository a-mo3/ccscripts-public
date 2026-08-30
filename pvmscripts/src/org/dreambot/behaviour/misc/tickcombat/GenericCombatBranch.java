package org.dreambot.behaviour.misc.tickcombat;

import lombok.Builder;
import lombok.Setter;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.behaviour.misc.tickcombat.decisions.*;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.PVMUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * like standard combat but with the tick decisions so we can 1t flick
 */
@Builder
public class GenericCombatBranch extends TickFractal {
    final Area area;
    final Filter<NPC> mobFilter;
    boolean flickPrayers = true;
    boolean disablePrayer;
    final Supplier<Prayer[]> prayers;
    HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
    Supplier<GroundItem> lootSupplier;
    Filter<GroundItem> lootFilter;
    // true if blocking false otherwise, add some extra logic to the attack branch to gap or safespot or whatever.
    BooleanSupplier extraFightLogic;
    Supplier<Item> dropSupplier = () -> PVMUtil.getCheapest();

    final CombatStyle style;
    final Supplier<CombatStyle> styleSupplier;

    public GenericCombatBranch init() {
        addChildren(
                // configure qp
                new TickConfigureQuickPrayers(prayers),
                // go to area
                new TickGoToArea(area),

                // todo potions
                new TickDrinkPotions(potions),

                // pray flick
                new TickFlickPray(flickPrayers).setDisable(disablePrayer),

                // eat
                new GenericTickEat().setSimpleName("GTEat"),

                // loot
                new TickLoot(lootSupplier, lootFilter, dropSupplier).setSimpleName("Loot"),

                new TickSetCombatStyle(style, styleSupplier).setSimpleName("Set style"),

                // fight
                new TickAttackMob(mobFilter, extraFightLogic).setSimpleName("Fight")


        );
        return this;
    }

    public static class GenericCombatBranchBuilder {
        public GenericCombatBranchBuilder addPotion(ItemVariant potion, BooleanSupplier cond) {
            if (potions == null) potions = new HashMap<>();
            potions.put(potion, cond);
            return this;
        }
    }
}
