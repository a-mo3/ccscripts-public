package org.dreambot.behaviour.quests.eadgarsruse;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.quest.book.PaidQuest;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.generic.TalkToFractal;
import org.dreambot.fractals.generic.UseOnFractal;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.quest.VarbitRequirement;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.webnodes.TrollStrongholdNodes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EadgarsRuse extends Fractal implements ChatListener {
    public EadgarsRuse() {
        super(() -> !PaidQuest.EADGARS_RUSE.isFinished());

        // todo add skill reqs
        this.paintArraySupplier = () -> new String[]{
                "State: " + getState()
        };

        TrollStrongholdNodes.init();
        VarbitRequirement freedEadgar = new VarbitRequirement(0, 1);
        Area ROCK_THROW_AREA = new Area(
                new Tile(2879, 3692, 0),
                new Tile(2884, 3696, 0),
                new Tile(2894, 3701, 0),
                new Tile(2904, 3697, 0),
                new Tile(2907, 3702, 0),
                new Tile(2901, 3710, 0),
                new Tile(2876, 3700, 0));

        Timer eatTimer = new Timer(1200);
        Supplier<Boolean> staySafePrepend = () -> {
            if (eatTimer.finished() && Combat.getHealthPercent() < 50 && Inventory.contains(ItemID.SHARK)) {
                if (Widgets.isOpen()) Widgets.closeAll();
                eatTimer.reset();
                Inventory.interact(ItemID.SHARK);
            }

            Item pp = ItemVariants.PRAYER_POTION.getItem();
            if (eatTimer.finished() && Skills.getBoostedLevel(Skill.PRAYER) < 1 && pp != null) {
                pp.interact("Drink");
                eatTimer.reset();
            }

            if (Skills.getBoostedLevel(Skill.PRAYER) > 0) {
                if (ROCK_THROW_AREA.contains(Players.getLocal())) {
                    Prayers.toggle(true, Prayer.PROTECT_FROM_MISSILES);
                } else {
                    Prayers.toggle(false, Prayer.PROTECT_FROM_MISSILES);
                    Prayers.toggle(Players.getLocal().isInCombat(), Prayer.PROTECT_FROM_MELEE);
                }
            }
            return false;
        };

        Tile parrotTile = new Tile(2612, 3285);
        Area eadgarsRoom = new Tile(2889, 10078, 2).getArea(15);
        Tile fireTile = new Tile(2888, 3669, 0);

        setSimpleName("Eadgars ruse");
        addChildren(
                new TalkToFractal(() -> getState() == 0,
                        new Tile(2899, 3429, 1).getArea(5),
                        () -> NPCs.closest("Sanfew"))
                        .setDialogueOptions("general", "work for me", "do it", "Yes.")
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                        )
                        .setSimpleName("Start @ Sanfew"),

                new TalkToFractal(() -> getState() == 10 && freedEadgar.isNotComplete(),
                        new Tile(2832, 10082, 0).getArea(5),
                        () -> GameObjects.closest(3765))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.SHARK, 1, 5)
                                .addItem(ItemID.CELL_KEY_2)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.CELL_KEY_2))
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Meet eadgar in Jail"),

                new TalkToFractal(() -> getState() == 10,
                        new Tile(2889, 10078, 2).getArea(5),
                        () -> NPCs.closest("Eadgar"))
                        .setDialogueOptions("goutweed")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.SHARK, 1, 5)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Meet eadgar in cave"),

                new TalkToFractal(() -> getState() == 15,
                        new Tile(2845, 10057, 1).getArea(5),
                        () -> NPCs.closest("Burntmeat"))
                        .setDialogueOptions("goutweed")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 5)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Talk to Burntmeat"),

                new TalkToFractal(() -> getState() == 25,
                        new Tile(2889, 10078, 2).getArea(5),
                        () -> NPCs.closest("Eadgar"))
                        .setDialogueOptions("goutweed")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.SHARK, 1, 5)
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Return to eadgar"),

                new Fractal(() -> getState() == 30)
                        .addChildren(
                                new TalkToFractal(() -> OwnedItems.contains(ItemID.DRUNK_PARROT),
                                        new Tile(2889, 10078, 2).getArea(5),
                                        () -> NPCs.closest("Eadgar"))
                                        .setDialogueOptions("goutweed")
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                                .addItem(ItemID.DRUNK_PARROT)
                                                .addItem(ItemID.SHARK, 12)
                                                .setEnabledCondition(() -> !Inventory.contains(ItemID.SHARK))
                                                .addItem(ItemVariants.PRAYER_POTION, 3, 3)
                                                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null)
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                                        )
                                        .setPrependLogic(staySafePrepend)
                                        .setSimpleName("Return to eadgar w/ parrot"),


                                new UseOnFractal(() -> Inventory.contains(ItemID.ALCOCHUNKS),
                                        () -> Inventory.get(ItemID.ALCOCHUNKS),
                                        () -> GameObjects.closest("Aviary Hatch"),
                                        true
                                ).setSimpleName("Get parrot drunk"),

                                new TalkToFractal(() -> !OwnedItems.contains(ItemID.DRUNK_PARROT),
                                        new Tile(2612, 3285).getArea(5),
                                        () -> NPCs.closest("Parroty Pete"))
                                        .setDialogueOptionsSupplier(() -> {
                                            List<String> a = Arrays.asList("Eadgar", "feed", "add it");
                                            Collections.shuffle(a);
                                            return a.toArray(new String[]{});
                                        })
                                        .setPrependLogic(() -> {
                                            if (parrotRelog && parrotRelogTimer.finished()) {
                                                parrotRelogTimer.reset();
                                                parrotRelog = false;
                                                log("Need to relog so we can mix vodka");
                                                WorldHopper.hopWorld(
                                                        Worlds.getRandomWorld(x -> x.isNormal() && x.getWorld() != 401 && x.isMembers() && x.getMinimumLevel() < Skills.getTotalLevel())
                                                );
                                                return true;
                                            }

                                            if (parrotTile.distance() < 7 && !Dialogues.inDialogue())
                                                Inventory.combine(ItemID.VODKA, ItemID.PINEAPPLE_CHUNKS);
                                            return false;
                                        })
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                                .addItem(ItemID.VODKA)
                                                .addItem(ItemID.PINEAPPLE_CHUNKS)
                                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                                        )
                                        .setSimpleName("Get parrot")
                        )
                        .setSimpleName("Get a parrot"),

                new Fractal(() -> getState() == 50 || (getState() == 30 && OwnedItems.contains(ItemID.DRUNK_PARROT)))
                        .addChildren(
                                new TalkToFractal(() -> !OwnedItems.contains(ItemID.DIRTY_ROBE),
                                        new Tile(2910, 3417, 0).getArea(4),
                                        () -> NPCs.closest("Tegid"))
                                        .setDialogueOptions("Sanfew", "Eadgar")
                                        .setSimpleName("Get dirty robes"),

                                new UseOnFractal(() -> true,
                                        () -> Inventory.get(ItemID.DRUNK_PARROT),
                                        () -> GameObjects.closest("Rack"),
                                        true)
                                        .setArea(new Tile(2828, 10097))
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                                .addItem(ItemID.DRUNK_PARROT)
                                                .addItem(ItemID.DIRTY_ROBE)
                                                .addItem(ItemID.SHARK, 12)
                                                .setEnabledCondition(() -> !Inventory.contains(ItemID.SHARK))
                                                .addItem(ItemVariants.PRAYER_POTION, 3, 3)
                                                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null)
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                                        )
                                        .setSimpleName("Put parrot on rack")
                        )
                        .setSimpleName("Get robes and put parrot on rack"),

                new TalkToFractal(() -> getState() <= 70,
                        new Tile(2889, 10078, 2).getArea(5),
                        () -> NPCs.closest("Eadgar"))
                        .setDialogueOptions("goutweed")
                        .setLoadoutCondition(() -> !eadgarsRoom.contains(Players.getLocal()))
                        .setInventoryLoadout(new InventoryLoadout()
                                // todo consider using item removed listener to track when these are taken from inv in eadgars cave
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.DIRTY_ROBE)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DIRTY_ROBE))
                                .addItem(ItemID.TINDERBOX)
                                .addItem(ItemID.GRAIN, 10)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DIRTY_ROBE))
                                .addItem(ItemID.RAW_CHICKEN, 5)
                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.DIRTY_ROBE))
                                .addItem(ItemID.LOGS, 3)
                                .addItem(ItemID.RANARR_POTION_UNF)
                                .addItem(ItemID.PESTLE_AND_MORTAR)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                                .addItem(ItemID.SHARK, 1, 3)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 1)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Return to Eadgar w/ materials"),

