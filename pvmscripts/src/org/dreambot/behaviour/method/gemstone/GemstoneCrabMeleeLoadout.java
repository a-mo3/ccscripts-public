package org.dreambot.behaviour.method.gemstone;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.training.slayer.Helper;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

// gemstone starts from lvl 3
public enum GemstoneCrabMeleeLoadout {
    SCIMITARS(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)

                    // defence doesnt really matter because it has chicken stats
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
    ),
    OBBY_SARA(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)

                    // defence doesnt really matter because it has chicken stats
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)
    ),
    DHAROKS(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.DWARVEN_ROCK_CAKE_7510).setEnabledCondition(GemstoneCrabMeleeLoadout::unlockedDharoks)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)

                    // defence doesnt really matter because it has chicken stats
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 60)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemVariants.DHAROK_GREATAXE).setEnabledCondition(GemstoneCrabMeleeLoadout::unlockedDharoks)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.DHAROK_CHEST).setEnabledCondition(GemstoneCrabMeleeLoadout::unlockedDharoks)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.DHAROK_LEGS).setEnabledCondition(GemstoneCrabMeleeLoadout::unlockedDharoks)
                    .addItem(EquipmentSlot.HAT, ItemVariants.DHAROK_HELM).setEnabledCondition(GemstoneCrabMeleeLoadout::unlockedDharoks)
    ),
    SCIMITARS_NO_POTION(
            new InventoryLoadout()
                    .addItem(ItemID.SHARK, 1, 22)
                    .setRefill(200)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)

                    // defence doesnt really matter because it has chicken stats
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
    ),
    SCIMITARS_COMBAT_POTION(
            new InventoryLoadout()
                    .addItem(ItemVariants.COMBAT_POTION, 1, 20)
                    .addItem(ItemID.SHARK, 2)
                    .addItem(ItemID.COINS_995, 1000, 5000),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)

                    // defence doesnt really matter because it has chicken stats
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT).setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR).setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
    ),
    ;


    public static boolean unlockedDharoks() {
        return Skill.ATTACK.getLevel() >= 70 && Skill.STRENGTH.getLevel() >= 70;
    }

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;

    GemstoneCrabMeleeLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
    }
}
