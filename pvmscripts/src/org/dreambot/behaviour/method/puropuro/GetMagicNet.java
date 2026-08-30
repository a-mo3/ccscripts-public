package org.dreambot.behaviour.method.puropuro;

import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetMagicNet extends Fractal {
    public GetMagicNet(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Get Magic Net");

        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH)
        ;

        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.GOURMET_IMPLING_JAR, 3)
                .addItem(ItemID.EARTH_IMPLING_JAR, 2)
                .addItem(ItemID.ESSENCE_IMPLING_JAR, 1)
        ;
    }

    Tile ELNOCK_LOC = new Tile(2586, 4314);
    String CONFIRM_TEXT = "Confirm: <col=ffffff>Magic butterfly net x1";

    @Override
    public int onLoop() {
        NPC elnock = NPCs.closest("Elnock Inquisitor");
        if (elnock == null) {
            log("No elnock here");
            if (Walking.shouldWalk()) Walking.walk(ELNOCK_LOC);
            return ReactionGenerator.getNormal();
        }

        if (!Widgets.isOpen()) {
            log("Open elnocks store");
            elnock.interact("Trade");
            Sleep.sleepUntil(Widgets::isOpen, 4000);
            return ReactionGenerator.getNormal();
        }

        WidgetChild confirmWidget = Widgets.get(x -> CONFIRM_TEXT.equals(x.getText()));
        if (confirmWidget != null) {
            log("Confirm buy magic net");
            confirmWidget.interact("Confirm");
            return ReactionGenerator.getNormal();
        }

        WidgetChild selectWidget = Widgets.get(540, 4, 3);
        if (selectWidget != null) {
            log("Selecting magic net ");
            selectWidget.interact();
        } else {
            log("Failed to select magic net");
        }


        return ReactionGenerator.getNormal();
    }
}
