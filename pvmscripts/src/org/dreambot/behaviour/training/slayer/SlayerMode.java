package org.dreambot.behaviour.training.slayer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.dreambot.CondHelper;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.quest.book.FreeQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.behaviour.method.lavadragons.LavaDragonConst;
import org.dreambot.behaviour.misc.CombatLoadouts;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.EquipmentLoadoutItem;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.PVMUtil;
import org.dreambot.scriptdata.LavaDragonSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public enum SlayerMode {
    MELEE(PVMUtil::getBestMeleePray,
            new InventoryLoadout()
                    .addItem(ItemID.SHARK, 1, 22),
            new EquipmentLoadout()
                    // rune, adamant, mithril, iron chestplate
                    .addItem(EquipmentSlot.CHEST, ItemID.IRON_PLATEBODY)
                    .addItem(EquipmentSlot.CHEST, ItemID.MITHRIL_PLATEBODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.CHEST, ItemID.ADAMANT_PLATEBODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.CHEST, ItemID.RUNE_CHAINBODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    // rune adamant mithril iron platelegs
                    .addItem(EquipmentSlot.LEGS, ItemID.IRON_PLATESKIRT)
                    .addItem(EquipmentSlot.LEGS, ItemID.MITHRIL_PLATESKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 20)
                    .addItem(EquipmentSlot.LEGS, ItemID.ADAMANT_PLATESKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 30)
                    .addItem(EquipmentSlot.LEGS, ItemID.RUNE_PLATESKIRT)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40)

                    // dragon sword or rune sword or mithril
                    .addItem(EquipmentSlot.WEAPON, ItemID.IRON_SCIMITAR)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MITHRIL_SCIMITAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ADAMANT_SCIMITAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 30)
                    .addItem(EquipmentSlot.WEAPON, ItemID.RUNE_SCIMITAR)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.ABYSSAL_WHIP)
                    .setEnabledCondition(() -> Skill.ATTACK.getLevel() >= 70)

                    // glory
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(5)
                    .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                    .setRefill(5)

                    .addItem(EquipmentSlot.FEET, ItemID.RUNE_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 40)
                    .addItem(EquipmentSlot.FEET, ItemID.DRAGON_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)

                    .addItem(EquipmentSlot.CAPE, ItemID.OBSIDIAN_CAPE)
                    .addItem(EquipmentSlot.CAPE, ItemID.FIRE_CAPE).enabledIfOwned()
                    .addItem(EquipmentSlot.CAPE, ItemID.INFERNAL_CAPE).enabledIfOwned()

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET)
                    .addItem(EquipmentSlot.HANDS, ItemID.BARROWS_GLOVES).enabledIfOwned()
                    .setRefill(5)

                    // climbing boots
//                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)

                    .addItem(EquipmentSlot.SHIELD, ItemID.TOKTZKETXIL_OBSIDIAN_SHIELD)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) >= 60)
                    // todo shields
//                    .addItem(EquipmentSlot.SHIELD, )
//                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.DEFENCE) < 60)

                    .addItem(EquipmentSlot.SHIELD, ItemID.DRAGON_DEFENDER).enabledIfOwned()
    ),
    RANGED(PVMUtil::getBestRangePray,
            new InventoryLoadout()
                    .addItem(ItemVariants.RANGING_POTION, 1, 4).setRefill(40)
                    .addItem(ItemID.JUG_OF_WINE, 1, 18).setRefill(500),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.WEAPON, ItemID.SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) < 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.WILLOW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 20)
                    .addItem(EquipmentSlot.WEAPON, ItemID.YEW_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 40)
                    .addItem(EquipmentSlot.WEAPON, ItemID.MAGIC_SHORTBOW)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 50)

                    .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                    .addItem(EquipmentSlot.CHEST, ItemID.GREEN_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 40 && FreeQuest.DRAGON_SLAYER_I.isFinished())
                    .addItem(EquipmentSlot.CHEST, ItemID.BLUE_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.CHEST, ItemID.RED_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.CHEST, ItemID.BLACK_DHIDE_BODY)
                    .setEnabledCondition(() -> Skill.DEFENCE.getLevel() >= 40 && Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                    .addItem(EquipmentSlot.LEGS, ItemID.GREEN_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLUE_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 50)
                    .addItem(EquipmentSlot.LEGS, ItemID.RED_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 60)
                    .addItem(EquipmentSlot.LEGS, ItemID.BLACK_DHIDE_CHAPS)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 70)

                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY).setRefill(10)

                    .addItem(EquipmentSlot.FEET, ItemID.SNAKESKIN_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 30 && Skills.getRealLevel(Skill.DEFENCE) >= 30)
                    .addItem(EquipmentSlot.FEET, ItemID.MIXED_HIDE_BOOTS)
                    .setEnabledCondition(() -> Skills.getRealLevel(Skill.RANGED) >= 60 && Skills.getRealLevel(Skill.DEFENCE) >= 50)

                    .addItem(EquipmentSlot.HANDS, ItemVariants.COMBAT_BRACLET).setRefill(10)

                    .addItem(EquipmentSlot.CAPE, ItemVariants.AVAS).enabledIfOwned()


                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.IRON_ARROW, 1, 500))
                    .setRefill(2000)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.MITHRIL_ARROW, 1, 500))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 20)
                    .addItem(EquipmentSlot.ARROWS, new EquipmentLoadoutItem(ItemID.RUNE_ARROW, 1, 500))
                    .setRefill(2000)
                    .setEnabledCondition(() -> Skill.RANGED.getLevel() >= 40)

    ),
    MAGIC(PVMUtil::getBestMagePray,
            new InventoryLoadout()
                    .addItem(ItemID.AIR_RUNE, 2500)
                    .setRefill(() -> 2500 * 5)
                    .setBuyPrice(6)

                    .addItem(() -> getCastRune()[0], () -> getCastRune()[1])
                    .setRefill(() -> getCastRune()[1] * 5)

                    .addItem(ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)
                    .addItem(ItemID.JUG_OF_WINE, 12, 12)
                    .setRefill(200),
            new EquipmentLoadout()
                    .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                    .setRefill(10)

                    .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
    );

    final Supplier<Prayer> boostPrayers;
    final InventoryLoadout inventoryLoadout;
    final EquipmentLoadout equipmentLoadout;

    public static Normal getSpell() {
        int mag = Skill.MAGIC.getLevel();
        if (mag >= 65) return Normal.WATER_WAVE;
        if (mag >= 47) return Normal.WATER_BLAST;
        if (mag >= 23) return Normal.WATER_BOLT;
        return Normal.WATER_STRIKE;
    }

    private static Integer[] getCastRune() {
        return spellMap.get(getSpell());
    }

    public static final Map<Normal, Integer[]> spellMap = new HashMap<>();

    static {
        spellMap.put(Normal.WATER_WAVE, new Integer[]{ItemID.BLOOD_RUNE, 500});
        spellMap.put(Normal.WATER_BLAST, new Integer[]{ItemID.DEATH_RUNE, 500});
        spellMap.put(Normal.WATER_BOLT, new Integer[]{ItemID.CHAOS_RUNE, 550});
        spellMap.put(Normal.WATER_STRIKE, new Integer[]{ItemID.MIND_RUNE, 1000});
    }

}
