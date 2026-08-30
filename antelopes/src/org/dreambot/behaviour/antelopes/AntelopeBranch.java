package org.dreambot.behaviour.antelopes;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.function.Supplier;

public class AntelopeBranch extends Fractal implements ItemContainerListener {
    public static int lootValue = 0;
    public AntelopeBranch(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        Client.getInstance().addEventListener(this);

        addChildren(
                new EatFood().setSimpleName("Eat"),
                new FletchBolts().setSimpleName("Fletch"),
                new BankAntelopes().setSimpleName("Deposit"),
                new GotoAntelopes().setSimpleName("Go to antelopes"),
                new ScavangeAntelopes().setSimpleName("Scavenge"),
                new LootAntelopes().setSimpleName("Loot"),
                new BaitAntelope().setSimpleName("Bait"),
                new SetTraps().setSimpleName("Set traps")
        );
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (GotoAntelopes.ANTELOPE_AREA.contains(Players.getLocal())) {
            if (item.getID() == ItemID.MOONLIGHT_ANTELOPE_ANTLER) lootValue += item.getLivePrice();
            if (item.getID() == ItemID.RAW_MOONLIGHT_ANTELOPE) lootValue += item.getLivePrice();
        }
    }
}
