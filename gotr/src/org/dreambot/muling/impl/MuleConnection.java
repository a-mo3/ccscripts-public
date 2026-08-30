package org.dreambot.muling.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.muling.messages.MessageType;
import org.dreambot.muling.messages.OwnedItem;
import org.dreambot.muling.messages.client.MuleRequestMessage;
import org.dreambot.muling.messages.client.OwnedItemsUpdateMessage;
import org.dreambot.muling.messages.client.TradeCompletedMessage;
import org.dreambot.muling.messages.server.MuleResponseMessage;
import org.dreambot.muling.messages.server.TradeResponseMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class MuleConnection extends WebSocketClient {
    private boolean hasOpened = false;
    private MuleResponseMessage muleResponse = null;
    private UUID uuid = UUID.randomUUID();
    @Setter
    String muleName = null;

    public MuleConnection(URI serverUri, boolean isMule) throws InterruptedException {
        super(serverUri);
        // todo set headers
        addHeader("isMule", String.valueOf(isMule));
        addHeader("clientUsername", "camalUser");
        addHeader("isMember", String.valueOf(Client.isMembers()));
        addHeader("playerName", Players.getLocal().getName());
        Tile muleTile = Players.getLocal().getTile();
        addHeader("worldId", String.valueOf(Worlds.getCurrentWorld()));
        addHeader("tileX", String.valueOf(muleTile.getX()));
        addHeader("tileY", String.valueOf(muleTile.getY()));
        addHeader("tileZ", String.valueOf(muleTile.getZ()));
        connectBlocking();
        Logger.info("Connected");
    }

    public MuleConnection(URI serverUri, Map<String, String> headers) throws InterruptedException {
        super(serverUri);
        System.out.println("connected!");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            this.addHeader(header.getKey(), header.getValue());
        }
        connectBlocking();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Logger.info("onOpen");
        hasOpened = true;
    }

    @Override
    public void onMessage(String message) {
        JsonElement jsonElement = new JsonParser().parse(message);
        if (jsonElement == null) {
            return;
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has("type")) {
            return;
        }

        Logger.info("Message: " + jsonElement.getAsJsonObject());

        MessageType messageType = MessageType.valueOf(jsonObject.get("type").getAsString());
        if (messageType == MessageType.MULE_REQUEST) {
            MuleRequestMessage muleRequestMessage = new Gson().fromJson(jsonElement, MuleRequestMessage.class);
            MuleState.queuedRequest.add(new TimeoutMuleRequest(muleRequestMessage));
            Logger.info("Got & added a mule request.");
        }

        if (messageType == MessageType.MULE_RESPONSE) {
            this.muleResponse = new Gson().fromJson(jsonElement, MuleResponseMessage.class);

        }

        if (messageType == MessageType.TRADE_RESPONSE) {
            Logger.info("trade response");
            TradeResponseMessage msg = new Gson().fromJson(jsonElement, TradeResponseMessage.class);
            // todo handle a fail
            setMuleName(msg.playerName);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Logger.info(String.format("MC closed: %d, %s", code, reason));
    }

    @Override
    public void onError(Exception ex) {
        MuleState.setMuleConnection(null);
        Logger.warn(ex);
        ex.printStackTrace();
    }

    public void updateOwnedItems(List<OwnedItem> ownedItems) {
        OwnedItemsUpdateMessage msg = new OwnedItemsUpdateMessage(ownedItems);
        send(new Gson().toJson(msg));
    }

    public void sendComplete(boolean success, String reason, String reqId) {
        TradeCompletedMessage msg = new TradeCompletedMessage(success, reason, reqId);
        send(new Gson().toJson(msg));
    }

    public void sendComplete() {
        if (muleResponse == null) {
            Logger.info("couldnt complete request, response was null");
            return;
        }

        send(new Gson().toJson(new TradeCompletedMessage(muleResponse.success, "complete", uuid.toString())));
    }
}
