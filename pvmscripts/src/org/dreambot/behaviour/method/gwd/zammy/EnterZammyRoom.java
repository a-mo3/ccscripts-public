package org.dreambot.behaviour.method.gwd.zammy;

import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.fractals.TickDecision;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.util.ObjectUtil;

public class EnterZammyRoom extends TickDecision {
    final Area ZAMMY_ROOM = new Area(2917, 5332, 2937, 5317, 2);
    final Tile ENTRANCE = new Tile(2925, 5333, 2);

    @Override
    public boolean evaluate() {
        if (ZAMMY_ROOM.contains(Players.getLocal())) return false;

        if (!ENTRANCE.equals(Players.getLocal().getTile())) {
            log("Get onto entrance");
            if (Walking.shouldWalk()) Walking.walk(ENTRANCE);
            return true;
        }

        // todo maybe pot up
        if (Inventory.contains(ItemID.RUNE_CROSSBOW)) {
            log("Equip cbow");
            Inventory.interact(ItemID.RUNE_CROSSBOW);
        }

        if (Combat.getCombatStyle() != CombatStyle.RANGED_RAPID) {
            log("Set rapid");
            Combat.setCombatStyle(CombatStyle.RANGED_RAPID);
        }

        log("Enter zammy room.");
        ObjectUtil.interact("Big door");
        return true;
    }
}
