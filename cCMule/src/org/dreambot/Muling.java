package org.dreambot;

import okhttp3.*;
import org.dreambot.alerts.Alerts;
import org.dreambot.api.Client;
import org.dreambot.api.methods.ForumUser;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.login.LoginUtility;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.randoms.BreakSolver;
import org.dreambot.api.randoms.RandomEvent;
import org.dreambot.api.randoms.RandomSolver;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.Menu;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.discordwebhook.WebhookListener;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.fractals.paint.FluffeesPaint;
import org.dreambot.fractals.paint.PaintInfo;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.RequiredItem;
import org.dreambot.muling.impl.MuleSettings;
import org.dreambot.muling.impl.MuleState;
import org.dreambot.muling.impl.TimeoutMuleRequest;
import org.dreambot.muling.messages.OwnedItem;
import org.dreambot.muling.server.Server;
import org.dreambot.settings.timing.ReactionGenerator;

import java.awt.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

@ScriptManifest(category = Category.MONEYMAKING, name = "cCMule", author = "", version = 0.0)
public class Muling extends AbstractScript implements ChatListener, PaintInfo {
    FluffeesPaint p = new FluffeesPaint(FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN, this);
    DecimalFormat df = new DecimalFormat("###,###,###");
    PaintButton analyticsToggle = new PaintButton();

