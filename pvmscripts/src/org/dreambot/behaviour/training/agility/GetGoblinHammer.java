package org.dreambot.behaviour.training.agility;

import org.dreambot.api.methods.container.impl.Shop;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

public class GetGoblinHammer extends Fractal {
    Area DRAYNOR_MARKET = new Area(3075, 3256, 3087, 3245);

    public GetGoblinHammer(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
        setSimpleName("Goblin hammer");
        inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.COINS_995, 150, 2000);
    }

    @Override
    public int onLoop() {
        if (Shop.isOpen()) {
            log("Buy 3 hammers");
            Shop.purchase(ItemID.CURSED_GOBLIN_HAMMER, 5);
            return ReactionGenerator.getNormal();
        }

        NPC diango = NPCs.closest("Diango");
        if (diango == null) {
            log("Go to draynor");
            if (Walking.shouldWalk()) Walking.walk(DRAYNOR_MARKET);
            return ReactionGenerator.getNormal();
        }

        diango.interact("Trade");
        Sleep.sleepUntil(Shop::isOpen, 4400);
        return ReactionGenerator.getNormal();
    }
}
