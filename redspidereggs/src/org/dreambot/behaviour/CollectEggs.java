package org.dreambot.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

public class CollectEggs extends Fractal {
    public static final Area SPIDER_AREA = new Area(1828, 9970, 1850, 9945);
    public static final Area FORTHOS_DUNGEON = new Area(1777, 9996, 1866, 9886);
    public static final Area FORTHOS_RUIN = new Area(1699, 3576, 1705, 3572);
    public static final Area FORTHOS_EXIT = new Area(1828, 9976, 1831, 9972);

    public CollectEggs() {
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.CHEST, ItemID.MONKS_ROBE_TOP)
                .addItem(EquipmentSlot.LEGS, ItemID.MONKS_ROBE)
                .addItem(EquipmentSlot.AMULET, ItemVariants.SKILLS_NECKLACE)
                .setRefill(50)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_DUELING)
                .setRefill(10)
        ;

        this.inventoryLoadout = new InventoryLoadout().setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995));
    }

    @Override
    public int onLoop() {
        if (Worlds.getCurrentWorld() == 330) {
            WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal()
                    && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel()
            ));
            return ReactionGenerator.getLong();
        }

        if (Client.isDynamicRegion()) {
            // from prayer training
            Magic.castSpell(Normal.HOME_TELEPORT);
            Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 24000);
            return ReactionGenerator.getNormal();
        }
        handlePrayer();

//        if (Skills.getBoostedLevel(Skill.PRAYER) < 10 || Inventory.isFull()) {
//            if (Walking.shouldWalk()) Walking.walk(BankLocation.HOSIDIUS);
//
//
//            if (FORTHOS_DUNGEON.contains(Players.getLocal())) {
//                handlePrayer();
//                WebFinder.getWebFinder().disableEquipmentTeleports();
//                WebFinder.getWebFinder().disableInventoryTeleports();
//                if (FORTHOS_EXIT.contains(Players.getLocal())) {
//                    if (Walking.shouldWalk(8)) Walking.walk(FORTHOS_EXIT);
//                    return ReactionGenerator.getNormal();
//                }
//
//                GameObject ladder = GameObjects.closest("Ladder");
//                if (ladder.interact("Climb-up")) {
//                    Sleep.sleepUntil(() -> FORTHOS_DUNGEON.contains(Players.getLocal()),
//                            2400);
//                }
//                return ReactionGenerator.getNormal();
//            }
//
//            if (!Bank.isOpen()) {
//                if (Walking.shouldWalk(8)) Bank.open(BankLocation.HOSIDIUS);
//                return ReactionGenerator.getNormal();
//            }
//
//            Bank.depositAllItems();
//            return ReactionGenerator.getNormal();
//        }
//

        if (!FORTHOS_DUNGEON.contains(Players.getLocal())) {
            if (!FORTHOS_RUIN.contains(Players.getLocal())) {
                if (Walking.shouldWalk(8)) Walking.walk(FORTHOS_RUIN);
                return ReactionGenerator.getNormal();
            }

            GameObject stairs = GameObjects.closest("Ladder");
            if (stairs != null && stairs.interact("Climb-down")) {
                Sleep.sleepUntil(() -> FORTHOS_DUNGEON.contains(Players.getLocal()), 2400);
            }
            return ReactionGenerator.getNormal();
        }

        if (!SPIDER_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(SPIDER_AREA);
            return ReactionGenerator.getNormal();
        }

        if (Walking.getRunEnergy() > 4 && !Walking.isRunEnabled()) {
            Walking.toggleRun();
        }

        GroundItem egg = GroundItems.closest(ItemID.RED_SPIDERS_EGGS);
        if (egg != null && egg.interact()) {
            Sleep.sleepUntil(() -> !egg.exists(), 2400);
        }
        return ReactionGenerator.getNormal();
    }

    private void handlePrayer() {
        if (SPIDER_AREA.contains(Players.getLocal())) {
            if (!Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) Prayers.toggle(true, Prayer.PROTECT_FROM_MELEE);
            return;
        }

        if (Prayers.isActive(Prayer.PROTECT_FROM_MELEE)) Prayers.toggle(false, Prayer.PROTECT_FROM_MELEE);
    }
}
