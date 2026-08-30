package org.dreambot;

import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.grandexchange.GrandExchange;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.walking.pathfinding.impl.web.WebFinder;
import org.dreambot.api.methods.walking.web.node.AbstractWebNode;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.script.listener.ItemContainerListener;
import org.dreambot.api.script.listener.SpawnListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.config.Config;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.FractalAPI;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.data.ObjectID;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.settings.SettingsLoader;
import org.dreambot.settings.script.ScriptSettings;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionGenerator;
import org.dreambot.behaviour.BehaviourUtils;
import org.dreambot.behaviour.impl.AntiPkNode;
import org.dreambot.behaviour.impl.BlackChinsNode;
import org.dreambot.behaviour.impl.EdgevilleBankLeaf;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;


@ScriptManifest(category = Category.HUNTING, name = "cCBlackChinChompa", author = "camalCase", version = 1.0)
public class Main extends TaskScript implements SpawnListener, ItemContainerListener, ChatListener, PaintInfo {
    private final Config config = Config.getConfig();
    private final Timer timer = new Timer();
    private final FluffeesPaint PAINT = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    private final Fractal tree = new Fractal().setSimpleName("cCBlacks");
    public static final Area SCARY_DRAGONS_EEEP = new Area(3116, 3722, 3172, 3691);

    @Override
    public void onStart() {
        for (AbstractWebNode node : WebFinder.getWebFinder().getAll()) {
            if (SCARY_DRAGONS_EEEP.contains(node.getTile()))
                node.removeConnections(node.getConnections().toArray(new AbstractWebNode[0]));
        }
        tree.addChildren(
//                new FeroxBankNode().setSimpleName("Bank"),
                new EdgevilleBankLeaf().setSimpleName("EdgeBank"),
                new AntiPkNode().setSimpleName("AntiPK"),
                new BlackChinsNode().setSimpleName("Blacks")
        );


        SettingsLoader<SettingsData> settingsLoader = new SettingsLoader<>(SettingsData.class);
        SettingsData settings = settingsLoader.loadFile("settings.json", ScriptSettings.getSettingsData());
        ScriptSettings.setSettingsData(settings);
        new WebhookListener();
    }

    @Override
    public int onLoop() {
        if (ClientSettings.isAcceptAidEnabled()) {
            Logger.info("Toggling off accept aid");
            if (Bank.isOpen()) Bank.close();
            ClientSettings.toggleAcceptAid(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isSellPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleSellPriceWarning(false);
            return ReactionGenerator.getNormal();
        }

        if (ClientSettings.isBuyPriceWarningEnabled()) {
            if (Bank.isOpen() || GrandExchange.isOpen()) Widgets.closeAll();
            ClientSettings.toggleBuyPriceWarning(false);
            return ReactionGenerator.getNormal();
        }
        return tree.run();
    }

    @Override
    public void onGameObjectSpawn(GameObject object) {
        if (object.getName().contains("trap")) {
            if (Players.getLocal().getTile().equals(object.getTile())) {
                // this is to stop boxes failing to catch getting added
                if (object.getID() == ObjectID.BOX_TRAP_9380) {
                    Logger.log("----------------------------------------------");
                    Logger.log("Ani: " + Players.getLocal().getAnimation());
                    Logger.log("Dist: " + object.distance(Players.getLocal()));
                    Logger.log("----------------------------------------------");
                    if (Players.getLocal().getAnimation() == 5208) {
                        config.trapMapPut(object.getTile(), object);
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message.getMessage().toLowerCase().contains("you are dead")) {
            AntiPkNode.shouldHop = true;
        }

        if (message.getMessage().toLowerCase().contains("set up only") && !message.getMessage().toLowerCase().contains("congratulations")) {
            Logger.log("Box tracking failure, this is normally caused by lag, dismissing random events or being crashed by another player");
            config.setFailSafe(true);
        }
    }

    @Override
    public void onPaint(Graphics g) {
        PAINT.paint(g);
    }

    int chinsCaught = 0;

    @Override
    public void onInventoryItemAdded(Item item) {
        Logger.info(String.format("New Item %s", item.getName()));
        if (item.getID() == ItemID.BLACK_CHINCHOMPA) chinsCaught++;
    }

    @Override
    public void onInventoryItemChanged(Item incoming, Item existing) {
        Logger.info(String.format("Item change %s, %d, %d", existing.getName(), incoming.getAmount(), existing.getAmount()));
        int quantityAdded = incoming.getAmount() - existing.getAmount();
        if (quantityAdded >= 1 && incoming.getID() == ItemID.BLACK_CHINCHOMPA) {
            chinsCaught += quantityAdded;
        }
    }

    DecimalFormat df = new DecimalFormat("###,###,###");

    @Override
    public String[] getPaintInfo() {
        int chinPrice = LivePrices.get(ItemID.BLACK_CHINCHOMPA);
        long earntGp = (long) chinsCaught * chinPrice;
        long xpGained = chinsCaught * 315L;
        return new String[]{
                String.format("Banking @ %d blacks caught", ScriptSettings.getChinMax()),
                String.format("cCBlackChins - %s ", timer.formatTime()),
                "Status: " + config.getSubStatus(),
                Arrays.toString(FractalAPI.hierarchy),
                "Hunter lvl: " + Skills.getRealLevel(Skill.HUNTER),
                String.format("Traps: %d/%d", config.getTrapMap().size(), BehaviourUtils.getTrapLimit()),
                String.format("Chins/hr %d(%d)", timer.getHourlyRate(chinsCaught), chinsCaught),
                String.format("Gp/hr %s(%s)", df.format(timer.getHourlyRate((int) earntGp)), df.format(earntGp)),
                String.format("Xp/hr %s(%s)", df.format(timer.getHourlyRate((int) xpGained)), df.format(xpGained)),
        };
    }
}
