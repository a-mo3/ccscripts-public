package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.PuroPuroSettings;
import org.dreambot.settings.timing.ReactionGenerator;

public class EnterPuroPuro extends Fractal {
    static final Area PURO_PURO = new Area(2555, 4359, 2626, 4283);
    static final Area WHOLE_ZANARIS = new Area(2317, 4484, 2509, 4339);
    static final Area LUMMY_SHED = new Area(3202, 3170, 3205, 3167);
    final PuroPuroSettings setting;

    public EnterPuroPuro(PuroPuroSettings setting) {
        super(() -> !PURO_PURO.contains(Players.getLocal()));
        this.setting = setting;
        this.setSimpleName("Enter puro pruo");

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.DRAMEN_STAFF)
                .addItem(EquipmentSlot.AMULET, ItemVariants.NECKLACE_OF_PASSAGE)
                .setRefill(10)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(10);

        if (setting.puroMode == PuroMode.SNARE) {
            // snare mode brings runes
            this.inventoryLoadout = new InventoryLoadout()
                    .addItem(ItemID.MAGIC_BUTTERFLY_NET)
                    .addItem(ItemID.EARTH_RUNE, 400).setRefill(4000)
                    .addItem(ItemID.WATER_RUNE, 400).setRefill(4000)
                    .addItem(ItemID.NATURE_RUNE, 300).setRefill(2500)
                    .addItem(ItemID.IMPLING_JAR, 24)
                    .setRefill(750)
                    .setStrict(true);
            return;
        }

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MAGIC_BUTTERFLY_NET)
                .addItem(ItemID.IMPLING_JAR, 27)
                .setRefill(750)
                .setStrict(true);
    }

    public EnterPuroPuro(PuroPuroSettings setting, int jars) {
        super(() -> !PURO_PURO.contains(Players.getLocal()));
        this.setting = setting;

        this.setSimpleName("Enter puro pruo");
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.MAGIC_BUTTERFLY_NET)
                .addItem(ItemID.IMPLING_JAR, jars)
                .setRefill(jars)
                .setStrict(true)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.DRAMEN_STAFF)
                .addItem(EquipmentSlot.AMULET, ItemVariants.NECKLACE_OF_PASSAGE)
                .setRefill(10)
                .setEnabledCondition(() -> PaidQuest.FAIRYTALE_II.isStarted() || PaidQuest.FAIRYTALE_II.isFinished())


                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(10)
        ;
    }

    @Override
    public int onLoop() {
        if (Walking.shouldWalk()) Walking.walk(PURO_PURO);
        return ReactionGenerator.getNormal();
    }
}
