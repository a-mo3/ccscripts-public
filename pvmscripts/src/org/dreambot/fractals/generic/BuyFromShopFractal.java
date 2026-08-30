package org.dreambot.fractals.generic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

@Accessors(chain = true)
public class BuyFromShopFractal extends Fractal {
    final String npcName;
    final Filter<Item> purchaseFilter;
    final Area area;
    Supplier<Entity> entitySupplier;
    @Setter
    String action = "Trade";
    @Setter
    int quantity = 1;

    public BuyFromShopFractal(Supplier<Boolean> acceptCondition, String npcName, Area area, int itemID) {
        super(acceptCondition);
        this.npcName = npcName;
        this.area = area;
        this.purchaseFilter = x -> x.getId() == itemID;
    }

    public BuyFromShopFractal(Supplier<Boolean> acceptCondition, Supplier<Entity> entitySupplier, Area area, int itemID) {
        super(acceptCondition);
        this.npcName = "";
        this.entitySupplier = entitySupplier;
        this.area = area;
        this.purchaseFilter = x -> x.getId() == itemID;
    }

    public BuyFromShopFractal(Supplier<Boolean> acceptCondition, String npcName, Area area, Filter<Item> itemFilter) {
        super(acceptCondition);
        this.npcName = npcName;
        this.area = area;
        this.purchaseFilter = itemFilter;
    }

    @Override
    public int onLoop() {
        if (Dialogues.inDialogue()) Dialog.solve();
        if (Shop.isOpen()) {
            Shop.purchase(purchaseFilter, quantity);
            Sleep.sleep(1200);
            return ReactionGenerator.getNormal();
        }

        if (!area.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(area);
            return ReactionGenerator.getNormal();
        }

        Entity shopkeep = entitySupplier == null ? NPCs.closest(npcName) : entitySupplier.get();
        if (shopkeep != null && shopkeep.interact(action)) {
            Antiban.sleepUntil(Shop::isOpen, 4400);
        }
        return ReactionGenerator.getNormal();
    }
}