    @Override
    public void onStart(String... params) {
        for (String p : params) {
            if (isNumeric(p)) {
                MuleSettings.setPort(Integer.parseInt(p));
            }
        }
        init();
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onStart() {
        init();
    }

    AtomicBoolean isBreaking = new AtomicBoolean(false);
    BooleanSupplier breakingSupplier = () -> isBreaking.get();

    @Override
    public boolean onSolverStart(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(true);
        return super.onSolverStart(solver);
    }

    @Override
    public void onSolverEnd(RandomSolver solver) {
        if (solver instanceof BreakSolver) isBreaking.set(false);
        super.onSolverEnd(solver);
    }

    private void init() {
        analyticsToggle.setLabel(MuleSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        analyticsToggle.setBorderColor(MuleSettings.isAnalyticsOn() ? Color.GREEN : Color.RED);
        analyticsToggle.setOnClick(m -> {
            analyticsToggle.setBorderColor(MuleSettings.toggleAnalytics() ? Color.GREEN : Color.RED);
            analyticsToggle.setLabel(MuleSettings.isAnalyticsOn() ? "Analytics On" : "Analytics Off");
        });

        Client.getInstance().getRandomManager().disableSolver(RandomEvent.LOGIN);

        new WebhookListener();
        try {
            Server server = new Server(MuleSettings.getPort());
            server.start();
        } catch (Exception e) {
            Logger.info("Failed to start server, probably already running, not the master, will make connection to mule server");
        }
        MuleState.setMule(true);
    }

    // the mule request you are current handling
    TimeoutMuleRequest currentRequest;
    boolean hasUpdated = false; // if you have updated owned items
    String status = "Waiting";
    int ownedCoins = 0;
    Tile tile = null;
    OkHttpClient httpClient = new OkHttpClient();

    @Override
    public int onLoop() {
        int coins = OwnedItems.count(ItemID.COINS_995);
        if (Client.isLoggedIn() && ownedCoins > 2 && coins > 1 && coins != ownedCoins) {
            List<OwnedItem> ownedItems = new ArrayList<>();
            for (Item item : Bank.all()) {
                if (item == null) continue;
                ownedItems.add(new OwnedItem(item.getID(), item.getAmount()));
            }

            for (Item item : Inventory.all()) {
                if (item == null) continue;
                ownedItems.add(new OwnedItem(item.getID(), item.getAmount()));
            }

            if (MuleState.updateOwnedItems(ownedItems)) {
                Logger.info("Sent owned items update coin change from " + ownedCoins + " " + coins);
            }

            int change = coins - ownedCoins;
            // send analytics update if opt'ed in
            // this identifier will be the name of the script that sent the request,
            // used to track what script is draining / supplying mules when you are running farms with more than 1
            String identifier = currentRequest == null ? null : currentRequest.getRequestMessage().muleName;
            ForumUser forumUser = Client.getForumUser();
            if (forumUser != null && MuleSettings.isAnalyticsOn() && identifier != null) {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, new AnalyticMessage(
                        identifier,
                        change,
                        forumUser.getUsername(),
                        hashStringSHA256(currentRequest.getRequestMessage().playerName)
                ).toString());
                Request request = new Request.Builder()
                        .url(".com/mule")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("User-Agent", "insomnia/10.0.0")
                        .addHeader("TOKEN", forumUser.getAuthenticationCode())
                        .build();

                try (Response response = httpClient.newCall(request).execute();) {
                } catch (IOException e) {
                    log("Exception " + e);
//                    throw new RuntimeException(e);
                }
            }
        }
        if (coins > 1) ownedCoins = coins;

        MuleState.clearOldReqs();
        if (currentRequest != null && currentRequest.isExpired()) currentRequest = null;

        if (!Client.isLoggedIn()) {
            if (currentRequest != null || MuleState.isMuleConnectionNull() || MuleState.queuedRequest.size() > 0) {
                status = "Logging in";
                LoginUtility.login();
                Sleep.sleepUntil(Client::isLoggedIn, 8_400);
            }
            return ReactionGenerator.getNormal();
        }

        if (currentRequest == null && Trade.isOpen()) {
            status = "Finding person im in trade with";
            currentRequest = MuleState.findRequestForMessage(Trade.getTradingWith());
            return ReactionGenerator.getNormal();
        }

        if (!hasUpdated) {
            status = "Updating owned items";
            if (Bank.getLastBankHistoryCacheTime() < 1) {
                if (Walking.shouldWalk()) Bank.open();
                if (Bank.isOpen()) Bank.close();
                return ReactionGenerator.getNormal();
            }

            if (Bank.isOpen()) {
                Bank.close();
                return ReactionGenerator.getNormal();
            }

            // make sure we dont get jebaited far away
            if (tile == null && !Widgets.isOpen()) {
                Logger.info("Setting tile");
                tile = Players.getLocal().getTile();
            }


            List<OwnedItem> ownedItems = new ArrayList<>();

            for (Item item : Bank.all()) {
                if (item == null) continue;
                ownedItems.add(new OwnedItem(item.getID(), item.getAmount()));
            }

            for (Item item : Inventory.all()) {
                if (item == null) continue;
                ownedItems.add(new OwnedItem(item.getID(), item.getAmount()));
            }

            if (MuleState.updateOwnedItems(ownedItems)) {
                Logger.info("Sent owned items update");
                hasUpdated = true;
            }
            return ReactionGenerator.getNormal();
        }

        if (MuleState.isMuleConnectionNull()) {
            status = "Getting mule connection";
            MuleState.getMuleConnection();
            return ReactionGenerator.getNormal();
        }

        if (currentRequest == null) {
            status = "Waiting";
            if (tile != null && Client.isLoggedIn() && !Widgets.isOpen() && tile.distance() > 10) {
                Logger.info("Forcing onto tile " + tile);
                if (Walking.shouldWalk()) Walking.walk(tile);
                return ReactionGenerator.getQuick();
            }
            if (Bank.isOpen()) Bank.close();
            return ReactionGenerator.getNormal() + 1200;
        }

        // get the loadout required to fulfill the mule request
        InventoryLoadout loadout = currentRequest.generateInventoryLoadout();
        if (!loadout.isFulfilled() && !Trade.isOpen()) {
            status = "Getting loadout";
            LoadoutExecutor.execInvLoadout(loadout);
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen()) {
            status = "Trading with client";
            // open trade with the current request
            Player custy = Players.closest(currentRequest.getRequestMessage().playerName);
            // mouse can cause errors on a crowded world
            if (!Menu.isMenuManipulationActive()) {
                Alerts.addAlert(6_000, Color.YELLOW, "Menu Manipulation on is recommend, dreambot VIP is required.");
            }
            Logger.info("Menu manip status " + Menu.isMenuManipulationActive());

            if (custy != null) {
                custy.interact("Trade with");
                Sleep.sleepUntil(Trade::isOpen, 4400);
            }

            if (!Trade.isOpen() && !Menu.isMenuManipulationActive()) {
                Logger.info("Trying to find chat widget because menu manip was disabled");
                WidgetChild chatTrade = Widgets.get(x -> x.getText().contains(currentRequest.getRequestMessage().playerName)
                        && x.getText().contains("trade with you.")
                        // check its within the chatbox because chat widgets go up the screen
                        && x.getY() > Client.getViewportHeight() - 145
                );
                if (chatTrade != null) chatTrade.interact();
            }

            return ReactionGenerator.getNormal();
        }

        if (Trade.isOpen(1)) {
            if (!currentRequest.hasFulfilled()) {
                // add items to trade
                status = "Trade screen one adding items to trade";
                RequiredItem requiredItem = currentRequest.getNextRequiredItem();
                Trade.addItem(requiredItem.getItemId(), requiredItem.getQuantity());
                Sleep.sleepUntil(() -> Trade.contains(true, requiredItem.getItemId()), 2400);
                return ReactionGenerator.getNormal();
            }
            status = "Trade screen one accept";
            Trade.acceptTrade();
            Sleep.sleepUntil(() -> Trade.isOpen(2), 4400);
            return ReactionGenerator.getNormal();
        }

        if (Trade.isOpen(2)) {
            // probably dont have to validate anything here tbh
            status = "Trade screen two accepting";
            Trade.acceptTrade();
            Sleep.sleepUntil(() -> !Trade.isOpen(), 4400);
        }
        return ReactionGenerator.getNormal();
    }

    @Override
    public void onExit() {
        MuleState.shutDown();
    }


    @Override
    public void onPaint(Graphics graphics) {
        Alerts.renderList(graphics);
        p.paint(graphics);
        analyticsToggle.paintButton(graphics);
    }

    @Override
    public void onTradeMessage(Message message) {
        String str = message.getMessage();
        Logger.info(message.getType() + " on trade " + str);

        TimeoutMuleRequest req = MuleState.findRequestForMessage(str);
        if (!Trade.isOpen() && req != null) {
            currentRequest = req;
        }
    }

    @Override
    public void onMessage(Message message) {

        String str = message.getMessage();
//        Logger.info(message.getType() + " on msg " + str);
        if (message.getType() == MessageType.TRADE_COMPLETE && str.contains("Accepted")) {
            Logger.info("MSG accept trade");
            currentRequest.finish();
        }
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                String.format("Queued Requests: %d", MuleState.queuedRequest.size()),
                String.format("Current Request: %s", currentRequest == null ? "None" : currentRequest.getRequestMessage().playerName),
                currentRequest == null ? "Null" : currentRequest.getTimeToLive(),
                "Tile " + tile,
                status,
                "Owned GP " + df.format(ownedCoins)
        };
    }

    public static String hashStringSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Error hashing: ", e);
            return null;
        }
    }
}
