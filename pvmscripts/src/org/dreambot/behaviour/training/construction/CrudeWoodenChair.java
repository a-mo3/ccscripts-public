package org.dreambot.behaviour.training.construction;


import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class CrudeWoodenChair extends Fractal {
    final Area RIMMINGTON_PORTAL = new Area(2951, 3228, 2956, 3220);

    public CrudeWoodenChair(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.HAMMER)
                .addItem(ItemID.SAW)
                .addItem(ItemID.PLANK, 2, 24).setRefill(100)
                .addItem(ItemID.STEEL_NAILS, 2, 200)
                .addItem(ItemID.TELEPORT_TO_HOUSE, 1, 30)
        ;
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.RING, ItemVariants.RING_OF_WEALTH);
    }

    @Override
    public int onLoop() {
        if (Client.isDynamicRegion()) {
            GameObject builtChair = GameObjects.closest("Chair");
            if (builtChair != null) {
                if (Dialogues.inDialogue()) {
                    Dialog.solve("Yes");
                    return ReactionGenerator.getNormal();
                }

                builtChair.interact("Remove");
                Sleep.sleepUntil(Dialogues::inDialogue, 1000);
                return ReactionGenerator.getNormal();
            }

            // in house make chair
            WidgetChild buildChair = getChairWidget();
            if (buildChair != null && buildChair.isVisible()) {
                buildChair.interact("Build");
                int xp = Skills.getExperience(Skill.CONSTRUCTION);
                Sleep.sleepUntil(() -> xp < Skills.getExperience(Skill.CONSTRUCTION), 4400);
                return ReactionGenerator.getNormal();
            }

            GameObject chairSpot = GameObjects.closest("Chair space");
            if (chairSpot != null) {
                chairSpot.interact("Build");
                Sleep.sleepUntil(() -> getChairWidget() != null, 4000);
            }
            return ReactionGenerator.getNormal();
        }

        if (!RIMMINGTON_PORTAL.contains(Players.getLocal())) {
            if (Widgets.isOpen()) Widgets.closeAll();
            if (Inventory.interact(ItemID.TELEPORT_TO_HOUSE, "Outside")) {
                Sleep.sleepUntil(() -> RIMMINGTON_PORTAL.contains(Players.getLocal()), 8400);
            }
            return ReactionGenerator.getNormal();
        }

        GameObject portal = GameObjects.closest("Portal");
        if (portal != null) {
            portal.interact("Build mode");
            Sleep.sleepUntil(Client::isDynamicRegion, 4400);
        }
        return ReactionGenerator.getNormal();
    }

    private WidgetChild getChairWidget() {
        return Widgets.get(x -> x.getText().equalsIgnoreCase("crude wooden chair"));
    }
}
