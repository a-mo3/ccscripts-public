package org.dreambot.emirs;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class DoFight extends Fractal {
    public DoFight() {
        super(Client::isDynamicRegion);
    }

    @Override
    public int onLoop() {
        GameObject portal = GameObjects.closest("Portal");
        if (portal != null && portal.distance() < 6 && portal.canReach()) {
            log("Exit portal");
            portal.interact("Exit");
            Sleep.sleepUntil(() -> !Client.isDynamicRegion(), 4000);
            return ReactionGenerator.getNormal();
        }

        if (!hasSetReady()) {;
            if (Widgets.isOpen()) {
                log("Widgets open");
                WidgetChild setReadyWidget = Widgets.get(758, 3, 1);
                if (setReadyWidget != null) {
                    log("Set ready");
                    setReadyWidget.interact();
                    Sleep.sleepUntil(this::hasSetReady, 4000);
                }
                return ReactionGenerator.getNormal();
            }

            GameObject chest = GameObjects.closest("Staging area supplies");
            if (chest != null) {
                chest.interact("Use");
                Sleep.sleepUntil(Widgets::isOpen, 3400);
            }
            return ReactionGenerator.getNormal();
        }

        // do fight / run away

        // todo logic for letting one person get a streak
        if (!Magic.isAutocasting()) {
            log("Set autocast");
            Magic.setAutocastSpell(Normal.FIRE_SURGE);
            return ReactionGenerator.getNormal();
        }

        log("attack a guy");
        Player p = Players.closest(x -> !x.equals(Players.getLocal()) && !x.isInCombat());
        if (p != null) {
            p.interact("Attack");
            Sleep.sleep(10_000);
        }

        if (p == null && !Players.getLocal().isInCombat()) {
            GameObject leave = GameObjects.closest(x -> x.hasAction("Leave"));
            if (leave != null) leave.interact("Leave");
        }
        return ReactionGenerator.getNormal();
    }

    private boolean hasSetReady() {
        return PlayerSettings.getBitValue(14014) == 1;
    }
}
