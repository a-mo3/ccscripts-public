package org.dreambot.behaviour.training.prayer;

import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.settings.Varcs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.walking.pathfinding.impl.local.LocalPathFinder;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.muling.Log;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.List;
import java.util.function.Supplier;

public class PrayAtAltar extends Fractal {
    Supplier<WidgetChild> geTeleport = () -> Widgets.get(590, 6, 6);

    @Override
    public boolean isValid() {
        return Client.isDynamicRegion();
    }

    @Override
    public int onLoop() {
        // Client.setInteractionMode(InteractionMode.INSTANT);
        if (!Inventory.contains(ItemID.DRAGON_BONES)) {
            // teleout using glory in house or some shit
            WidgetChild geTp = geTeleport.get();
            if (geTp != null && geTp.interact("Grand Exchange")) {
                Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 2400);
                return ReactionGenerator.getNormal();
            }

            GameObject box = GameObjects.closest("Ornate Jewellery Box");
            if (Inventory.isItemSelected()) Inventory.deselect();
            if (box != null && box.interact("Teleport Menu")) {
                Sleep.sleepUntil(() -> geTeleport.get() != null, 4000);
            }

            if (box == null) {
                Log.info("No box and no bones");
                Walking.walk(BankLocation.GRAND_EXCHANGE.getTile());
            }
            return ReactionGenerator.getNormal();
        }

        GameObject altar = GameObjects.closest("Altar");
        if (altar == null) {
//            Log.info("No altar " + Client.isDynamicRegion() + " - " + Client.getGameStateID() + " " + Client.isLoggedIn());
//            Walking.walk(BankLocation.GRAND_EXCHANGE.getTile());
            return ReactionGenerator.getNormal();
        }

//        List<Tile> path = PathUtility.generateLocalPath(altar.getTile().translate(2, 2));
        // todo troll houses are now back
        List<Tile> path = null;
        if (path != null && path.size() > 20) {
            addHouseToBlacklist();
            GameObject portal = GameObjects.closest("Portal");
            if (portal != null && portal.interact("Enter")) {
                Sleep.sleepUntil(Client::isDynamicRegion, 4400);
            }
            return ReactionGenerator.getNormal();
        }

        Item bone = Inventory.get(ItemID.DRAGON_BONES);
        log("Praying @ altar: " + altar + "Dist: " + altar.tileDistance(Players.getLocal().getTile()));
        int pathSize = LocalPathFinder.getLocalPathFinder().calculate(Players.getLocal().getTile(), altar.getTile()).size();
        log("Path size " + pathSize);
        if (pathSize > 50) {
            log("Long ass blacklisting this house");
            addHouseToBlacklist();
        }
        if (bone.useOn(altar)) {
            Sleep.sleep(300, 800);
        }
        return ReactionGenerator.getNormal();
    }

    private void addHouseToBlacklist() {
        String homeOwner = Varcs.getString(361); // 🧙 client str for last entered house
        if (!GotoHouse.blacklistedOwners.contains(homeOwner)) {
            GotoHouse.blacklistedOwners.add(homeOwner);
            log("Blacklisted " + homeOwner);
        }
    }
}
