package org.dreambot.muling.impl;

import com.google.gson.Gson;
import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankMode;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.trade.Trade;
import org.dreambot.api.methods.trade.TradeUser;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.events.AbstractEvent;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.LoadoutExecutor;
import org.dreambot.fractals.util.OwnedItems;
import org.dreambot.muling.Log;
import org.dreambot.muling.OfferedItem;
import org.dreambot.muling.RequiredItem;
import org.dreambot.muling.messages.client.MuleRequestMessage;
import org.dreambot.muling.messages.server.MuleResponseMessage;
import org.dreambot.settings.timing.ReactionGenerator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class MuleRequestEvent extends AbstractEvent implements ChatListener {
    List<RequiredItem> requiredItems = new ArrayList<>();
    List<OfferedItem> offeredItems = new ArrayList<>();
    MuleConnection muleConnection;
    MuleResponseMessage muleResponse;
    TimeoutMuleRequest timeoutMuleRequest;
    String status = "";

    public MuleRequestEvent() {
        Client.getInstance().addEventListener(this);
    }

    @Override
    public int onLoop() {
        log("on loop");
        if (muleConnection == null) {
            log("Getting a mule connection");
            status = "getting connection";
            muleConnection = getMuleConnection();
            return ReactionGenerator.getNormal();
        }

        if (muleResponse == null || timeoutMuleRequest == null) {
            log("Sending mule request");
            status = "Sending mule request";
            muleResponse = sendMuleRequest();
            return ReactionGenerator.getNormal();
        }

        if (timeoutMuleRequest.isExpired()) {
            setComplete(true);
            log("Finsihed trade request event");
            return ReactionGenerator.getNormal();
        }

        // handle getting out offered items & making space
        InventoryLoadout loadout = generateInventoryLoadout();
        loadout.setStrict(true);
        if (!loadout.isFulfilled() && !Trade.isOpen()) {
            log("Fulfilling loadout");
            LoadoutExecutor.execInvLoadout(loadout);
            return ReactionGenerator.getNormal();
        }

        if (!Trade.isOpen()) {
            for (OfferedItem item : offeredItems) {
                if (Inventory.count(item.getItemId()) < item.getQuantity()) {
                    if (!Bank.isOpen()) {
                        Bank.open();
                    }

                    Bank.setWithdrawMode(BankMode.NOTE);

                    Bank.withdrawAll(item.getItemId());
                    return ReactionGenerator.getNormal();
                }
            }
        }

        if (Bank.isOpen()) {
            Bank.close();
            return ReactionGenerator.getNormal();
        }
        // handle actual trade
        if (Trade.isOpen(1)) {
            log("trade screen 1");
            if (!timeoutMuleRequest.requestSideHasFulfilled()) {
                OfferedItem offeredItem = timeoutMuleRequest.getNextOfferedItem();
                log("%d * %d", offeredItem.getItemId(), offeredItem.getQuantity());
                Trade.addItem(offeredItem.getItemId(), offeredItem.getQuantity());
                Sleep.sleepUntil(() -> Trade.contains(true, offeredItem.getItemId()), 2400);
                return ReactionGenerator.getNormal();
            }
            if (!Trade.hasAcceptedTrade(TradeUser.US)) {
                Trade.acceptTrade();
                Sleep.sleepUntil(() -> Trade.hasAcceptedTrade(TradeUser.US), 1000);
            }
            return ReactionGenerator.getNormal();
        }

        if (Trade.isOpen(2)) {
            Log.info("accpeting trade screen 2");
            if (!Trade.hasAcceptedTrade(TradeUser.US)) {
                Trade.acceptTrade();
                Sleep.sleepUntil(() -> Trade.hasAcceptedTrade(TradeUser.US), 1000);
            }
            return ReactionGenerator.getNormal();
        }



        // check valid res
        if (muleResponse.world == 0) {
            log("No mule responded to request, either all busy or all broke, trying again soon.");
            Sleep.sleep(3000);
            setFailed(true);
            return ReactionGenerator.getNormal();
        }

        // hop worlds
        if (muleResponse.world != Worlds.getCurrentWorld()) {
            log("Hopping to mule world, %d -> %d", Worlds.getCurrentWorld(), muleResponse.world);
            WorldHopper.hopWorld(muleResponse.world);
            return ReactionGenerator.getNormal();
        }

        // trade mule
        Player mulePlayer = Players.closest(muleResponse.muleName);
        if (mulePlayer != null) {
            mulePlayer.interact("Trade with");
            Sleep.sleepUntil(Trade::isOpen, 4400);
            return ReactionGenerator.getNormal();
        }


        // walk to mules tile
        log("Walking to mule loc");
        Tile muleLoc = new Tile(muleResponse.location.x, muleResponse.location.y);
        Walking.walk(muleLoc);

        return ReactionGenerator.getNormal();
    }

    @Override
    public void onExit() {
        Client.getInstance().removeEventListener(this);
        if (muleConnection != null) {
            Log.info("Closing mule connection");
            muleConnection.sendComplete();
            muleConnection.close();
        }
    }

    public MuleRequestEvent addRequiredItem(int itemID, int quantity) {
        requiredItems.add(new RequiredItem(itemID, quantity));
        return this;
    }

    public MuleRequestEvent addOfferedItem(int itemID, int quantity) {
        offeredItems.add(new OfferedItem(itemID, quantity));
        return this;
    }

    private MuleConnection getMuleConnection() {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("clientUsername", "cCUser");
            put("playerName", Players.getLocal().getName());
            put("isMule", "false");
            put("isMember", String.valueOf(Client.isMembers()));
        }};

        if (muleConnection == null) {
            Log.info("Getting mule connection");
            try {
                muleConnection = new MuleConnection(new URI("ws://" + MuleState.MULE_SERVER_ADDRESS), headers);
            } catch (URISyntaxException | InterruptedException e) {
                Log.info(e);
                throw new RuntimeException(e);
            }
        }

        return muleConnection;
    }

    private MuleResponseMessage sendMuleRequest() {
        Log.info("Sending mule request");
        if (muleConnection == null) {
            Log.info("mule connection null");
            muleConnection = getMuleConnection();
            return null;
        }


        if (muleConnection.getMuleResponse() != null) {
            Log.info("Mule connection response was null");
            return muleConnection.getMuleResponse();
        }
        MuleRequestMessage muleRequestMessage = new MuleRequestMessage(
                muleConnection.getUuid().toString(),
                System.currentTimeMillis(),
                Players.getLocal().getName(),
                Client.isMembers(),
                requiredItems,
                offeredItems,
                null
        );
        String jsonMuleRequest = new Gson().toJson(muleRequestMessage);

        muleConnection.send(jsonMuleRequest);
        timeoutMuleRequest = new TimeoutMuleRequest(muleRequestMessage);
        while (muleConnection.getMuleResponse() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return muleConnection.getMuleResponse();
    }

    private void log(String message, Object... formats) {
        Logger.info(String.format("[MULE EVENT] - " + message, formats));
    }

    public InventoryLoadout generateInventoryLoadout() {
        InventoryLoadout loadout = new InventoryLoadout();
        for (OfferedItem req : offeredItems) {
            loadout.addItem(req.getItemId(), req.getQuantity(), OwnedItems.count(req.getItemId()));
        }
        return loadout;
    }

    @Override
    public void onGameMessage(Message message) {
        String str = message.getMessage();
        if (message.getType() == MessageType.TRADE_COMPLETE && str.contains("Accepted")) {
            Logger.info("accept trade");
            timeoutMuleRequest.finish();
        }
    }
}
