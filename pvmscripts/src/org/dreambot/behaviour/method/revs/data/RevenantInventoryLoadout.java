package org.dreambot.behaviour.method.revs.data;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.revs.behaviour.RevenantTeleportStrategy;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.scriptdata.RevenantSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;

public enum RevenantInventoryLoadout {
    MANTAS_RESTORE(
            new InventoryLoadout()
                    .addItem(ItemID.KNIFE).setRefill(5)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.REVENANT_CAVE_TELEPORT)
                    .setRefill(55)
                    // todo setting for if cave teleports should be taken
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.REVENANT_CAVE_TELEPORT && !Combat.isInWild())
                    .addItem(ItemVariants.BURNING_AMULET)
                    .setEnabledCondition(() -> !Combat.isInWild() && SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.BURNING_NECKLACE)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(25)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(300)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
    ),
    TANK(
            new InventoryLoadout()
                    .addItem(ItemID.KNIFE).setRefill(5)
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.REVENANT_CAVE_TELEPORT)
                    .setRefill(55)
                    // todo setting for if cave teleports should be taken
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.REVENANT_CAVE_TELEPORT && !Combat.isInWild())
                    .addItem(ItemVariants.BURNING_AMULET)
                    .setEnabledCondition(() -> !Combat.isInWild() && SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.BURNING_NECKLACE)
                    .addItem(ItemID.SARADOMIN_BREW4, 4)
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(25)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 10)
                    .setRefill(300)
                    .addItem(ItemID.BLIGHTED_KARAMBWAN, 6)
                    .setRefill(300)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
    ),
    FIRE_SPELLS(
            new InventoryLoadout()
                    .addItem(ItemID.KNIFE).setRefill(5)
                    .addItem(ItemVariants.BURNING_AMULET)
                    .setEnabledCondition(() -> !Combat.isInWild() && SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.BURNING_NECKLACE)
                    .addItem(ItemVariants.RING_OF_DUELING)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useAvarice)
                    .addItem(ItemID.REVENANT_CAVE_TELEPORT)
                    .setRefill(55)
                    // todo setting for if cave teleports should be taken
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).teleportStrategy == RevenantTeleportStrategy.REVENANT_CAVE_TELEPORT && !Combat.isInWild())
                    .addItem(ItemVariants.LOOTING_BAG)
                    .setEnabledCondition(() -> SettingsRepository.findInstanceOf(new RevenantSettings()).useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))
                    .addItem(ItemID.BLIGHTED_SUPER_RESTORE4, 3, 3)
                    .setRefill(25)
                    .addItem(ItemID.BLIGHTED_MANTA_RAY, 16)
                    .setRefill(300)
                    .addItem(ItemID.MIND_RUNE, 400)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 35)
                    .addItem(ItemID.CHAOS_RUNE, 200)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 35 && Skills.getRealLevel(Skill.MAGIC) < 59)
                    .addItem(ItemID.DEATH_RUNE, 200)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 59 && Skills.getRealLevel(Skill.MAGIC) < 75)
                    .addItem(ItemID.BLOOD_RUNE, 200)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75)
                    .addItem(ItemID.AIR_RUNE, 6, 1000).setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 13)
                    .setStrict(true)
    )
    // todo anti pk jaunt with entangles
    ;

    public final InventoryLoadout loadout;

    RevenantInventoryLoadout(InventoryLoadout loadout) {
        this.loadout = loadout;
    }
}
