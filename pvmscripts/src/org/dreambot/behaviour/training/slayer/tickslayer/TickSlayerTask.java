package org.dreambot.behaviour.training.slayer.tickslayer;

import lombok.Builder;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.misc.tickcombat.decisions.*;
import org.dreambot.behaviour.training.slayer.SlayerMode;
import org.dreambot.behaviour.training.slayer.SlayerSettings;
import org.dreambot.fractals.TickFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariant;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Builder
public class TickSlayerTask extends TickFractal {
    public static SlayerMode mode;
    final SlayerSettings settings;
    final Area area;
    final Filter<NPC> mobFilter;
    final Supplier<Prayer[]> prayers;
    boolean flickPrayers;
    HashMap<ItemVariant, BooleanSupplier> potions = new HashMap<>();
    Supplier<GroundItem> lootSupplier;
    // true if blocking false otherwise, add some extra logic to the attack branch to gap or safespot or whatever.
    BooleanSupplier extraFightLogic;
    BooleanSupplier extraWalkLogic;
    InventoryLoadout inventoryLoadout;
    EquipmentLoadout equipmentLoadout;

    static class TickSlayerTaskBuilder {
        public TickSlayerTaskBuilder() {
            if (potions == null) potions = new HashMap<>();
            potions.put(ItemVariants.PRAYER_POTION, () -> Skill.PRAYER.getBoostedLevel() < 1);
            potions.put(ItemVariants.DIVINE_SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);
            potions.put(ItemVariants.SUPER_COMBAT_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);
            potions.put(ItemVariants.STRENGTH_POTION, () -> Skill.STRENGTH.getBoostedLevel() - Skill.STRENGTH.getLevel() < 3);
            potions.put(ItemVariants.BASTION_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
            potions.put(ItemVariants.RANGE_POTION, () -> Skill.RANGED.getBoostedLevel() - Skill.RANGED.getLevel() < 3);
        }

        public TickSlayerTask build() {
            TickSlayerTask task = new TickSlayerTask(settings, area, mobFilter, prayers, flickPrayers, potions,
                    lootSupplier, extraFightLogic, extraWalkLogic, inventoryLoadout, equipmentLoadout);

            task.setInventoryLoadout(inventoryLoadout);
            task.setEquipmentLoadout(equipmentLoadout);

            if (settings == null) settings = SettingsRepository.getSetting("slayer", new SlayerSettings());

            task.addChildren(
                    // configure qp
                    new TickConfigureQuickPrayers(() -> {
                        if (prayers == null) return null;
                        // add the boost prayers to the prayer array
                        if (mode == null) return null;
                        Prayer boost = mode.getBoostPrayers().get();
                        Prayer[] overheads = prayers.get();
                        if (boost == null) return overheads;
                        Prayer[] combined = Arrays.copyOf(overheads, overheads.length + 1);
                        combined[combined.length - 1] = boost;
                        return combined;
                    }),
                    // go to area
                    new TickGoToArea(area, extraWalkLogic),

                    new TickDrinkPotions(potions),

                    new TickFlickPray(settings.isFlickPrayer()).setDisable(prayers == null),

                    new GenericTickEat(),

                    // loot
                    new TickLoot(lootSupplier).setSimpleName("Loot"),

                    // fight
                    new TickAttackMob(mobFilter, extraFightLogic).setSimpleName("Fight")
            );
            return task;
        }

    }

    public static int getSlayerTaskKey() {
        return PlayerSettings.getConfig(395);
    }
}
