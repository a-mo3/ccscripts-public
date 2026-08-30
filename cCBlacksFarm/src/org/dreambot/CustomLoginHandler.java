package org.dreambot;

import org.dreambot.api.Client;
import org.dreambot.api.methods.RSLoginResponse;
import org.dreambot.api.methods.combat.Combat;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.List;

/**
 * a login handler that hops worlds to a normal world on an adverse response
 */
public class CustomLoginHandler extends RandomSolver {
    Filter<World> freeNormalWorldFilter = w -> w.isNormal() && w.getMinimumLevel() == 0 && w.isF2P();
    Filter<World> membersNormalWorldFilter = w -> w.isNormal() && w.getMinimumLevel() == 0 && w.isMembers();

    List<RSLoginResponse> stopResponses = Arrays.asList(
            RSLoginResponse.DISABLED,
            RSLoginResponse.UNSUCCESSFUL_LOGIN,
            RSLoginResponse.UPDATED,
            RSLoginResponse.ADDRESS_BLOCKED
    );

    public CustomLoginHandler(String eventString) {
        super(eventString);
    }

    @Override
    public boolean shouldExecute() {
        return !Client.isLoggedIn();
    }

    @Override
    public int onLoop() {
        RSLoginResponse res = LoginUtility.getResponse();
        if (stopResponses.contains(res)) {
            Logger.info("Stopping script login response: " + res);
            ScriptManager.getScriptManager().stop();
            return ReactionGenerator.getLong();
        }

        if (res == RSLoginResponse.MEMBERS_WORLD || res == RSLoginResponse.MEMBERS_WORLD_2) {
            Logger.info("Members world, hopping to a normal f2p world");
            World w = Worlds.getRandomWorld(freeNormalWorldFilter);
            Logger.info("Picked world " + w.getWorld());
            WorldHopper.changeWorldDirect(w);
            Logger.info("Logging in " + w.getWorld());
            LoginUtility.login();
            LoginUtility.login();
            LoginUtility.login();
            LoginUtility.login();
            return ReactionGenerator.getNormal();
        }

        Logger.info("Hopping to a normal members world");
        World w = Worlds.getRandomWorld(membersNormalWorldFilter);
        Logger.info("Picked world " + w.getWorld());
        WorldHopper.changeWorldDirect(w);
        // just login
        LoginUtility.login();
        Sleep.sleepUntil(Client::isLoggedIn, 1_000);
        return ReactionGenerator.getNormal();
    }
}
