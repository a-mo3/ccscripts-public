package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * enters puro puro but uses an overworld crop circle, if none are scouted rn go on the hunt for them
 */
public class EnterOverworldPuroPuro extends Fractal {
    static final Area PURO_PURO = new Area(2555, 4359, 2626, 4283);

    public EnterOverworldPuroPuro() {
        super(() -> !PURO_PURO.contains(Players.getLocal()));

        this.setSimpleName("Enter puro pruo (Overworld)");
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.IMPLING_JAR, 28)
                .setRefill(750)
                .setStrict(true)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.BUTTERFLY_NET)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(10)
        ;
    }

    public EnterOverworldPuroPuro(Supplier<Boolean> accept) {
        super(() -> accept.get() && !PURO_PURO.contains(Players.getLocal()));
        this.setSimpleName("Enter puro pruo (Overworld)");
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.IMPLING_JAR, 28)
                .setRefill(750)
                .setStrict(true)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.WEAPON, ItemID.BUTTERFLY_NET)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(10)
        ;
    }

    public EnterOverworldPuroPuro(int jars) {
        super(() -> !PURO_PURO.contains(Players.getLocal()));

        this.setSimpleName("Enter puro pruo");
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.BUTTERFLY_NET)
                .addItem(ItemID.IMPLING_JAR, jars)
                .setRefill(jars)
                .setStrict(true)
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                .setRefill(10)
        ;
    }

    Area GE_CROP_AREA = new Area(3139, 3463, 3143, 3458);
    int lastCheckedWorld = -1;
    Timer checkAPITimer = new Timer(30_000);
    boolean firstCheck = false;

    @Override
    public int onLoop() {
        // check the server every few minutes to see if anyone else found one
        if (checkAPITimer.finished() || !firstCheck) {
            checkAPITimer.reset();
            firstCheck = true;
            CropCircleScouter.getCropCircles();
            return ReactionGenerator.getNormal();
        }

        if (!GE_CROP_AREA.contains(Players.getLocal())) {
            log("Walk to crop area");
            if (Walking.shouldWalk()) Walking.walk(GE_CROP_AREA);
            return ReactionGenerator.getNormal();
        }

        if (CropCircleScouter.getWorld() < 0) {
            log("Scout for overworld portals");

            GameObject cropCircle = GameObjects.closest("Centre of crop circle");
            if (cropCircle != null) {
                log("Found crop circle on " + Worlds.getCurrentWorld());
                cropCircle.interact();
                CropCircleScouter.reportCropCircle();
                Sleep.sleepUntil(() -> PURO_PURO.contains(Players.getLocal()), 2400);
                return ReactionGenerator.getLong();
            }

            log("Hop worlds we've checked this world");
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() == 0));
            // i believe dreambot sleeps in this method until we're good and loaded into the next world
            return ReactionGenerator.getNormal() + 1000;
        }

        // we are at spot, make sure we're on world then enter puro puro or invalidate the portal
        if (Worlds.getCurrentWorld() != CropCircleScouter.getWorld()) {
            log("Get onto crop world " + CropCircleScouter.getWorld());
            WorldHopper.hopWorld(CropCircleScouter.getWorld());
            return ReactionGenerator.getNormal() + 1000;
        }

        GameObject cropCircle = GameObjects.closest("Centre of crop circle");
        if (cropCircle != null) {
            log("Found crop circle on " + Worlds.getCurrentWorld());
            cropCircle.interact();
            CropCircleScouter.reportCropCircle();
            Sleep.sleepUntil(() -> PURO_PURO.contains(Players.getLocal()), 2400);
            return ReactionGenerator.getLong();
        } else {
            log("No crop circle on the crop circle world, invalidating and searching again.");
            CropCircleScouter.invalidateCropCircle();
        }

//        if (Walking.shouldWalk()) Walking.walk(PURO_PURO);
        return ReactionGenerator.getNormal();
    }
}
