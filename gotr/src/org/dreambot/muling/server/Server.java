package org.dreambot.muling.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.utilities.Logger;
import org.dreambot.muling.Client;
import org.dreambot.muling.Random;
import org.dreambot.muling.Request;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;
import org.dreambot.muling.messages.Mule;
import org.dreambot.muling.messages.MuleTile;
import org.dreambot.muling.messages.client.*;
import org.dreambot.muling.messages.server.ListMulesResponseMessage;
import org.dreambot.muling.messages.server.MuleResponseMessage;
import org.dreambot.muling.messages.server.TradeResponseMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Server extends WebSocketServer {
    private long connIndex = 0L;
    private final Map<Long, Client> clients = new ConcurrentHashMap<>();
    private final List<Request> requests = new CopyOnWriteArrayList<>();
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public Server(int port) {
        super(new InetSocketAddress(port), AVAILABLE_PROCESSORS, null);
    }

    private Client getClientFromConn(WebSocket conn, ClientHandshake handshake) {
        if (handshake.getFieldValue("clientUsername").length() == 0
                || handshake.getFieldValue("playerName").length() == 0
                || handshake.getFieldValue("isMule").length() == 0
                || handshake.getFieldValue("isMember").length() == 0) {
            conn.close(10001, "Invalid or missing handshake data");
            return null;
        }

        String clientUsername = handshake.getFieldValue("clientUsername");
        String[] groups = handshake.getFieldValue("groups").length() > 0 ? handshake.getFieldValue("groups").split(",") : new String[]{"default"};
        int queueSize = handshake.getFieldValue("queueSize").length() > 0 ? Integer.parseInt(handshake.getFieldValue("queueSize")) : 0;
        String playerName = handshake.getFieldValue("playerName");
        boolean isMule = handshake.getFieldValue("isMule").equals("true");
        boolean isMember = handshake.getFieldValue("isMember").equals("true");
        int muleWorldId = 0;
        MuleTile muleTile = null;

        if (isMule) {
            if (handshake.getFieldValue("worldId").length() == 0
                    || handshake.getFieldValue("tileX").length() == 0
                    || handshake.getFieldValue("tileY").length() == 0
                    || handshake.getFieldValue("tileZ").length() == 0) {
                conn.close(10002, "Invalid or missing mule handshake data");
                return null;
            }
            muleWorldId = Integer.parseInt(handshake.getFieldValue("worldId"));
            muleTile = new MuleTile(
                    Integer.parseInt(handshake.getFieldValue("tileX")),
                    Integer.parseInt(handshake.getFieldValue("tileY")),
                    Integer.parseInt(handshake.getFieldValue("tileZ"))
            );
        }

        conn.setAttachment(connIndex);

        Client client = new Client(conn, connIndex, System.currentTimeMillis(), clientUsername, groups, queueSize, playerName, isMule, isMember);

        client.setWorldId(muleWorldId);
        client.setTile(muleTile);

        clients.put(connIndex, client);

        connIndex++;

        return client;
    }

    private Client getClientFromConn(WebSocket conn) {
        if (conn == null || conn.getAttachment() == null) {
            return null;
        }
        long connIndex = conn.<Long>getAttachment();
        return clients.getOrDefault(connIndex, null);
    }

    private void removeClient(Client client, String reason) {
        Logger.error(client.getLoggingPrefix() + String.format("Removing client: %s - %s", client, reason));

        for (Request request : requests) {
            if (request.getClient() != client || request.isCompleted()) {
                continue;
            }
            TradeCompletedMessage message = new TradeCompletedMessage(false, reason, request.getMuleRequest().requestId);
            send(client.getConn(), message);
            send(request.getMule().getConn(), message);
        }

        clients.remove(client.getConnIndex());
    }

    @Override
    public void onStart() {
        Logger.info("LostMuleServer started on port: " + getPort());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Client client = getClientFromConn(conn, handshake);
        if (client == null) {
            return;
        }
        Logger.info(client.getLoggingPrefix() + "Connected to org.dreambot.muling.server: " + client);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Client client = getClientFromConn(conn);
        if (client == null) {
            return;
        }
        removeClient(client, String.format("Disconnected from org.dreambot.muling.server: %d - %s", code, reason));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Client client = getClientFromConn(conn);
        if (client == null) {
            return;
        }
        removeClient(client, String.format("Disconnected from org.dreambot.muling.server: %s", ex));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            Client client = getClientFromConn(conn);
            if (client == null) {
                return;
            }

            Logger.info("org.dreambot.muling.Client message: " + conn.getRemoteSocketAddress() + " - " + message);

            JsonElement jsonElement = new JsonParser().parse(message);
            if (jsonElement == null) {
                return;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("type")) {
                return;
            }

            MessageType messageType = MessageType.valueOf(jsonObject.get("type").getAsString());

            switch (messageType) {
                case MULE_REQUEST: {
                    MuleRequestMessage muleRequest = new Gson().fromJson(jsonElement, MuleRequestMessage.class);
                    requests.removeIf(request -> request.getMuleRequest().playerName.equals(muleRequest.playerName));

                    Client mule = findMuleForRequest(client.getGroups(), muleRequest);
                    if (mule == null) {
                        send(conn, new MuleResponseMessage(false, "Failed to find a mule to handle the request", 0, null, null));
                        return;
                    }

                    requests.add(new Request(client, mule, muleRequest));

                    send(conn, new MuleResponseMessage(true, null, mule.getWorldId(), mule.getTile(), mule.getPlayerName()));
                    send(mule.getConn(), muleRequest);
                }
                break;

                case TRADE_REQUEST: {
                    TradeRequestMessage tradeRequest = new Gson().fromJson(jsonElement, TradeRequestMessage.class);

                    Request matchingRequest = requests.stream()
                            .filter(r -> r.getMuleRequest().requestId.equals(tradeRequest.requestId) && r.getMule() != null)
                            .findFirst()
                            .orElse(null);

                    if (matchingRequest == null) {
                        send(conn, new TradeResponseMessage(false, "Failed to find matching request with id", tradeRequest.requestId, null));
                        return;
                    }

                    send(conn, new TradeResponseMessage(true, null, tradeRequest.requestId, matchingRequest.getMule().getPlayerName()));
                }
                break;

                // sent from both client and mule for when a trade is completed, successful or not
                case TRADE_COMPLETED: {
                    TradeCompletedMessage tradeCompleted = new Gson().fromJson(jsonElement, TradeCompletedMessage.class);

                    List<Request> matchingRequests = requests
                            .stream()
                            .filter(r -> r.getMuleRequest().requestId.equals(tradeCompleted.requestId))
                            .collect(Collectors.toList());

                    for (Request matchingRequest : matchingRequests) {
                        // if the sender is a mule, forward the same message onto the bots
                        if (client.isMule()) {
                            send(matchingRequest.getClient().getConn(), tradeCompleted);
                        }

                        requests.remove(matchingRequest);
                    }
                }
                break;

                // sent from mule when traded from an unknown player, message contains trading player name
                case UNKNOWN_TRADER: {
                    UnknownTraderMessage unknownTrader = new Gson().fromJson(jsonElement, UnknownTraderMessage.class);

                    List<Request> matchingRequests = requests
                            .stream()
                            .filter(r -> r.getMuleRequest().playerName.equals(unknownTrader.playerName))
                            .collect(Collectors.toList());

                    for (Request matchingRequest : matchingRequests) {
                        send(matchingRequest.getClient().getConn(), unknownTrader);

                        requests.remove(matchingRequest);
                    }
                }
                break;

                // sent from mule whenever inventory changes happen or trades are completed
                case OWNED_ITEMS_UPDATE: {
                    OwnedItemsUpdateMessage ownedItemsUpdate = new Gson().fromJson(jsonElement, OwnedItemsUpdateMessage.class);

                    client.getOwnedItems().clear();
                    client.getOwnedItems().addAll(ownedItemsUpdate.ownedItems);
                }
                break;

                // sent from any client to fetch a list of mules connected to org.dreambot.muling.server & all their info
                case LIST_MULES_REQUEST: {
//					ListMulesRequestMessage listMulesRequest = new Gson().fromJson(jsonElement, ListMulesRequestMessage.class);

                    List<Mule> mules = new ArrayList<>();

                    for (Client muleClient : clients.values()) {
                        if (!muleClient.isMule()) {
                            continue;
                        }

                        List<Request> muleRequests = requests.stream()
                                .filter(r -> r.getMule() == muleClient)
                                .collect(Collectors.toList());

                        mules.add(new Mule(
                                muleClient.getPlayerName(),
                                muleClient.getGroups(),
                                muleClient.getWorldId(),
                                muleClient.getTile(),
                                muleClient.isMember(),
                                muleClient.getOwnedItems(),
                                muleClient.getRemainingItems(muleRequests),
                                muleClient.getQueueSize(),
                                muleRequests.size()
                        ));
                    }

                    send(conn, new ListMulesResponseMessage(true, null, mules));
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void send(WebSocket conn, AbstractMessage message) {
        try {
            if (conn.isClosing() || conn.isClosed()) {
                return;
            }
            String data = message.toJson().toString();
            Logger.info("Sending message to conn: " + conn.getRemoteSocketAddress() + " - " + data);
            conn.send(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Client findMuleForRequest(String[] groups, MuleRequestMessage request) {
        List<Client> validMules = new ArrayList<>();
        for (Client muleClient : clients.values()) {
            if (!muleClient.isMule()) {
                continue;
            }

            if (!muleClient.isInGroup(groups)) {
                continue;
            }

            if (request.muleName != null && !muleClient.getPlayerName().equalsIgnoreCase(request.muleName)) {
                continue;
            }

            if (muleClient.getQueueSize() > 0) {
                List<Request> muleRequests = requests.stream()
                        .filter(r -> r.getMule() == muleClient)
                        .collect(Collectors.toList());
                if (muleRequests.size() >= muleClient.getQueueSize()) {
                    continue;
                }
            }

            if (request.requiredItems.size() > 0 && !muleClient.hasRequiredItems(request.requiredItems, requests.stream()
                    .filter(r -> r.getMule() == muleClient)
                    .collect(Collectors.toList()))) {
                continue;
            }

            validMules.add(muleClient);
        }
        if (validMules.size() == 0) {
            return null;
        }
        return validMules.get(Random.asInt(0, validMules.size() - 1));
    }
}
