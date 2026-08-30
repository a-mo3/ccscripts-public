package org.dreambot.behaviour.method.gwd;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.prayer.Prayers;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

public class PlaceRopes extends Fractal {

    public PlaceRopes(boolean placeAllRopes) {
        super(() -> placeAllRopes ? PlayerSettings.getBitValue(3968) == 0 : PlayerSettings.getBitValue(3969) == 0);

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.FEET, ItemID.SARADOMIN_DHIDE_BOOTS)
                .addItem(EquipmentSlot.LEGS, ItemID.ZAMORAK_CHAPS)
                .addItem(EquipmentSlot.HANDS, ItemID.BANDOS_BRACERS)
                .addItem(EquipmentSlot.HAT, ItemID.ARMADYL_COIF)
                .addItem(EquipmentSlot.AMULET, ItemVariants.AMULET_OF_GLORY)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.LAW_RUNE, 5, 20)
                .setRefill(200)
                .addItem(ItemID.SMOKE_RUNE, 20, 200)
                .setRefill(400)
                .addItem(ItemID.SHARK, 1, 10)
                .addItem(ItemID.ROPE, 6)
                .setEnabledCondition(() -> !Inventory.contains(ItemID.ROPE) || Players.getLocal().getY() < 3500); // when not in gwd or trollheim
    }

    // gwd entrance hole
    Area GWD_ENTERANCE = new Area(2909, 3751, 2920, 3743);
    Area SARA_ROPE_ONE = new Area(2908, 5301, 2912, 5298, 2);
    Area SARA_ROPE_TWO = new Area(2917, 5281, 2921, 5276, 1);

    @Override
    public int onLoop() {
        if (Combat.isAutoRetaliateOn()) {
            if (Widgets.isOpen()) {
                log("Close widget");
                Widgets.closeAll();
            }
            log("Turn off auto realiate");
            Combat.toggleAutoRetaliate(false);
            return ReactionGenerator.getNormal();
        }

        boolean hitByWolves = Players.getLocal()
                .getCharactersInteractingWithMe()
                .stream()
                .anyMatch(x -> x.distance() < 3 && x.getName().toLowerCase().contains("wolf"));
        Prayers.toggle(hitByWolves, Prayer.PROTECT_FROM_MELEE);

        if (Combat.getHealthPercent() < 50) {
            Inventory.interact(ItemID.SHARK);
            Sleep.sleepTicks(1);
        }

        if (PlayerSettings.getBitValue(3966) == 0) {
            log("Placing entrance rope");
            return placeRope(GWD_ENTERANCE);
        }
        // dying knight
        if (PlayerSettings.getBitValue(3969) == 0) {
            if (!GWD_ENTERANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(GWD_ENTERANCE);
                return ReactionGenerator.getNormal();
            }

            if (Dialogues.inDialogue()) {
                log("solve dialog");
                Dialog.solve();
                return ReactionGenerator.getNormal();
            }

            NPC dyingKnight = NPCs.closest("Knight");
            if (dyingKnight != null) {
                log("Talk to knight");
                dyingKnight.interact("Talk-to");
                Antiban.sleepUntil(Dialogues::inDialogue, 2400);
            }

            return ReactionGenerator.getNormal();
        }

        if (PlayerSettings.getBitValue(3967) == 0) {
            log("Placing Sara Rope 1");
            return placeRope(SARA_ROPE_ONE);
        }

        if (PlayerSettings.getBitValue(3968) == 0) {
            log("Placing Sara Rope 2");
            return placeRope(SARA_ROPE_TWO);
        }
        return ReactionGenerator.getNormal();
    }

    private int placeRope(Area a) {
        if (!a.contains(Players.getLocal())) {
            slowLog("Walking there...");
            if (Walking.shouldWalk()) Walking.walk(a);
            return ReactionGenerator.getQuick();
        }

        GameObject tieRope = GameObjects.closest(x -> x.hasAction("Tie-rope"));
        if (tieRope == null) {
            log("Couldnt find obstacle to tie a rope to.");
            return ReactionGenerator.getNormal();
        }

        Item rope = Inventory.get(ItemID.ROPE);
        if (rope == null) {
            log("Missing rope?!");
            return ReactionGenerator.getNormal();
        }

        log("Putting a rope down");
        rope.useOn(tieRope);
        return ReactionGenerator.getNormal();
    }
}
