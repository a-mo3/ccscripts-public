package org.dreambot.behaviour.method.bluedragons;

import lombok.Getter;
import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.CombatMode;

@Getter
public enum BlueDragonLoadout {
    MAGIC_WATER(
            new InventoryLoadout()
                    .addItem(ItemID.LOBSTER, 2)
                    .setRefill(100)
                    .addItem(ItemID.PRAYER_POTION4, 2)
                    .setRefill(50)
                    .addItem(ItemID.FALADOR_TELEPORT)
                    .setEnabledCondition(() -> !GoToBlueDragons.TAVERLEY_DUNGEON_START.contains(Players.getLocal()) && !GoToBlueDragons.WHOLE_FALADOR.contains(Players.getLocal()))
                    .setRefill(50)
                    .addItem(ItemVariants.SUMMER_PIE)
                    .setRefill(50)
                    .setEnabledCondition(() -> Skills.getBoostedLevel(Skill.AGILITY) < 70)
                    .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 2)
                    .setRefill(100)
                    // start at bolt because below that you wouldn't be able to use staff
                    // loadout wont be enforced once you start casting so dont need min-max
                    .addItem(ItemID.CHAOS_RUNE, 500) // bolt @ 23 (min 30 for loadout staff)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 47)
                    .addItem(ItemID.DEATH_RUNE, 500) // blast @ 47
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 65, 47))
                    .addItem(ItemID.BLOOD_RUNE, 500) // wave @ 65
                    .setEnabledCondition(() -> CondHelper.skillBetween(Skill.MAGIC, 85, 65))
                    .addItem(ItemID.WRATH_RUNE, 500) // surge
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 85),

            new EquipmentLoadout()
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MIST_BATTLESTAFF)
                    .addItem(EquipmentSlot.FEET, ItemID.INFINITY_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING),
            CombatMode.MAGIC
    ),
    MELEE_ABY_DAGGER(
            new InventoryLoadout()
                    .addItem(ItemID.LOBSTER, 2)
                    .setRefill(100)
                    .addItem(ItemID.PRAYER_POTION4, 2)
                    .setRefill(50)
                    .addItem(ItemID.FALADOR_TELEPORT)
                    .setEnabledCondition(() -> !GoToBlueDragons.TAVERLEY_DUNGEON_START.contains(Players.getLocal()) && !GoToBlueDragons.WHOLE_FALADOR.contains(Players.getLocal()))
                    .setRefill(50)
                    .addItem(ItemVariants.SUMMER_PIE)
                    .setRefill(50)
                    .setEnabledCondition(() -> Skills.getBoostedLevel(Skill.AGILITY) < 70)
                    .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 2)
                    .setRefill(100),

            new EquipmentLoadout()
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_DAGGER)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING),
            CombatMode.MELEE
    ),

    MELEE_DRAGON_SWORD(
            new InventoryLoadout()
                    .addItem(ItemID.LOBSTER, 2)
                    .setRefill(100)
                    .addItem(ItemID.PRAYER_POTION4, 2)
                    .setRefill(50)
                    .addItem(ItemID.FALADOR_TELEPORT)
                    .setEnabledCondition(() -> !GoToBlueDragons.TAVERLEY_DUNGEON_START.contains(Players.getLocal()) && !GoToBlueDragons.WHOLE_FALADOR.contains(Players.getLocal()))
                    .setRefill(50)
                    .addItem(ItemVariants.SUMMER_PIE)
                    .setRefill(50)
                    .setEnabledCondition(() -> Skills.getBoostedLevel(Skill.AGILITY) < 70)
                    .addItem(ItemVariants.ANTI_FIRE_POTION, 1, 2)
                    .setRefill(100),

            new EquipmentLoadout()
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SWORD)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.SHIELD, ItemID.ANTIDRAGON_SHIELD)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING),
            CombatMode.MELEE
    ),
    ;
    private final InventoryLoadout inventoryLoadout;
    private final EquipmentLoadout equipmentLoadout;
    private final CombatMode combatMode;

    BlueDragonLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, CombatMode combatMode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.combatMode = combatMode;
    }
}
