package org.dreambot.behaviour.method.corp;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.behaviour.method.corp.messages.CorpRole;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;

public enum CorpLoadout {
    MELEE_OBY(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.PRAYER_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(ItemID.DRAGON_WARHAMMER)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DRAGON_WARHAMMER))
                    .addItem(ItemID.BANDOS_GODSWORD)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.BANDOS_GODSWORD))
                    .addItem(ItemID.COOKED_KARAMBWAN, 10, 10).setRefill(500)
                    .addItem(ItemID.SHARK, 10, 11).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(50)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    // oby gear
                    .addItem(EquipmentSlot.LEGS, ItemID.OBSIDIAN_PLATELEGS)
                    .addItem(EquipmentSlot.CHEST, ItemID.OBSIDIAN_PLATEBODY)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 60)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ZAMORAKIAN_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            Skill.ATTACK
    ),
    MELEE_KARILS(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.PRAYER_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(ItemID.DRAGON_WARHAMMER)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DRAGON_WARHAMMER))
                    .addItem(ItemID.BANDOS_GODSWORD)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.BANDOS_GODSWORD))
                    .addItem(ItemID.COOKED_KARAMBWAN, 10, 10).setRefill(500)
                    .addItem(ItemID.SHARK, 10, 11).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(50)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    // oby gear
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 60)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ZAMORAKIAN_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            Skill.ATTACK
    ),
    MELEE_KARILS_FANG(
            new InventoryLoadout()
                    .addItem(ItemVariants.DIVINE_SUPER_COMBAT_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.PRAYER_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(ItemID.DRAGON_WARHAMMER)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DRAGON_WARHAMMER))
                    .addItem(ItemID.BANDOS_GODSWORD)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.BANDOS_GODSWORD))
                    .addItem(ItemID.COOKED_KARAMBWAN, 10, 10).setRefill(500)
                    .addItem(ItemID.SHARK, 10, 11).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(50)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    // oby gear
                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .addItem(EquipmentSlot.HAT, ItemID.OBSIDIAN_HELMET)
                    .addItem(EquipmentSlot.HAT, ItemID.HELM_OF_NEITIZNOT).setEnabledCondition(PaidQuest.THE_FREMENNIK_ISLES::isFinished)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)

                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 60)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ZAMORAKIAN_SPEAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)
                    .addItem(EquipmentSlot.WEAPON, ItemID.OSMUMTENS_FANG)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 82)

                    .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned()

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned(),
            Skill.ATTACK
    ),

    RANGE_D_HIDE(
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGE_POTION)
                    .setRefill(50)
                    .addItem(ItemVariants.PRAYER_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(ItemID.DARK_BOW)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DARK_BOW))
                    .addItem(ItemID.RUNE_ARROW, 100)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DARK_BOW))
                    .addItem(ItemID.COOKED_KARAMBWAN, 10, 10).setRefill(500)
                    .addItem(ItemID.SHARK, 10, 11).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.SARADOMIN_COIF)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
//                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
//                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30 && Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60 && Skill.DEFENCE.getLevel() >= 50)

                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(50)
                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()
                    .setRefill(50)

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, ItemID.RUBY_BOLTS_E, 250, 1000),
            Skill.RANGED
    ),
    RANGE_KARILS(
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGE_POTION)
                    .setRefill(50)
                    .addItem(ItemVariants.PRAYER_POTION)
                    .setRefill(20)
                    .addItem(ItemVariants.GAMES_NECKLACE)
                    .setRefill(20)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setRefill(20)
                    .addItem(ItemID.DARK_BOW)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DARK_BOW))
                    .addItem(ItemID.RUNE_ARROW, 100)
                    .setEnabledCondition(() -> CorpClient.getRole() == CorpRole.SPECIAL_FORCES || OwnedItems.contains(ItemID.DARK_BOW))
                    .addItem(ItemID.COOKED_KARAMBWAN, 10, 10).setRefill(500)
                    .addItem(ItemID.SHARK, 10, 11).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.HAT, ItemID.SARADOMIN_COIF)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.CHEST, ItemVariants.KARIL_TOP)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.CHEST, ItemID.MIXED_HIDE_TOP)
//                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 50 && Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)
                    .addItem(EquipmentSlot.LEGS, ItemVariants.KARIL_SKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 70 && Skill.RANGED.getLevel() >= 70)
//                    .addItem(EquipmentSlot.LEGS, ItemID.MIXED_HIDE_LEGS)
//                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30 && Skill.RANGED.getLevel() >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60 && Skill.DEFENCE.getLevel() >= 50)

                    .addItem(EquipmentSlot.RING, ItemID.RING_OF_RECOIL)
                    .setRefill(50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()

                    .addItem(EquipmentSlot.SHIELD, ItemID.BLACK_DHIDE_SHIELD)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70 && Skill.DEFENCE.getLevel() >= 40)
                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_CROSSBOW)
                    .addItem(EquipmentSlot.ARROWS, ItemID.RUBY_BOLTS_E, 250, 1000),
            Skill.RANGED
    ),
    ;

    public final InventoryLoadout inventoryLoadout;
    public final EquipmentLoadout equipmentLoadout;
    public final Skill mode;

    CorpLoadout(InventoryLoadout inventoryLoadout, EquipmentLoadout equipmentLoadout, Skill mode) {
        this.inventoryLoadout = inventoryLoadout;
        this.equipmentLoadout = equipmentLoadout;
        this.mode = mode;
    }

    public boolean isFulfilled() {
        return inventoryLoadout.isFulfilled() && equipmentLoadout.isFulfilled();
    }

    public static CorpLoadout forName(String name) {
        return Arrays.stream(values()).filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }
}
