package org.dreambot.behaviour.method.huey.comms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.method.huey.comms.messages.HueyLeaderRegroup;
import org.dreambot.behaviour.method.huey.comms.messages.HueyMsgType;
import org.dreambot.behaviour.method.huey.comms.messages.HueyUpdateMeMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.*;
import java.net.URI;

public class HueyCommsClient extends WebSocketClient {
    public static HueyTeam currentTeam;
    private static HueyCommsClient instance;
    public static boolean needsToRegroup;

    public static HueyCommsClient getInstance(int teamSize, Location worldPreference) {
        if (instance == null) instance = new HueyCommsClient(6099, teamSize, worldPreference);
        return instance;
    }

    public static HueyCommsClient getInstance(int teamSize) {
        if (instance == null) instance = new HueyCommsClient(6099, teamSize, Location.GERMANY);
        return instance;
    }

    public void getUpdate() {
        log("Sending update team request.");
        Gson gson = new Gson();
        send(gson.toJson(new HueyUpdateMeMessage()));
    }

    private HueyCommsClient(int port, int teamSize, Location worldPreference) {
        super(URI.create("ws://0.0.0.0:" + port));
        log("Init huey comms");

        // try and start a server, if ones already running it just fails safely like how the mule works
        try {
            new HueyComms(teamSize, worldPreference);
        } catch (Exception e) {
            log("Huey server not started - probably already exists.");
        }

        addHeader("username", Players.getLocal().getName());
        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log("Comms open");
    }

    @Override
    public void onMessage(String s) {
        // only message we should get is to set the HueyTeam object for the team this bot is apart of
//        log(s);
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            currentTeam = new Gson().fromJson(s, HueyTeam.class);
            log("Team updated " + currentTeam);
            return;
        }

        HueyMsgType type = null;

        try {
            type = HueyMsgType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
        }
        if (type == null) {
            log("Couldn't find type " + s);
            return;
        }

        switch (type) {
            case LEADER_REGROUP:
                log("I need to regroup");
                needsToRegroup = true;
                return;
        }

        // handle leader regroup
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log("Comms close, should probably reopen?");
    }

    @Override
    public void onError(Exception e) {
        log("Comms error " + e);
    }

    private void log(String msg) {
        Logger.log(Color.PINK, "[Huey comms client] " + msg);
    }

    public void orderRegroup() {
        log("Attempting to send a regroup command");
        send(new Gson().toJson(new HueyLeaderRegroup()));
    }

    @Override
    public void run() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Uncaught exception (server already running probably, can ignore) " + e));
        super.run();
    }

}
