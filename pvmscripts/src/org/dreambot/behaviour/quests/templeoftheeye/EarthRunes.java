package org.dreambot.behaviour.quests.templeoftheeye;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.EquipmentLoadout;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class EarthRunes extends Fractal {
    public static final Area MUD_ALTAR = new Area(2648, 4849, 2668, 4827);
    final Area MUD_ALTAR_ENTRANCE = new Area(3294, 3478, 3299, 3473);

    public EarthRunes(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        this.equipmentLoadout = new EquipmentLoadout()
                .addItem(EquipmentSlot.HAT, ItemID.EARTH_TIARA)
                .setBuyPrice(2000);
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.PURE_ESSENCE, 27).setRefill(200).setBuyPrice(4)
                .setStrict(true);
    }

    @Override
    public int onLoop() {
        if (!MUD_ALTAR.contains(Players.getLocal())) {
            if (!MUD_ALTAR_ENTRANCE.contains(Players.getLocal())) {
                if (Walking.shouldWalk()) Walking.walk(MUD_ALTAR_ENTRANCE.getTile());
                return ReactionGenerator.getNormal();
            }

            GameObject ruins = GameObjects.closest("Mysterious ruins");
            if (ruins != null) {
                ruins.interact("Enter");
                Sleep.sleepUntil(() -> MUD_ALTAR.contains(Players.getLocal()), 14_400);
            }
            return ReactionGenerator.getNormal();
        }

        Item essence = Inventory.get(ItemID.PURE_ESSENCE);
        GameObject altar = GameObjects.closest("Altar");
        if (essence != null && altar != null) {
            essence.useOn(altar);
            Sleep.sleepUntil(() -> !Inventory.contains(ItemID.PURE_ESSENCE), 4400);
            return ReactionGenerator.getNormal();
        }
        return ReactionGenerator.getNormal();
    }
}
