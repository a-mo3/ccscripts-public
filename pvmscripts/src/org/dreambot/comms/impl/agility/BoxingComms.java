package org.dreambot.comms.impl.agility;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.comms.AbstractCommServer;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.agility.msg.BoxingMessageTypes;
import org.dreambot.comms.impl.agility.msg.BoxingPkMessage;
import org.dreambot.comms.impl.agility.msg.BoxingTeam;
import org.dreambot.comms.impl.agility.msg.MatchMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Match wilderness agility accounts with each other so they know who to box
 */
public class BoxingComms extends AbstractCommServer {
    Map<String, WebSocket> userMap = new ConcurrentHashMap<>();
    Map<String, BoxingTeam> teamMates = new ConcurrentHashMap<>();

    String unmatched = null;

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log("Box server open");
        String user = clientHandshake.getFieldValue("username");
        if (user == null || user.isEmpty()) {
            log("Username was empty on open");
            return;
        }

        userMap.put(user, webSocket);
        if (unmatched == null) {
            log("Set this user to unmatched");
            unmatched = user;
            return;
        } else {
            Gson gson = new Gson();
            log("Pair " + AnalyticsReporter.hashStringSHA256(unmatched) + " " + AnalyticsReporter.hashStringSHA256(user));
            if (user.equals(unmatched)) {
                log("Nvm they're the same");
                return;
            }

            int world = getRandomWorld();
            BoxingTeam team = new BoxingTeam(user, unmatched);
            teamMates.put(user, team);
            teamMates.put(unmatched, team);
            userMap.get(user).send(gson.toJson(new MatchMessage(unmatched).setWorld(world)));
            userMap.get(unmatched).send(gson.toJson(new MatchMessage(user).setWorld(world)));
            unmatched = null;
        }
    }

    @Override
    public void onMessage(WebSocket socket, String msg) {
        JsonObject obj = JsonParser.parseString(msg).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        BoxingMessageTypes type = null;

        try {
            type = BoxingMessageTypes.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + msg);
            return;
        }
        Gson g = new Gson();

        switch (type) {
            case PK_MESSAGE:
                BoxingPkMessage pkMessage = g.fromJson(msg, BoxingPkMessage.class);
                BoxingTeam t = teamMates.get(pkMessage.getUsername());
                // pick a new world and update the 2 client
                int world = getRandomWorld();
                log("Transmitting new world " + world);
                userMap.get(t.getMemberA()).send(g.toJson(new MatchMessage(t.getMemberB(), world)));
                userMap.get(t.getMemberB()).send(g.toJson(new MatchMessage(t.getMemberA(), world)));
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
    }

    private static BoxingComms instance;

    public static BoxingComms getInstance() {
        if (instance == null) {
            // no settings
            instance = new BoxingComms();
        }
        return instance;
    }

    private void log(String log) {
        EverythingCommServer.log("[Lemon] - " + log);
    }

    private int getRandomWorld() {
        return Worlds.getRandomWorld(x -> x.isNormal() && x.isMembers() && x.getMinimumLevel() < 50)
                .getWorld();
    }
}
