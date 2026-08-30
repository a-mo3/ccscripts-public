package org.dreambot.behaviour.method.pirates;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.Arrays;

public enum PirateEquipmentLoadout {
    DHIDE_DARTS(new EquipmentLoadout(PirateLoadoutBases.dhideBase)
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.RUNE_DART, 100, 200))
            .setRefill(1500)
            // todo dart reqs

            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
            .setEnabledCondition(() -> OwnedItems.containsAny(
                    Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())),
            Skill.RANGED
    ),

    DHIDE_ROSEWOOD_BLOWPIPE(new EquipmentLoadout(PirateLoadoutBases.dhideBase)
            .addItem(EquipmentSlot.WEAPON, ItemVariants.ROSEWOOD_BLOWPIPE)

            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
            .setEnabledCondition(() -> OwnedItems.containsAny(
                    Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())),
            Skill.RANGED
    ),

    MSB_ADDY(new EquipmentLoadout(PirateLoadoutBases.dhideBase)
            .addItem(EquipmentSlot.WEAPON, new EquipmentLoadoutItem(ItemID.MAGIC_SHORTBOW))


            .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.ADAMANT_ARROW, 1, 150))

            .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS)
            .setEnabledCondition(() -> OwnedItems.containsAny(
                    Arrays.stream(ItemVariants.AVAS.getIds()).mapToInt(x -> x).toArray())),
            Skill.RANGED
    ),

    CHUDGEL_MONK(new EquipmentLoadout()
            // legs
            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.AMULET, ItemVariants.BURNING_AMULET)
            .setRefill(10)
            // todo add salve
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(10)
            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)

            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)

            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.WEAPON, ItemID.SARACHNIS_CUDGEL),
            Skill.ATTACK
    ),

    URSINE_MONK(new EquipmentLoadout()
            // legs
            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.AMULET, ItemVariants.BURNING_AMULET)
            .setRefill(10)
            // todo add salve
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(10)
            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)

            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)

            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.WEAPON, ItemVariants.URSINE_CHAINMACE),
            Skill.ATTACK
    ),
    VIGGORA_MONK(new EquipmentLoadout()
            // legs
            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
            .addItem(EquipmentSlot.AMULET, ItemVariants.BURNING_AMULET)
            .setRefill(10)
            // todo add salve
            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(10)
            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)

            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
            .addItem(EquipmentSlot.WEAPON, ItemVariants.VIGGORA_CHAINMACE),
            Skill.ATTACK
    ),


    SCIMS_THEN_WHIP(new EquipmentLoadout()
            // legs
            .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
            .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)

            .addItem(EquipmentSlot.AMULET, ItemVariants.BURNING_AMULET)
            .setRefill(10)
            .addItem(EquipmentSlot.AMULET, ItemVariants.SALVE_AMULET)
            .setEnabledCondition(() -> OwnedItems.contains(ItemVariants.SALVE_AMULET))

            .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
            .setRefill(10)

            .addItem(EquipmentSlot.HAT, ItemID.SNAKESKIN_BANDANA)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
            .setRefill(5)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)

            .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
            .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
            .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
            .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
            .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
            .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
            .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
            .addItem(EquipmentSlot.WEAPON, ItemID.DRAGON_SCIMITAR)
            .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 60 && PaidQuest.MONKEY_MADNESS_I.isFinished())
            .addItem(EquipmentSlot.WEAPON, ItemID.SARADOMIN_SWORD)
            .setEnabledCondition(() -> Skills.getRealLevel(Skill.ATTACK) >= 70),
            Skill.ATTACK
    );

    public final EquipmentLoadout loadout;
    public final Skill mode;

    PirateEquipmentLoadout(EquipmentLoadout loadout, Skill mode) {
        this.loadout = loadout;
        this.mode = mode;
    }
}
