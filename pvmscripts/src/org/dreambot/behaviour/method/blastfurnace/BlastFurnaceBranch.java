package org.dreambot.behaviour.method.blastfurnace;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.events.BankAllInventoryEvent;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.ItemVariants;

import java.util.function.Supplier;

public class BlastFurnaceBranch extends Fractal {
    public BlastFurnaceBranch(Supplier<Boolean> acceptCondition, BlastFurnaceModes mode) {
        super(acceptCondition);
        setSimpleName("BFurn");
        this.paintArraySupplier = () -> new String[]{
                "Coal in furnace " + BarsOres.COAL.getValue(),
                String.format("Ore in furnace Ir: %d Mi: %d Ad: %d Rn: %d",
                        BarsOres.IRON_ORE.getValue(),
                        BarsOres.MITHRIL_ORE.getValue(),
                        BarsOres.ADAMANTITE_ORE.getValue(),
                        BarsOres.RUNITE_ORE.getValue()
                ),
                "",
                ""
        };

        int[] bfWorlds = new int[]{356, 355, 357, 358};

        int bfWorld = bfWorlds[Calculations.random(0, bfWorlds.length)];
        addChildren(
                // go to a bf world
                new Fractal(() -> Worlds.getCurrentWorld() != bfWorld && !Players.getLocal().isInCombat())
                        .setPrependLogic(() -> {
                            WorldHopper.hopWorld(bfWorld);
                            return true;
                        })
                        .setSimpleName("Hop to BF world"),

                new Fractal(() -> !Equipment.contains(ItemID.ICE_GLOVES) || !Equipment.contains(x -> ItemVariants.RING_OF_WEALTH.contains(x.getId())))
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.HAT, ItemID.GRACEFUL_HOOD).enabledIfOwned()
                                .addItem(EquipmentSlot.CHEST, ItemID.GRACEFUL_TOP).enabledIfOwned()
                                .addItem(EquipmentSlot.LEGS, ItemID.GRACEFUL_LEGS).enabledIfOwned()
                                .addItem(EquipmentSlot.FEET, ItemID.GRACEFUL_BOOTS).enabledIfOwned()
                                .addItem(EquipmentSlot.CAPE, ItemID.GRACEFUL_CAPE).enabledIfOwned()
                                .setStrict(true)
                        )
                        .setSimpleName("Wear ice gloves"),
                // make sure you have enough ores
                new GotoBlastFurnace(mode.requiredOres)
                        .setSimpleName("Go to blast furnace"),

                new Fractal(() -> Inventory.contains(Item::isNoted)).setSimpleName("Remove notes")
                        .setPrependLogic(() -> {
                            new BankAllInventoryEvent(BankLocation.BLAST_FURNACE).execute();
                            return true;
                        }),
                // pay the 2500 every 10 minute fee (must be done before coffer)
                new BlastFurnacePayFee().setSimpleName("Pay fee"),
                // stock coffer
                new BlastFurnaceFillCoffer().setSimpleName("Fill coffer"),

                mode.getFractal().setPrependLogic(() -> {
                    if (Widgets.get(x -> x.getText().contains("You must ask the foreman")) != null) {
                        Logger.info("need foreman's permission");
                        BlastFurnacePayFee.mustPayFee = true;
                    }
                    return false;
                })
        );
    }
}
