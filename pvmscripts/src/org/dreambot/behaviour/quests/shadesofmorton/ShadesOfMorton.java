package org.dreambot.behaviour.quests.shadesofmorton;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.behaviour.training.slayer.SlayerMode;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.util.VarplayerRequirement;

import java.util.function.Supplier;

public class ShadesOfMorton extends Fractal {
    public ShadesOfMorton(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        VarplayerRequirement razmirePartlyCured = new VarplayerRequirement(340, true, 3);
        VarplayerRequirement curedRazmire = new VarplayerRequirement(340, true, 6); //64
        VarplayerRequirement ulsquirePartlyCured = new VarplayerRequirement(340, true, 1);
        VarplayerRequirement curedUlsquire = new VarplayerRequirement(340, true, 5);
        VarplayerRequirement repairedTemple = new VarplayerRequirement(340, 100);
//        VarplayerRequirement has20Sanctity = new VarplayerRequirement(VarPlayerID.TEMPLE_SANCTITY_P, 20, Operation.GREATER_EQUAL);

        setSimpleName("Shades");
        addChildren(
                new TalkToFractal(
                        () -> getState() == 0,
                        new Tile(3481, 3279, 0),
                        () -> GameObjects.closest(4062))
                        .setInteraction("Search")
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.SERUM_BOOK)) {
                                log("Open serum book");
                                Inventory.interact(ItemID.SERUM_BOOK);
                            }
                            return false;
                        })
                        // bring combat and
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.TARROMIN_POTION_UNF, 2)
                                .addItem(ItemID.ASHES, 2)
                                .addItem(ItemID.COINS_995, 5000)
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemID.FLAMTAER_HAMMER)
                                .addItem(ItemID.LOGS)
                                .addItem(ItemID.SHARK, 10)
                        )
                        .setEquipmentLoadout(SlayerMode.MELEE.getEquipmentLoadout())
                        .setSimpleName("Read diary"),

                new UseOnFractal(() -> getState() == 5,
                        () -> Inventory.get(ItemID.TARROMIN_POTION_UNF),
                        () -> Inventory.get(ItemID.ASHES))
                        .setSimpleName("Make 207"),

                new TalkToFractal(() -> getState() == 10,
                        new Tile(3488, 3296, 0),
                        () -> NPCs.closest("Razmire")
                ).setSimpleName("Razmire")


        );
    }

    private int getState() {
        return PaidQuest.SHADES_OF_MORTTON.getConfigValue();
    }
}
