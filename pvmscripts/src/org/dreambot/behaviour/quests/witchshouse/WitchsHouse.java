package org.dreambot.behaviour.quests.witchshouse;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.training.magic.MagicBranch;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.fractals.util.VarplayerRequirement;

public class WitchsHouse extends Fractal {
    Area PAST_DOOR = new Area(
            new Tile(2900, 3466, 0),
            new Tile(2904, 3466, 0),
            new Tile(2904, 3467, 0),
            new Tile(2908, 3468, 0),
            new Tile(2908, 3477, 0),
            new Tile(2913, 3477, 0),
            new Tile(2914, 3468, 0),
            new Tile(2937, 3468, 0),
            new Tile(2938, 3459, 0),
            new Tile(2900, 3459, 0));
    Area INSIDE_HOUSE = new Area(
            new Tile[]{
                    new Tile(2900, 3476, 0),
                    new Tile(2900, 3464, 0),
                    new Tile(2908, 3465, 0),
                    new Tile(2908, 3477, 0)
            }
    );

    public WitchsHouse() {
        super(() -> !PaidQuest.WITCHS_HOUSE.isFinished());
        setSimpleName("Witchs House");

        Tile mouseTile = new Tile(2902, 3467);
        VarplayerRequirement ratHasMagnet = new VarplayerRequirement(226, 3);
        paintArraySupplier = () -> new String[]{
                "State " + getState(),
                "Rat magged " + ratHasMagnet.check()
        };

        addChildren(
                new KillExperiment().setSimpleName("Kill"),
                new TalkToFractal(() -> Inventory.contains("Ball"),
                        new Tile(2928, 3456),
                        () -> NPCs.closest("Boy"))
                        .setDialogueOptions("matter", "what I can do", "Yes")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(2407)
                        )
                        .setSimpleName("End Witchs house"),

                new MagicBranch(() -> Skills.getRealLevel(Skill.MAGIC) < 35)
                        .setSimpleName("35 magic"),
                // start quest
                new TalkToFractal(() -> getState() == 0,
                        new Tile(2928, 3456),
                        () -> NPCs.closest("Boy"))
                        .setDialogueOptions("matter", "what I can do", "Yes")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CHEESE, 3)
                                .addItem(ItemID.SALMON, 8)
                                .addItem(ItemID.CHAOS_RUNE, 250)
                                .addItem(ItemID.AIR_RUNE, 750)
                                .addItem(ItemID.FALADOR_TELEPORT, 1, 10)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.HANDS, ItemID.LEATHER_GLOVES)
                        )
                        .setSimpleName("Start Witchs house"),

                // if you are no in the maze / get ball area
                new Fractal(() -> !PAST_DOOR.contains(Players.getLocal())
                        && !ratHasMagnet.check()
                )
                        .addChildren(
                                new TalkToFractal(() -> !Inventory.contains(ItemID.DOOR_KEY),
                                        new Tile(2900, 3474),
                                        () -> GameObjects.closest(x -> x.getId() == 2867 && x.hasAction("Look-under")))
                                        .setInteraction("Look-under")
                                        .setSimpleName("Get door key"),
                                // get magnet
                                // todo might need to read diary
                                new TalkToFractal(() -> !Inventory.contains(ItemID.MAGNET),
                                        new Tile(2900, 9874),
                                        () -> GameObjects.closest("Cupboard"))
                                        .setInteraction("Open", "Search")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.DOOR_KEY)
                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DOOR_KEY))
                                                .addItem(ItemID.MAGNET)
                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.MAGNET))
                                                .addItem(ItemID.CHEESE, 1, 3)
                                                .addItem(ItemID.SALMON, 8)
                                                .addItem(ItemID.CHAOS_RUNE, 250)
                                                .addItem(ItemID.AIR_RUNE, 750)
                                                .addItem(ItemID.FALADOR_TELEPORT, 1, 10)
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.WEAPON, ItemID.STAFF_OF_FIRE)
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                                .addItem(EquipmentSlot.HANDS, ItemID.LEATHER_GLOVES)
                                        )
                                        .setSimpleName("Get Magnet"),
                                // drop cheese and do mouse thing, then go through door
                                new UseOnFractal(() -> !ratHasMagnet.check(),
                                        () -> Inventory.get(ItemID.MAGNET),
                                        () -> NPCs.closest("Mouse"), true)
                                        .setArea(mouseTile, 0)
                                        .setPrependLogic(() -> {
                                            NPC mouse = NPCs.closest("Mouse");
                                            if (mouseTile.equals(Players.getLocal().getTile()) && mouse == null) {
                                                Inventory.drop(ItemID.CHEESE);
                                                Sleep.sleepUntil(() -> NPCs.closest("Mouse") != null, 5400);
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Magnet the mouse")
                        )
                        .setSimpleName("Get into witchs house"),

                // witches mini to key,
                new WitchsGarden(() -> true).setSimpleName("Garden")
        );
    }

    private int getState() {
        return PaidQuest.WITCHS_HOUSE.getConfigValue();
    }
}
