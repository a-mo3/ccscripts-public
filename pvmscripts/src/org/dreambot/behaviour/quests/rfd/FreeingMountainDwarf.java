package org.dreambot.behaviour.quests.rfd;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.impl.EntranceWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.behaviour.quests.fishingcontest.FishingContest;
import org.dreambot.behaviour.quests.icegloves.GetIceGloveBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.OwnedItems;

public class FreeingMountainDwarf extends Fractal {
    private int aleState() {
        return PlayerSettings.getBitValue(1893);
    }

    public FreeingMountainDwarf() {
        super(() -> getState() <= 50);

        VarbitRequirement givenAle = new VarbitRequirement(1893, 1);
        VarbitRequirement learnedHowToMakeAle = new VarbitRequirement(1891, 1);

        paintArraySupplier = () -> new String[]{
                "State " + getState(),
                "Learned asgoldian " + learnedHowToMakeAle.isComplete(),
                "given ale " + aleState(),
                ""
        };

        // dwarf tunnel webnode
        WebFinder wf = WebFinder.getWebFinder();
        EntranceWebNode stairsBottom = (EntranceWebNode) wf.getNodesWithin(3, new Tile(2876, 9880)).get(0);
        stairsBottom.setAction("Climb-up");
        stairsBottom.setEntityName("Stairs");
        stairsBottom.setCondition(PaidQuest.FISHING_CONTEST::isFinished);

        EntranceWebNode stairsTop = new EntranceWebNode(2876, 3480, 0);
        stairsTop.setAction("Climb-down");
        stairsTop.setEntityName("Stairs");
        stairsTop.setCondition(PaidQuest.FISHING_CONTEST::isFinished);
        stairsTop.addDualConnections(stairsBottom);
        wf.addWebNode(stairsTop);
        wf.getNearest(stairsTop.getTile(), 15).addDualConnections(stairsTop);

        setSimpleName("RFD Dwarf");
        addChildren(
                new GetIceGloveBranch(() -> !OwnedItems.contains(ItemID.ICE_GLOVES))
                        .setSimpleName("Get ice gloves"),
                new FishingContest().setSimpleName("Fishing contest"),

                new Fractal(() -> OwnedItems.contains(ItemID.DWARVEN_ROCK_CAKE_7510))
                        .addChildren(

                                new TalkToFractal(() -> !Client.isDynamicRegion(),
                                        new Tile(3213, 3222).getArea(2),
                                        () -> GameObjects.closest("Large door"))
                                        .setInteraction("Open")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.DWARVEN_ROCK_CAKE_7510)
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                                        )
                                        .setSimpleName("Finsih RFD Dwarf"),

                                new UseOnFractal(() -> true,
                                        () -> Inventory.get(ItemID.DWARVEN_ROCK_CAKE_7510),
                                        () -> GameObjects.closest("Dwarf"), true)
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                                        )
                                        .setSimpleName("Inspect dwarf")
                        )
                        .setSimpleName("Free dwarf"),

                // Enter RFD instance
                new TalkToFractal(() -> getState() == 0 && !Client.isDynamicRegion(),
                        new Tile(3213, 3222).getArea(2),
                        () -> GameObjects.closest("Large door"))
                        .setInteraction("Open")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EGG)
                                .addItem(ItemID.POT_OF_FLOUR)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .addItem(ItemID.BOWL_OF_WATER)
                                .addItem(ItemID.ASGARNIAN_ALE, 4)
                                .addItem(ItemID.COINS_995, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Start RFD Dwarf"),

                new TalkToFractal(() -> getState() == 0 && Client.isDynamicRegion(),
                        (Area) null,
                        () -> GameObjects.closest("Dwarf"))
                        .setInteraction("Inspect")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EGG)
                                .addItem(ItemID.POT_OF_FLOUR)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .addItem(ItemID.BOWL_OF_WATER)
                                .addItem(ItemID.ASGARNIAN_ALE, 4)
                                .addItem(ItemID.COINS_995, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Inspect dwarf"),


                new TalkToFractal(() -> getState() == 10,
                        new Tile(2865, 9876),
                        () -> NPCs.closest("An old Dwarf"))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EGG)
                                .addItem(ItemID.POT_OF_FLOUR)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .addItem(ItemID.BOWL_OF_WATER)
                                .addItem(ItemID.ASGARNIAN_ALE, 4)
                                .addItem(ItemID.COINS_995, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Old dwarf"),

                new TalkToFractal(() -> getState() == 20 && learnedHowToMakeAle.isNotComplete(),
                        new Tile(2957, 3371),
                        () -> NPCs.closest("Kaylee"))
                        .setDialogueOptions("dwarves", "200")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EGG)
                                .addItem(ItemID.POT_OF_FLOUR)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .addItem(ItemID.BOWL_OF_WATER)
                                .addItem(ItemID.ASGARNIAN_ALE, 4)
                                .addItem(ItemID.COINS_995, 1, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Kaylee"),

                new TalkToFractal(() -> getState() == 20,
                        new Tile(2865, 9876),
                        () -> NPCs.closest("Rohak"))
                        .setInventoryLoadout(new InventoryLoadout()
                                        .addItem(ItemID.EGG)
                                        .addItem(ItemID.POT_OF_FLOUR)
                                        .addItem(ItemID.BUCKET_OF_MILK)
                                        .addItem(ItemID.BOWL_OF_WATER)
                                        .addItem(ItemID.ASGARNIAN_ALE, () -> 4 - Inventory.count(ItemID.ASGOLDIAN_ALE) - aleState())
//                                .setEnabledCondition()
                                        .addItem(ItemID.COINS_995, 500, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setPrependLogic(() -> {
                            if (Inventory.contains(ItemID.ASGARNIAN_ALE)) {
                                if (Widgets.isOpen()) Widgets.closeAll();
                                Inventory.combine(ItemID.ASGARNIAN_ALE, ItemID.COINS_995);
                                Sleep.sleep(1000);
                                return true;
                            }
                            return false;
                        })
                        .setSimpleName("Old dwarf"),

                new TalkToFractal(() -> getState() <= 40,
                        new Tile(2865, 9876),
                        () -> NPCs.closest("Rohak"))
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Old dwarf, make cake"),

                new TalkToFractal(() -> getState() == 50,
                        new Tile(2865, 9876),
                        () -> {
                            GroundItem cake = GroundItems.closest("Dwarven rock cake");
                            return cake != null ? cake : NPCs.closest("Rohak");
                        })
                        .setInteraction("Take", "Talk-to")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.EGG)
                                .setEnabledCondition(() -> GroundItems.closest("Dwarven rock cake") == null)
                                .addItem(ItemID.POT_OF_FLOUR)
                                .setEnabledCondition(() -> GroundItems.closest("Dwarven rock cake") == null)
                                .addItem(ItemID.BUCKET_OF_MILK)
                                .setEnabledCondition(() -> GroundItems.closest("Dwarven rock cake") == null)
                                .addItem(ItemID.BOWL_OF_WATER)
                                .setEnabledCondition(() -> GroundItems.closest("Dwarven rock cake") == null)
                                .addItem(ItemID.COINS_995, 500, 1000)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.GAMES_NECKLACE)
                                .addItem(EquipmentSlot.HANDS, ItemID.ICE_GLOVES)
                        )
                        .setSimpleName("Old dwarf, make cake")
        );
    }

    private static int getState() {
        return PlayerSettings.getBitValue(1892);
    }
}
