package org.dreambot.behaviour.quests.rfd;

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.quests.icegloves.GetIceGloveBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;

import java.util.function.Supplier;

public class GetRockCake extends Fractal {
    public GetRockCake(Supplier<Boolean> acceptCondition) {
        super(() -> acceptCondition.get() && Bank.isCached() && !OwnedItems.contains(ItemID.DWARVEN_ROCK_CAKE_7510));
        setSimpleName("Get rock cake");
        addChildren(
                new AnotherCooksAssistant(),
                new GetIceGloveBranch(() -> !OwnedItems.contains(ItemID.ICE_GLOVES)).setSimpleName("Ice gloves"),
                new FreeingMountainDwarf(),
                new TalkToFractal(() -> true,
                        new Tile(2865, 9876),
                        () -> {
                            GroundItem cake = GroundItems.closest("Dwarven rock cake");
                            return cake != null ? cake : NPCs.closest("Rohak");
                        })
                        .setInteraction("Take", "Talk-to")
                        .setDialogueOptions("rock cake", "100")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 500, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("After quest rock cake")
        );
    }

    public GetRockCake() {
        super(() -> Bank.isCached() && !OwnedItems.contains(ItemID.DWARVEN_ROCK_CAKE_7510));
        setSimpleName("Get rock cake");
        addChildren(
                new AnotherCooksAssistant(),
                new GetIceGloveBranch(() -> !OwnedItems.contains(ItemID.ICE_GLOVES)).setSimpleName("Ice gloves"),
                new FreeingMountainDwarf(),
                new TalkToFractal(() -> true,
                        new Tile(2865, 9876),
                        () -> {
                            GroundItem cake = GroundItems.closest("Dwarven rock cake");
                            return cake != null ? cake : NPCs.closest("Rohak");
                        })
                        .setInteraction("Take", "Talk-to")
                        .setDialogueOptions("rock cake", "100")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.COINS_995, 500, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("After quest rock cake")
        );
    }
}
