package org.dreambot.behaviour.method.barrows;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.listener.PaintListener;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BarrowsLoadout {
    TWINFLAME_AIR_MAGIC_ONLY(
            new InventoryLoadout()
                    .addItem(ItemVariants.RING_OF_WEALTH)
                    .setRefill(5)
                    .addItem(ItemID.SPADE)
                    .addItem(ItemID.SHARK, 12)
                    .addItem(ItemID.PRAYER_POTION4, 5)
                    .setRefill(25)

                    .addItem(ItemID.STRANGE_ICON).enabledIfOwned()

                    .addItem(ItemID.AIR_RUNE, 1000, 3000)

                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 17)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)

                    .addItem(ItemID.BARROWS_TELEPORT, 1, 5)
                    .setRefill(30)
                    .setEnabledCondition(() -> Players.getLocal().getX() < 3500 && Players.getLocal().getY() < 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.ZAMORAK_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_ZAMORAK_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLOODBARK_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.TWINFLAME_STAFF)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60)
            ,
            Collections.emptyList(),
            Collections.emptyList()
    ),

    AIR_MSB(
            new InventoryLoadout()
                    // range switch
                    .addItem(ItemID.MAGIC_SHORTBOW)
                    .addItem(ItemVariants.AVAS)

                    .addItem(ItemVariants.RING_OF_WEALTH)
                    .setRefill(5)
                    .addItem(ItemID.SPADE)
                    .addItem(ItemID.SHARK, 6, 11)
                    .addItem(ItemID.PRAYER_POTION4, 3, 6)
                    .setRefill(25)

                    .addItem(ItemID.STRANGE_ICON).enabledIfOwned()

                    .addItem(ItemID.AIR_RUNE, 1000, 3000)

                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 17)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)

                    .addItem(ItemID.BARROWS_TELEPORT, 1, 5)
                    .setRefill(30)
                    // Y check is for when you have already looted chest so forceRestock, but are in crypt, without a teleport, and should restock
                    .setEnabledCondition(() -> Players.getLocal().getX() < 3500 && Players.getLocal().getY() < 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.ARROWS, ItemID.RUNE_ARROW, 200, 400)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.ZAMORAK_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_ZAMORAK_CAPE).enabledIfOwned()


                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLOODBARK_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR),
            Arrays.asList(ItemID.MAGIC_SHORTBOW, ItemID.AVAS_ACCUMULATOR, ItemID.AVAS_ASSEMBLER, ItemID.AVAS_ATTRACTOR),
            Arrays.asList(
                    ItemID.GUTHIX_CAPE,
                    ItemID.SARADOMIN_CAPE,
                    ItemID.ZAMORAK_CAPE,
                    ItemID.IMBUED_GUTHIX_CAPE,
                    ItemID.IMBUED_SARADOMIN_CAPE,
                    ItemID.IMBUED_ZAMORAK_CAPE,
                    ItemID.FIRE_CAPE, ItemID.GUTHIX_CAPE, ItemID.OBSIDIAN_CAPE, ItemID.STAFF_OF_AIR
            )
    ),

    AIR_MAGIC_ONLY(
            new InventoryLoadout()
                    .addItem(ItemVariants.RING_OF_WEALTH)
                    .setRefill(5)
                    .addItem(ItemID.SPADE)
                    .addItem(ItemID.SHARK, 12)
                    .addItem(ItemID.PRAYER_POTION4, 5)
                    .setRefill(25)

                    .addItem(ItemID.STRANGE_ICON).enabledIfOwned()

                    .addItem(ItemID.AIR_RUNE, 1000, 3000)

                    // cast runes, these bring runes 1 level before they're needed so on level up it can be switched
                    .addItem(ItemID.MIND_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 17)
                    .addItem(ItemID.CHAOS_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 22 && Skills.getRealLevel(Skill.MAGIC) < 41)
                    .addItem(ItemID.DEATH_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.MAGIC) < 62)
                    .addItem(ItemID.BLOOD_RUNE, 100, 1600)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 61)

                    .addItem(ItemID.BARROWS_TELEPORT, 1, 5)
                    .setRefill(30)
                    .setEnabledCondition(() -> Players.getLocal().getX() < 3500 && Players.getLocal().getY() < 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.MYSTIC_HAT)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.HAT, ItemID.BLOODBARK_HELM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.ZAMORAK_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_GUTHIX_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_SARADOMIN_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.IMBUED_ZAMORAK_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.MYSTIC_ROBE_TOP)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLOODBARK_BODY)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.MYSTIC_ROBE_BOTTOM)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 40 && Skills.getRealLevel(Skill.DEFENCE) >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLOODBARK_LEGS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING).enabledIfOwned()
                    .addItem(EquipmentSlot.RING, ItemID.SEERS_RING_I).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_AIR)
            ,
            Collections.emptyList(),
            Collections.emptyList()
    ),
    ;

    final InventoryLoadout inventoryLoadout;
    final EquipmentLoadout equipmentLoadout;
    final List<Integer> rangeSwitch;
    final List<Integer> mageSwitch;

    BarrowsLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, List<Integer> rangeSwitch, List<Integer> mageSwitch) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.rangeSwitch = rangeSwitch;
        this.mageSwitch = mageSwitch;
    }

    public void doMageSwitch() {
        for (int id : mageSwitch) {
            if (!Equipment.contains(id) && Inventory.contains(id)) Inventory.interact(id);
        }
    }

    public void doRangeSwitch() {
        for (int id : rangeSwitch) {
            if (!Equipment.contains(id) && Inventory.contains(id)) Inventory.interact(id);
        }
    }
}