//                new TalkToFractal(() -> getState() == 70,
//                        new Tile(2889, 10078, 2).getArea(5),
//                        () -> NPCs.closest("Eadgar"))
//                        .setDialogueOptions("goutweed")
//                        .setInventoryLoadout(new InventoryLoadout()
//                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
//                                .addItem(ItemID.TINDERBOX)
//                                .addItem(ItemID.LOGS)
//                                .addItem(ItemID.PESTLE_AND_MORTAR)
//                                .addItem(ItemID.RANARR_POTION_UNF)
//                                .addItem(ItemID.SHARK, 1, 3)
//                                .addItem(ItemVariants.PRAYER_POTION, 1, 1)
//                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
//                        )
//                        .setEquipmentLoadout(new EquipmentLoadout()
//                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
//                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
//                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
//                        )
//                        .setPrependLogic(staySafePrepend)
//                        .setSimpleName("Return to Eadgar w/ materials"),

                new Fractal(() -> getState() == 80)
                        .addChildren(
                                new TalkToFractal(() -> !Inventory.contains(ItemID.TROLL_THISTLE, ItemID.DRIED_THISTLE, ItemID.GROUND_THISTLE, ItemID.TROLL_POTION),
                                        new Tile(2890, 3678),
                                        () -> NPCs.closest("Thistle"))
                                        .setDialogueOptions("Eadgar")
                                        .setInteraction("Pick")
                                        .setSimpleName("Pick thistle"),
                                new UseOnFractal(() -> !Inventory.contains(ItemID.DRIED_THISTLE, ItemID.GROUND_THISTLE, ItemID.TROLL_POTION),
                                        () -> Inventory.get(ItemID.TROLL_THISTLE),
                                        () -> GameObjects.closest("Fire"), true)
//                                        .setArea(new Tile(2889, 3685))
                                        .setPrependLogic(() -> {
                                            if (GameObjects.closest(x -> x.getName().equals("Fire") && x.distance() < 32) == null
                                                    && Inventory.containsAll(ItemID.LOGS, ItemID.TINDERBOX)) {
                                                log("Make fire");
                                                if (!fireTile.equals(Players.getLocal().getTile())) {
                                                    Walking.walkExact(fireTile);
                                                    Sleep.sleepUntil(() -> fireTile.equals(Players.getLocal().getTile()), 3200);
                                                } else {
                                                    Inventory.combine(ItemID.TINDERBOX, ItemID.LOGS);
                                                    Sleep.sleepUntil(() -> GameObjects.closest(x -> x.getName().equals("Fire") && x.distance() < 32) != null,
                                                            8400);
                                                }

                                                return true;
                                            }
                                            return false;
                                        })
                                        .setSimpleName("Dry thistle"),

                                new TalkToFractal(() -> true,
                                        new Tile(2889, 10078, 2).getArea(5),
                                        () -> NPCs.closest("Eadgar"))
                                        .setDialogueOptions("goutweed")
                                        .setPrependLogic(() -> {
                                            staySafePrepend.get();
                                            if (Inventory.containsAll(ItemID.DRIED_THISTLE, ItemID.PESTLE_AND_MORTAR)) {
                                                Inventory.combine(ItemID.DRIED_THISTLE, ItemID.PESTLE_AND_MORTAR);
                                                return true;
                                            }

                                            if (Inventory.containsAll(ItemID.RANARR_POTION_UNF, ItemID.GROUND_THISTLE)) {
                                                Inventory.combine(ItemID.RANARR_POTION_UNF, ItemID.GROUND_THISTLE);
                                                return true;
                                            }
                                            return false;
                                        })
                                        .setLoadoutCondition(() -> !Inventory.contains(ItemID.TROLL_POTION))
                                        .setInventoryLoadout(new InventoryLoadout()
                                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                                .addItem(ItemID.TINDERBOX)
                                                .addItem(ItemID.LOGS)
                                                .addItem(ItemID.PESTLE_AND_MORTAR)
                                                .addItem(ItemID.RANARR_POTION_UNF)
                                                .setEnabledCondition(() -> !OwnedItems.contains(ItemID.TROLL_POTION))
                                                .addItem(ItemID.TROLL_POTION)
                                                .setEnabledCondition(() -> OwnedItems.contains(ItemID.TROLL_POTION))
                                                .addItem(ItemID.SHARK, 1, 3)
                                                .addItem(ItemVariants.PRAYER_POTION, 1, 1)
                                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                                        )
                                        .setEquipmentLoadout(new EquipmentLoadout()
                                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                                        )
                                        .setSimpleName("Return to Eadgar w/ troll potion")


                        )
                        .setSimpleName("Make troll potion"),

                new TalkToFractal(() -> getState() == 85,
                        new Tile(2828, 10097).getArea(4),
                        () -> GameObjects.closest("Rack"))
                        .setInteraction("Search")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.SHARK, 1, 3)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 1)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Take parrot off rack"),

                new TalkToFractal(() -> getState() == 86,
                        new Tile(2889, 10078, 2).getArea(5),
                        () -> NPCs.closest("Eadgar"))
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.SHARK, 1, 3)
                                .addItem(ItemVariants.PRAYER_POTION, 1, 1)
                                .addItem(ItemID.DRUNK_PARROT)
                                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995))
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Return to Eadgar w/ parrot"),

                new TalkToFractal(() -> getState() == 87,
                        new Tile(2845, 10057, 1).getArea(5),
                        () -> NPCs.closest("Burntmeat"))
                        .setDialogueOptions("Eadgar", "goutweed")
                        .setInventoryLoadout(new InventoryLoadout()
                                .addItem(ItemID.CAMELOT_TELEPORT, 1, 5) // todo consider zana staff & use fairy ring
                                .addItem(ItemID.SHARK, 12)
                                .setEnabledCondition(() -> !Inventory.contains(ItemID.SHARK))
                                .addItem(ItemVariants.PRAYER_POTION, 3, 3)
                                .setEnabledCondition(() -> ItemVariants.PRAYER_POTION.getItem() == null)
                                .addItem(ItemID.FAKE_MAN)
                        )
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                                .addItem(EquipmentSlot.FEET, ItemID.CLIMBING_BOOTS)
                        )
                        .setPrependLogic(staySafePrepend)
                        .setSimpleName("Talk to Burntmeat"),

                new Fractal(() -> getState() == 90 || !OwnedItems.contains(ItemID.GOUTWEED))
                        .addChildren(
                                // key is consumed once you go through stockroom door so we also check Y
                                new TalkToFractal(() -> !Inventory.contains(ItemID.STOREROOM_KEY) && getState() < 100 && Players.getLocal().getZ() == 1,
                                        new Tile(2852, 10049, 1).getArea(3),
                                        () -> GameObjects.closest(x -> x.getTile().equals(new Tile(2852, 10049, 1))))
                                        .setDialogueOptions("going")
                                        .setInteraction("Open", "Search")
                                        .setPrependLogic(staySafePrepend)
                                        .setSimpleName("Get storeroom key"),

                                new GoutWeedPuzzle().setSimpleName("maze")
                                        .setPrependLogic(staySafePrepend)
                        )
                        .setSimpleName("Get goutweed"),

                new TalkToFractal(() -> getState() == 100,
                        new Tile(2899, 3429, 1).getArea(5),
                        () -> NPCs.closest("Sanfew"))
                        .setDialogueOptions("general", "work for me", "do it", "Yes.")
                        .setInventoryLoadout(new InventoryLoadout().addItem(ItemID.GOUTWEED))
                        .setEquipmentLoadout(new EquipmentLoadout()
                                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
                        )
                        .setSimpleName("Finsih @ Sanfew")
        );
    }

    Timer parrotRelogTimer = new Timer(95_000);
    boolean parrotRelog;

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message == null || message.getMessage() == null) return;
        if (message.getMessage().toLowerCase().contains("why would you want to do that")) parrotRelog = true;
    }

    private int getState() {
        return PaidQuest.EADGARS_RUSE.getConfigValue();
    }
}
