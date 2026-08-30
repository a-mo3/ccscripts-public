package org.dreambot.behaviour.dragons;


import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.StaffMode;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;


public class PathToDragons extends Fractal {
    final Area EDGE_LEVER = new Area(3089, 3478, 3093, 3474);
    public static final Area WILDERNESS_PLATO = new Area(3169, 3951, 3145, 3917);
    public static final int QUANTITY_MULTIPLIER = ScriptSettings.getSettingsData().setsToRestock;

    Supplier<Boolean> shouldBringWaterBlast = () -> Skills.getRealLevel(Skill.MAGIC) < 65;

    Supplier<Boolean> shouldBringWaterWave = () -> {
        int magicLvl = Skills.getRealLevel(Skill.MAGIC);
        if (ScriptSettings.getSettingsData().staffMode == StaffMode.WATER_STAFF) return magicLvl >= 65;
        // other staff modes stop bringing runes after 75
        if (ScriptSettings.getSettingsData().staffMode == StaffMode.SCEPTRE) return magicLvl >= 65 && magicLvl < 70;
        return magicLvl >= 65 && magicLvl < 75;
    };

    public PathToDragons(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.eventBreakCondition = () -> !Worlds.getCurrent().isMembers() || Combat.isInWild();
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.AIR_RUNE, ScriptSettings.getFireBoltCharges() * 3).setRefill(ScriptSettings.getFireBoltCharges() * 3 * QUANTITY_MULTIPLIER)
                .setEnabledCondition(shouldBringWaterBlast)
                .addItem(ItemID.MIND_RUNE, ScriptSettings.getFireBoltCharges()).setRefill(ScriptSettings.getFireBoltCharges() * QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> shouldBringWaterBlast.get() && Skills.getRealLevel(Skill.MAGIC) < 23)
                .addItem(ItemID.CHAOS_RUNE, ScriptSettings.getFireBoltCharges()).setRefill(ScriptSettings.getFireBoltCharges() * QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> shouldBringWaterBlast.get() && Skills.getRealLevel(Skill.MAGIC) < 47)
                .addItem(ItemID.DEATH_RUNE, ScriptSettings.getFireBoltCharges()).setRefill(ScriptSettings.getFireBoltCharges() * QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> shouldBringWaterBlast.get() && Skills.getRealLevel(Skill.MAGIC) < 65)


                // runes for water wave over lvl 65
                .addItem(ItemID.AIR_RUNE, ScriptSettings.getFireBoltCharges() * 5).setRefill(ScriptSettings.getFireBoltCharges() * 3 * QUANTITY_MULTIPLIER)
                .setEnabledCondition(shouldBringWaterWave)
                .addItem(ItemID.BLOOD_RUNE, ScriptSettings.getFireBoltCharges()).setRefill(ScriptSettings.getFireBoltCharges() * QUANTITY_MULTIPLIER)
                .setEnabledCondition(shouldBringWaterWave)

                .addItem(ItemID.JUG_OF_WINE, 12).setRefill(1000)
                .addItem(ItemID.KNIFE, 1).setRefill(15).setPriceIncrease(1.5f)

                .addItem(ItemVariants.AMULET_OF_GLORY)
                .setRefill(5)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && ScriptSettings.getSettingsData().useOccult)

                .addItem(ItemVariants.STAMINA_POTION).setRefill(10)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useStaminas && !Combat.isInWild())

                .addItem(ItemVariants.LOOTING_BAG)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().useLootingBag && OwnedItems.contains(ItemVariants.LOOTING_BAG))

                .setStrictSupplier(() -> !Combat.isInWild())
                .setMuleRequestAmount(740_000)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < 70 || !ScriptSettings.getSettingsData().useOccult)

                .addItem(EquipmentSlot.AMULET, ItemID.OCCULT_NECKLACE)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && ScriptSettings.getSettingsData().useOccult)

                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_WATER)
                .setRefill(QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) < (ScriptSettings.getSettingsData().staffMode == StaffMode.TRIDENT ? 75 : 70)
                        || ScriptSettings.getSettingsData().staffMode == StaffMode.WATER_STAFF)

                .addItem(EquipmentSlot.HAT, ScriptSettings.getSettingsData().hatId)
                .setRefill(QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().hatId > 0 && Skills.getRealLevel(Skill.DEFENCE) >= ScriptSettings.getSettingsData().armourDefReq)

                .addItem(EquipmentSlot.CHEST, ScriptSettings.getSettingsData().chestId)
                .setRefill(QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().chestId > 0 && Skills.getRealLevel(Skill.DEFENCE) >= ScriptSettings.getSettingsData().armourDefReq)

                .addItem(EquipmentSlot.LEGS, ScriptSettings.getSettingsData().legId)
                .setRefill(QUANTITY_MULTIPLIER)
                .setEnabledCondition(() -> ScriptSettings.getSettingsData().legId > 0 && Skills.getRealLevel(Skill.DEFENCE) >= ScriptSettings.getSettingsData().armourDefReq)

                .addItem(EquipmentSlot.WEAPON, ItemVariants.TRIDENT)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 75 && ScriptSettings.getSettingsData().staffMode == StaffMode.TRIDENT)

                .addItem(EquipmentSlot.WEAPON, ItemVariants.SCEPTRE)
                .setEnabledCondition(() -> Skills.getRealLevel(Skill.MAGIC) >= 70 && ScriptSettings.getSettingsData().staffMode == StaffMode.SCEPTRE)

//                .setMuleRequestAmount(740_000)
        ;

    }

    @Override
    public int onLoop() {
        if (!Combat.isInWild()) {
            if (Dialogues.inDialogue() && !Dialogues.canEnterInput()) {
                Dialog.solve("I'm brave.");
                return ReactionGenerator.getNormal();
            }

            if (!EDGE_LEVER.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(EDGE_LEVER.getCenter());
                return ReactionGenerator.getNormal();
            }

            GameObject lever = GameObjects.closest("Lever");
            if (lever != null && lever.interact("Pull")) {
                Sleep.sleepUntil(Combat::isInWild, 3200);
                return ReactionGenerator.getNormal();
            }

            return ReactionGenerator.getNormal();
        }

        if (WILDERNESS_PLATO.contains(Players.getLocal())) {
            GameObject web = GameObjects.closest(x -> x.getName().equals("Web") && x.distance() < 12 && x.hasAction("Slash"));
            if (web != null && web.interact("Slash")) {
                Sleep.sleepUntil(() -> GameObjects.closest("Web") == null, 3400);
                return ReactionGenerator.getNormal();
            }
            if (Walking.shouldWalk()) Walking.clickTileOnMinimap(Players.getLocal().getTile().translate(0, 10));
        }
        return ReactionGenerator.getNormal();
    }
}
