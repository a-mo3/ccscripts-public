package org.dreambot.behaviour.method.antelope;

import org.dreambot.api.input.Keyboard;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.widget.helpers.ItemProcessing;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.scriptdata.MoonlightAntelopeSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class MoonlightAntelopes extends Fractal {
    public MoonlightAntelopes(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Moonlight Antelopes");

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.CHISEL)
                .addItem(ItemID.KNIFE)
                .addItem(ItemID.BRONZE_AXE)
                .addItem(ItemID.TEASING_STICK)
                .addItem(ItemVariants.STAMINA_POTION, 4, 4)
                .setEnabledCondition(() -> !Inventory.contains(x -> ItemVariants.STAMINA_POTION.contains(x.getId())))
                .addItem(ItemID.JUG_OF_WINE, 12)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.JUG_OF_WINE) || Inventory.count(ItemID.JUG_OF_WINE) > 12)
                .setRefill(100)
                .strictIgnore(ItemID.LOGS, ItemID.MOONLIGHT_ANTELOPE_ANTLER, ItemID.MOONLIGHT_ANTLER_BOLTS,
                        ItemID.MOONLIGHT_ANTELOPE_FUR, ItemID.RAW_MOONLIGHT_ANTELOPE)
                .setStrictSupplier(() -> Inventory.contains(ItemID.COINS_995) || !Inventory.contains(ItemID.JUG_OF_WINE))
        ;

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
        ;

        this.paintArraySupplier = () -> {
            NPC baitedAntelope = (NPC) Players.getLocal().getCharactersInteractingWithMe().stream()
                    .filter(x -> x instanceof NPC)
                    .filter(x -> x.getName().toLowerCase().contains("antelope"))
                    .findFirst().orElse(null);

            GameObject northPit = GameObjects.closest(51676);

            NPC onTrap = NPCs.closest(x -> x.getTile().equals(northPit));
            return new String[]{
                    "Antelope " + (baitedAntelope == null ? "-" : baitedAntelope.getName()),
                    "Antelope T: " + (baitedAntelope == null ? "-" : baitedAntelope.getTile().toString()),
                    "Trap: " + (northPit == null ? "-" : northPit.getTile().toString()),
                    "Antelope A: " + (baitedAntelope == null ? "-" : baitedAntelope.getAnimation()),
                    "onTrap " + onTrap
            };
        };

    }

    public static final Area ANTELOPE_AREA = new Area(1551, 9430, 1568, 9412);

    // after jumping we dont want to bait be able to bait an antelope for 2400,
    Timer baitTimer = new Timer(2400);

    @Override
    public int onLoop() {
        if (Widgets.isOpen()) {
            log("Close all widget");
            Widgets.closeAll();
        }

        if (Dialogues.canEnterInput()) {
            log("Enter dialogue handle");
            Keyboard.type(" 1", true);
            return ReactionGenerator.getNormal();
        }

        // eat
        int missingHp = Skills.getRealLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
        if (missingHp >= 10 || Skills.getBoostedLevel(Skill.HITPOINTS) < 7) {
            log("Drinking a wine");
            Inventory.interact(ItemID.JUG_OF_WINE, "Drink");
            return ReactionGenerator.getNormal();
        }

        // drop empty wines / bones / meat
        if (Inventory.contains(ItemID.JUG, ItemID.RAW_MOONLIGHT_ANTELOPE, ItemID.BIG_BONES, ItemID.MOONLIGHT_ANTELOPE_FUR)) {
            log("Drop all garbage");
            Inventory.dropAll(ItemID.JUG, ItemID.RAW_MOONLIGHT_ANTELOPE, ItemID.BIG_BONES, ItemID.MOONLIGHT_ANTELOPE_FUR);
            return ReactionGenerator.getNormal();
        }

        NPC baitedAntelope = (NPC) Players.getLocal().getCharactersInteractingWithMe().stream()
                .filter(x -> x instanceof NPC)
                .filter(x -> x.getName().toLowerCase().contains("antelope"))
                .findFirst().orElse(null);

        // fletch
        if (baitedAntelope == null && Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_ANTLER)
                && SettingsRepository.findInstanceOf(new MoonlightAntelopeSettings()).fletchBolts) {
            log("Fletching antlers to save space");
            if (ItemProcessing.isOpen()) {
                ItemProcessing.makeAll(ItemID.MOONLIGHT_ANTLER_BOLTS);
                Sleep.sleepUntil(() -> !Inventory.contains(ItemID.MOONLIGHT_ANTELOPE_ANTLER), 4400);

                return ReactionGenerator.getNormal();
            }

            Inventory.combine(ItemID.CHISEL, ItemID.MOONLIGHT_ANTELOPE_ANTLER);
            Sleep.sleepUntil(ItemProcessing::isOpen, 2400);
            return ReactionGenerator.getQuick();
        }

        // go to antelopes
        if (!ANTELOPE_AREA.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(ANTELOPE_AREA);
            return ReactionGenerator.getNormal();
        }

        Item stamina = ItemVariants.STAMINA_POTION.getItem();
        if (stamina != null && Walking.getRunEnergy() < 10) {
            stamina.interact("Drink");
            Sleep.sleepUntil(() -> Walking.getRunEnergy() > 10, 2400);
        }

        if (Walking.getRunEnergy() > 10 && !Walking.isRunEnabled()) Walking.toggleRun();

        GameObject northPit = GameObjects.closest(51678);
        if (northPit == null) {
            log("Failed to find pit.");
            return ReactionGenerator.getNormal();
        }

        // loot an antelope
        GameObject collapsedTrap = GameObjects.closest(x -> x.hasAction("Dismantle")
                && x.getName().equals("Collapsed trap"));
        if (collapsedTrap != null) {
            if (Inventory.emptySlotCount() < 3) {
                log("Inventory is full when trying to dismantle, drop wine");
                Inventory.drop(ItemID.JUG_OF_WINE);
            }

            log("Dismantling trap");
            collapsedTrap.interact("Dismantle");
            Sleep.sleepUntil(() -> !collapsedTrap.exists(), 3400);
            return ReactionGenerator.getNormal();
        }

        boolean isTrapLayed = northPit.getName().contains("Spiked");
        if (baitedAntelope != null && isTrapLayed) {
            log("Bait antelope");
            // the antelope can get stuck if its on the side on the pit
            // here we walk to the appropriate side to unstuck it
            // todo consider pit orientation when we have multiple pit options
            // todo something for if antelope is roughly same Y but on the other side of the pit
            int baitedYDiff = Players.getLocal().getY() - baitedAntelope.getY();
            baitedYDiff *= baitedYDiff < 0 ? -1 : 1;
            if (baitedYDiff < 3 && !baitedAntelope.isMoving() && baitedAntelope.distance() > 6) {
                log("Antelope is far away & not moving probably stuck");
                Walking.walk(Players.getLocal().getTile().translate(0, 7));
                // i want to just sleep for a while here but we should loop if we're low hp
                Sleep.sleepUntil(() -> Combat.getHealthPercent() < 50, 3000);
                return ReactionGenerator.getNormal();
            }

            Tile northBase = northPit.getTile().clone().translate(0, 3);
            Tile northCorner = northPit.getTile().clone().translate(1, 4);
            Area northArea = new Area(northBase, northCorner);

            Tile southBase = northPit.getTile().clone().translate(0, -2);
            Tile southCorner = northPit.getTile().clone().translate(1, -3);
            Area southArea = new Area(southBase, southCorner);
            if (northArea.contains(baitedAntelope) || southArea.contains(baitedAntelope)) {
                log("Unstuck the antelope walk to the side");
                Walking.walkExact(new Tile(
                        // if you are to the right
                        Players.getLocal().getX() > northPit.getX() ? northPit.getX() + 4 : northPit.getX() - 4,
                        northPit.getY()
                ));
                Sleep.sleepUntil(() -> !northArea.contains(baitedAntelope), 3500);
                return ReactionGenerator.getQuick();
            }

            northPit.interact("Jump");
            log("Jumped and waiting for catch");
            Sleep.sleepUntil(() -> {
                        if (northArea.contains(baitedAntelope) || southArea.contains(baitedAntelope)) return true;

                        GameObject n = GameObjects.closest(x -> x.hasAction("Dismantle")
                                && x.getName().equals("Collapsed trap"));
                        return n != null && n.getName().contains("Collapsed");
                    },
                    10_500); // todo this sleep isnt breaking instantly when the pit transforms
            log("Sleep end");
            baitTimer.reset();
            return ReactionGenerator.getQuick();
        }

        // bait & trap
        if (baitedAntelope == null && isTrapLayed && baitTimer.finished()) {
            NPC antelopeNpc = NPCs.closest(x -> x.getName().equals("Moonlight antelope") && x.getInteractingCharacter() == null, northPit.getTile());
            Logger.info("Interacting with " + antelopeNpc);
            if (antelopeNpc != null && antelopeNpc.interact("Tease")) {
                Sleep.sleepUntil(() -> Players.getLocal().getCharacterInteractingWithMe() != null, 4600);
                return ReactionGenerator.getNormal() + 300;
            }
            return ReactionGenerator.getNormal() + 300;
        }

        // get wood
        if (!Inventory.contains(ItemID.LOGS) && !isTrapLayed) {
            GameObject tree = GameObjects.closest(x -> x.getName().equals("Roots") && x.hasAction("Take-log"));
            log("Take logs " + tree);
            if (tree != null) {
                tree.interact("Take-log");
                Sleep.sleepUntil(() -> Inventory.contains(ItemID.LOGS) || !tree.exists(), 4600, 100);
            }
            return ReactionGenerator.getNormal();
        }

        // set the trap
        log("Lay trap");
        if (northPit.distance() > 8 && !Menu.isMenuManipulationActive()) {
            if (Walking.shouldWalk()) Walking.walk(northPit);
            return ReactionGenerator.getNormal();
        }
        if (northPit.hasAction("Trap") && northPit.interact("Trap")) {
            Sleep.sleepUntil(() -> !northPit.exists() || northPit.getName().toLowerCase().contains("spiked"), 2400);
        }

        return ReactionGenerator.getNormal();
    }
}
