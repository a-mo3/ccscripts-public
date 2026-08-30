package org.dreambot.muling.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.muling.*;
import org.dreambot.muling.messages.*;
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

    @Override
    public void start() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Mule exception (probably cant connect) " + e));
        super.start();
    }

    @Override
    public void run() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Mule exception (probably cant connect) " + e));
        super.run();
    }

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
        int muleWorldId = -1;
        MuleTile muleTile = null;

        if (isMule) {
            if (handshake.getFieldValue("worldId").length() == 0
                    || handshake.getFieldValue("tileX").length() == 0
                    || handshake.getFieldValue("tileY").length() == 0
                    || handshake.getFieldValue("tileZ").length() == 0) {
                conn.close(10002, "Invalid or missing mule handshake data");
                return null;
            }

            Logger.info("Le Glozzen Debug " + handshake.getFieldValue("worldId"));
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
        Logger.info(client.getLoggingPrefix() + "Connected to server: " + client);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Client client = getClientFromConn(conn);
        if (client == null) {
            return;
        }
        removeClient(client, String.format("Disconnected from server: %d - %s", code, reason));
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Client client = getClientFromConn(conn);
        if (client == null) {
            return;
        }
        removeClient(client, String.format("Disconnected from server: %s", ex));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            Client client = getClientFromConn(conn);
            if (client == null) {
                return;
            }

            Logger.info("Client message: " + conn.getRemoteSocketAddress() + " - " + message);

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
                    Logger.info("MULE REQ: " + muleRequest.toJson());
                    requests.removeIf(request -> request.getMuleRequest().playerName.equals(muleRequest.playerName));

                    Client mule = findMuleForRequest(client.getGroups(), muleRequest);
                    if (mule == null) {
                        StringBuilder sb = new StringBuilder();
                        for (Client muleClient : clients.values()) {
                            if (!muleClient.isMule()) {
                                continue;
                            }

                            List<Request> muleRequests = requests.stream()
                                    .filter(r -> r.getMule() == muleClient)
                                    .collect(Collectors.toList());

                            OwnedItem coins = muleClient.getOwnedItems().stream().filter(x -> x.getItemId() == 995).findFirst().orElse(null);
                            if (coins == null) coins = new OwnedItem(995, 0);
                            sb.append(String.format("Mule %s is on world %d with membership %b client membership: %b coins %d\n",
                                    muleClient.getPlayerName(), muleClient.getWorldId(), muleClient.isMember(), org.dreambot.api.Client.isMembers(), coins.getQuantity()));
                        }


                        send(conn, new MuleResponseMessage(false, "Failed to find a mule to handle the request", 0, null, null,
                                sb.toString()
                        ));
                        return;
                    }

                    requests.add(new Request(client, mule, muleRequest));

                    send(conn, new MuleResponseMessage(true, null, mule.getWorldId(), mule.getTile(), mule.getPlayerName(), ""));
                    send(mule.getConn(), muleRequest);
                }
                break;

                case TRADE_REQUEST: {
                    TradeRequestMessage tradeRequest = new Gson().fromJson(jsonElement, TradeRequestMessage.class);
                    Logger.info(tradeRequest.toJson());

                    Request matchingRequest = requests.stream()
                            .filter(r -> r.getMuleRequest().requestId.equals(tradeRequest.requestId) && r.getMule() != null)
                            .findFirst()
                            .orElse(null);

                    if (matchingRequest == null) {
                        for (Map.Entry<Long, Client> e : clients.entrySet()) {
                            if (e.getValue().isMule()) {
                                OwnedItem gp = e.getValue().getOwnedItems().stream().filter(x -> x.getItemId() == 995).findFirst().orElse(null);
                                Logger.format("Mule %s has %d gp", e.getValue().getPlayerName(), gp);
                            }
                        }
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
//            if (muleClient.isMember() && !request.hasMembership) {
//                continue;
//            }

            if (!muleClient.isMule()) {
                continue;
            }

            boolean contFlag = false; // better than a break in my 3am opinion
            World world = Worlds.getWorld(muleClient.getWorldId());
            if (world == null)
                Logger.warn("WAS UNABLE TO CHECK MULES WORLD DEFAULTING TO MEMBERSHIP STATUS " + muleClient.getWorldId());
            boolean isMuleInMembersWorld = world == null ? muleClient.isMule() : world.isMembers();

            if (isMuleInMembersWorld && !request.hasMembership) {
                Logger.info("skipped " + muleClient + " incorrect membership status " + request);
                continue;
            }

            // check all items in a request for members items and only allow a members bot to handle that
            for (OfferedItem offeredItem : request.offeredItems) {
                if (new Item(offeredItem.getItemId(), 1).isMembersOnly()) {
                    Logger.info("offered item was members only");
                    contFlag = !isMuleInMembersWorld;
                }
            }
            for (RequiredItem requiredItem : request.requiredItems) {
                if (new Item(requiredItem.getItemId(), 1).isMembersOnly()) {
                    Logger.info("required item was members only");
                    contFlag = !isMuleInMembersWorld;
                }
            }
            Logger.info("Requests cont flag was: " + contFlag + " on mule " + muleClient.getPlayerName());
            if (contFlag) continue;

            if (!muleClient.isInGroup(groups)) {
                Logger.info("mule groups");
                continue;
            }

//            if (request.muleName != null && !muleClient.getPlayerName().equalsIgnoreCase(request.muleName)) {
//                Logger.info("same mule name");
//                continue;
//            }

            if (muleClient.getQueueSize() > 0) {
                List<Request> muleRequests = requests.stream()
                        .filter(r -> r.getMule() == muleClient)
                        .collect(Collectors.toList());
                if (muleRequests.size() >= muleClient.getQueueSize()) {
                    Logger.info("mule requests queue size");
                    continue;
                }
            }

            if (request.requiredItems.size() > 0 && !muleClient.hasRequiredItems(request.requiredItems, requests.stream()
                    .filter(r -> r.getMule() == muleClient)
                    .collect(Collectors.toList()))) {
                Logger.info("Mule does not have required items");
                continue;
            }

            validMules.add(muleClient);
        }
        if (validMules.size() == 0) {
            Logger.info("no valid mules");
            return null;
        }
        return validMules.get(Random.asInt(0, validMules.size() - 1));
    }
}
