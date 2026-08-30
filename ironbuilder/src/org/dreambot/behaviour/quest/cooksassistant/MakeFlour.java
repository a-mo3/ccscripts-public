package org.dreambot.behaviour.quest.cooksassistant;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.fractals.IronFractal;
import org.dreambot.generics.BankAllItems;
import org.dreambot.generics.GenericEntityInteraction;
import org.dreambot.loadouts.InventoryLoadoutItem;
import org.dreambot.loadouts.data.ItemID;
import org.dreambot.loadouts.data.ItemSpawn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class MakeFlour extends IronFractal implements ChatListener {
    // varbit 4920 is flour ready to be collected
    boolean needsToPullLever;

    public MakeFlour(BooleanSupplier acceptCondition, int wheatCount) {
        super(acceptCondition);

        setSimpleName("Make flour");
        Client.getInstance().addEventListener(this);

        InventoryLoadoutItem pot = new InventoryLoadoutItem(ItemID.POT)
                .setInventoryMin(wheatCount)
                .setRestockMethod(new GenericEntityInteraction(ItemSpawn.LUM_KITCHEN_POT, wheatCount));

        List<Integer> allowedIds = Arrays.asList(
                ItemID.POT,
                ItemID.POT_OF_FLOUR,
                ItemID.GRAIN
        );

        addChildren(
                new BankAllItems(() -> Inventory.contains(x -> !allowedIds.contains(x.getId()))),

                new GenericEntityInteraction(() -> needsToPullLever, () -> GameObjects.closest("Hopper controls"))
                        .setEntityLocation(new Tile(3166, 3305, 2).getArea(3))
                        .setSimpleName("Pull lever"),

                new GenericEntityInteraction(() -> Inventory.contains(ItemID.GRAIN), () -> GameObjects.closest("Hopper"))
                        .setEntityLocation(new Tile(3166, 3305, 2).getArea(3))
                        .setSimpleName("Grain in hopper"),

                new GenericEntityInteraction(() -> PlayerSettings.getBitValue(4920) > 0, () -> GameObjects.closest("Flour bin"))
                        .setEntityLocation(new Tile(3166, 3305, 0).getArea(3))
                        .addInventoryItem(pot)
                        .setSimpleName("Collect flour"),

                new GenericEntityInteraction(() -> Inventory.count(ItemID.GRAIN) < wheatCount,
                        () -> GameObjects.closest("Wheat"))
                        .setEntityLocation(new Area(3157, 3300, 3162, 3293))
                        .setSimpleName("Pick grain")
        );
    }

    @Override
    public void onGameMessage(Message message) {
        // "you operate the hopper" = we can now load more in
        // "There is already grain in the hopper" = pull lever
        // "You put the grain in the hopper" = pull lever
        String m = message.getMessage();
        if (m == null) return;
        if (m.contains("You operate")) {
            needsToPullLever = false;
            return;
        }
        if (m.contains("in the hopper")) needsToPullLever = true;
    }
}
