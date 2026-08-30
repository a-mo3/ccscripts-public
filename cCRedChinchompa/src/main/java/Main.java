import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.behaviour.impl.RedChinNode;
import org.dreambot.config.Config;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.listener.ListenerManager;
import org.dreambot.listener.events.ObjectEvent;
import org.dreambot.listener.impl.ObjectListener;

import java.awt.*;
import java.util.Map;

@ScriptManifest(category = Category.HUNTING,
        name = "cCRedChinchompas",
        author = "camalCase",
        version = 1.1,
        image = "https://i.imgur.com/pTJUT4y.png",
        description = "Catches red chins in feldip hills")


public class Main extends TaskScript implements ObjectListener, ItemContainerListener, ChatListener {
    private final Config config = Config.getConfig();
    private final Timer timer = new Timer();

    @Override
    public void onStart() {
        addNodes(new RedChinNode());
        ListenerManager.getInstance().addListener(new ObjectEvent(this));
    }

    @Override
    public void onPaint(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 340, 515, 140);
        g.setColor(Color.GREEN);
        g.drawString("cCRedChinchompas - " + config.getSubStatus(), 10, 360);
        g.drawString("Runtime: " + timer.formatTime(), 395, 360);
        g.drawString("Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER), 395, 380);

        g.drawString("Chins caught: " + config.getRedChinCount(), 10, 380);
        g.drawString("Traps down: " + config.getTrapMap().size() + " / " + getTrapLimit(), 10, 400);
        for (Map.Entry<Tile, GameObject> entry : config.getTrapMap().entrySet()) {
            Tile tile = entry.getKey();
            if (tile != null) {
                g.drawPolygon(tile.getPolygon());
            }
        }

    }

    @Override
    public void onObjectSpawn(GameObject object) {
        if (object.getName().contains("trap")) {
            if (Players.getLocal().getTile().equals(object.getTile())) {
                // this is to stop boxes failing to catch getting added
                if (object.getID() == 9380) {
                    Logger.log("----------------------------------------------");
                    Logger.log("Ani: " + Players.getLocal().getAnimation());
                    Logger.log("Dist: " + object.distance(Players.getLocal()));
                    Logger.log("----------------------------------------------");
                    if (Players.getLocal().getAnimation() == 5208) {
                        config.trapMapPut(object);
                    }
                }
            }
        }
    }

    @Override
    public void onObjectRemove(GameObject object) {
    }

    @Override
    public void onInventoryItemAdded(Item item) {
        if (item.getID() == ItemID.RED_CHINCHOMPA) {
            config.setRedChinCount(config.getRedChinCount() + 1);
        }
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        if (incoming.getID() == ItemID.RED_CHINCHOMPA) {
            config.setRedChinCount(config.getRedChinCount() + 1);
        }
    }

    private int getTrapLimit() {
        int level = Skills.getRealLevel(Skill.HUNTER);
        if (level < 20) return 50;
        if (level < 40) return 2;
        if (level < 60) return 3;
        if (level < 80) return 4;
        return 5;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("set up only") && !message.getMessage().toLowerCase().contains("congratulations")) {
            Logger.log("Box tracking failure, this is normally caused by lag, dismissing random events or being crashed by another player");
            config.setFailSafe(true);
        }
    }
}
