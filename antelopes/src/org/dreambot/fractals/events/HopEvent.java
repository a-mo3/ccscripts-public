package org.dreambot.fractals.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * alternative to quick hop, log off and set world
 */
public class HopEvent extends AbstractResponseEvent<HopEvent.Response> {
    public enum Response {
        LOGGED_OFF,
        ATTACKED
    }

    @Override
    protected void onStart() {
        Logger.info("Turn off login solver");
        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);
        allowLogout = true;
    }

    @Override
    public int onLoop() {
        if (Players.getLocal().isHealthBarVisible()) {
            setResponse(Response.ATTACKED);
            return ReactionGenerator.getQuick();
        }
        
        if (!Client.isLoggedIn()) {
            World w = Worlds.getRandomWorld(x -> !x.isF2P()
                    && x.getMinimumLevel() < Skills.getTotalLevel()
                    && x.isNormal()
            );
            Logger.info("Change world direct " + w);
            WorldHopper.changeWorldDirect(w);
            setResponse(Response.LOGGED_OFF);
            return ReactionGenerator.getNormal();
        }

        if (!Menu.isMenuManipulationActive()) {
            Logger.info("Menu manip disabled - anti pk will be slow");
            Client.setIdleTime(30_000);
            return ReactionGenerator.getNormal();
        }

        WidgetChild logoutButton = Widgets.get(x -> x.hasAction("Logout") && (x.getParentID() == 182 || x.getParentID() == 69) );
        if (logoutButton == null) {
            Logger.info("Open the tab");
            WidgetChild wc = Tab.LOGOUT.getWidgetChild();
            wc.interact();
        }

        logoutButton = Widgets.get(x -> x.hasAction("Logout") && (x.getParentID() == 182 || x.getParentID() == 69));
        if (logoutButton != null) logoutButton.interact();
        return ReactionGenerator.getQuick();
    }
    @Override
    protected void onExit() {
        Logger.info("turn on login solver");
        Client.getInstance().getRandomManager().enableSolver(RandomEvent.LOGIN);
    }
}
