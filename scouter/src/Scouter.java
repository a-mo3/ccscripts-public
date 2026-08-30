import org.dreambot.api.Client;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.wrappers.interactive.Player;
import scout.PlayerScout;

@ScriptManifest(category = Category.MISC, name = "Panopticon", author = "camalCase", version = 0.0)
public class Scouter extends AbstractScript implements SpawnListener {

    @Override
    public int onLoop() {
//        if (!BankLocation.GRAND_EXCHANGE.getArea(50).contains(Players.getLocal())) {
//            if (Walking.shouldWalk(8)) Walking.walk(BankLocation.GRAND_EXCHANGE);
//            return Calculations.random(100, 300);
//        }

        WorldHopper.hopWorld(Worlds.getRandomWorld(x -> x.isNormal()
                && x.getMinimumLevel() < Skills.getTotalLevel()
                && (x.isMembers() == Client.isMembers())));
        return 40_000;
    }

    @Override
    public void onPlayerSpawn(Player entity) {
        new PlayerScout(entity);
    }
}