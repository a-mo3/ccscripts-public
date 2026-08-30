package org.dreambot.behaviour.method.corp.behaviour;

import org.dreambot.api.Client;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.map.Region;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.behaviour.method.corp.CorpClient;
import org.dreambot.behaviour.method.corp.messages.CorpRole;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.ItemVariants;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.muling.impl.MuleRequestEvent;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.function.Supplier;

/**
 * We need to hop to the corp would, go to corp w/ games necklace
 * probably enforcing loadout here
 * then enter private clan instance
 * enter the fight room
 * <p>
 * after that the spec or normal fight decicions can take over
 */
public class GetIntoCorpFight extends Fractal {
    // in the part of the corp arena where corp is.
    final Area CORP_INSIDE = new Area(2974, 4397, 2998, 4370, 2);

    public GetIntoCorpFight(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public int onLoop() {
        int corpWorld = CorpClient.getCorpWorld();
        if (Worlds.getCurrentWorld() != corpWorld) {
            log("Needs to hop to corp team world " + corpWorld);
            if (Widgets.isOpen()) Widgets.closeAll();
            WorldHopper.hopWorld(corpWorld);
            return ReactionGenerator.getNormal();
        }

        if (!Client.isDynamicRegion()) {

            WidgetChild needMoreMoney = Widgets.get(x -> x.getText().contains("You need 200,000 x Coins to open this instance."));
            if (needMoreMoney != null) {
                log("Needs more money to start instance.");
                new MuleRequestEvent("corp start instance money")
                        .addRequiredItem(ItemID.COINS_995, 250_000)
                        .execute();
                return ReactionGenerator.getNormal();
            }

            // check for portal before games necklace, necklace could expire when he tp to corp
            GameObject privPortal = GameObjects.closest("Private portal");
            if (privPortal != null) {
                log("Enter private portal");
                if (Dialogues.inDialogue()) {
                    log("Handle yes dialogue");
                    Dialog.solve("Yes");
                    return ReactionGenerator.getNormal();
                }
                privPortal.interact();
                return ReactionGenerator.getNormal();
            }

            Item gamesNeck = ItemVariants.GAMES_NECKLACE.getItem();
            if (gamesNeck == null) {
                Logger.warn("We're expecting games necklace here but dont have one, open ccscripts bug ticket");
                return ReactionGenerator.getNormal();
            }

            log("TP to corp.");
            if (Widgets.isOpen()) Widgets.closeAll();
            gamesNeck.interact("Corporeal Beast");
            return ReactionGenerator.getLong() + 3000;
        }

        Tile toNonInstanced = Region.fromInstance(Players.getLocal().getTile());
        if ( !CORP_INSIDE.contains(toNonInstanced)) {
            log("Go into the corp arena");
            if (CorpClient.getRole() == CorpRole.HOST) {
                log("Idle, we're host");
                Client.setIdleTime(1);
                return 10_000;
            }
            GameObject passage = GameObjects.closest("Passage");
            if (passage != null) {
                log("Go into passage");
                passage.interact();
            } else {
                log("Failed to find passage");
            }
        }

        return ReactionGenerator.getNormal();
    }
}
