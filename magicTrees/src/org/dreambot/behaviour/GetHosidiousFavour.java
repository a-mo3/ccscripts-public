package org.dreambot.behaviour;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.settings.timing.ReactionGenerator;

public class GetHosidiousFavour extends Fractal {
    private final Area VERTICAL_PLOUGH = new Area(1761, 3540, 1779, 3521);

    private final int PREFERRED_X = 1766; // todo make this set off shuffle fractal login code so it scales better

    public static final int KOUREND_FAVOR_HOSIDIUS = 4895;

    public GetHosidiousFavour() {
        this.acceptCondition = () -> PlayerSettings.getBitValue(KOUREND_FAVOR_HOSIDIUS) <= 750;
//        this.acceptCondition = () -> true;
        this.inventoryLoadout = new InventoryLoadout()
                .addItem(ItemID.HAMMER)
        ;
    }

    @Override
    public int onLoop() {
        if (!VERTICAL_PLOUGH.contains(Players.getLocal())) {
            if (Walking.shouldWalk()) Walking.walk(VERTICAL_PLOUGH.getCenter());
            return ReactionGenerator.getNormal();
        }

        NPC plough = NPCs.closest(x -> x.getX() == PREFERRED_X && x.getName().contains("Plough"));
        if (plough == null) {
            Logger.info("Failed to find plough");
            return ReactionGenerator.getNormal();
        }

        if (plough.hasAction("Repair")) {
            Logger.info("Repairing plough");
            if (plough.interact("Repair")) {
                Sleep.sleepUntil(() -> !plough.hasAction("Repair"), 3600);
            }
            return ReactionGenerator.getNormal();
        }

        // push from north side
        if (plough.getOrientation() > 0) {
            // walk to north side
            Tile myTile = Players.getLocal().getTile();
            Tile correctTile = plough.getTile().translate(0, 2);
            if (!myTile.equals(correctTile)) {
                if (Walking.shouldWalk(1)) Walking.walk(correctTile);
                return ReactionGenerator.getLong();
            }

            if (plough.interact("Push")) {
                Sleep.sleepUntil(() -> plough.hasAction("Repair"), () -> Players.getLocal().isMoving(), 3600, 100);
            }
            return ReactionGenerator.getNormal();
        }


        Tile myTile = Players.getLocal().getTile();
        Tile correctTile = plough.getTile().translate(0, -2);
        if (!myTile.equals(correctTile)) {
            if (Walking.shouldWalk(6)) Walking.walk(correctTile);
            return ReactionGenerator.getLong();
        }

        if (plough.interact("Push")) {
            Sleep.sleepUntil(() -> plough.hasAction("Repair"), () -> Players.getLocal().isMoving(), 2600, 100);
        }
        return ReactionGenerator.getNormal();
    }
}
