package org.dreambot.comms.impl.vetion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.vetion.messages.VetionMessageType;
import org.dreambot.comms.impl.vetion.messages.VetionPkReportMessage;
import org.dreambot.comms.impl.vetion.messages.VetionTeamState;
import org.dreambot.discordwebhook.pojo.DiscordEmbed;
import org.dreambot.discordwebhook.pojo.DiscordWebHook;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * maybe this should be an everything comms clients? havent thought about it much
 */
public class VetionClient extends WebSocketClient {
    @Getter
    VetionTeamState vetionTeamState;

    private static VetionClient instance;

    public static VetionClient getInstance() {
        if (instance == null) instance = new VetionClient();
        return instance;
    }


    public static VetionTeamState getState() {
        if (instance == null) {
            return null;
        }
        return instance.vetionTeamState;
    }

    public static List<String> getPkers() {
        return getInstance().getVetionTeamState().getOpps();
    }

    public static Player getFirstPker() {
        if (instance == null) return null;
        if (instance.vetionTeamState == null) return null;
        return getInstance().getVetionTeamState().getOpps()
                .stream()
                .map(Players::closest)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    // we only want to webhook when we're attacked by someone new
    static Map<String, Timer> pkers = new HashMap<>();

    public static void reportPker(String pkerName, String webhook) {
        pkers.entrySet().removeIf(x -> x.getValue().finished());

        if (!pkers.containsKey(pkerName)) {
            pkers.put(pkerName, new Timer(1000 * 60 * 5));
            if (webhook != null && !webhook.isEmpty()) {
                Logger.info("Sending webhook");
                try {
                    new DiscordWebHook()
                            .setEmbeds(
                                    new DiscordEmbed()
                                            .setTitle("HELP! W" + Worlds.getCurrentWorld())
                                            .setDescription("HELP! W" + Worlds.getCurrentWorld() + " Pker " + pkerName)
                            )
                            .send(webhook, Client.getCanvasImage());
                } catch (IOException e) {
                    Logger.info("Failed to send webhook " + e);
                }
            }

        }


        VetionTeamState state = getInstance().getVetionTeamState();
        if (!state.getOpps().contains(pkerName)) {
            VetionPkReportMessage report = new VetionPkReportMessage();
            report.setTeamId(state.getTeamId());
            report.setOpp(pkerName);
            Logger.info("Reporting PKer " + pkerName);
            getInstance().send(new Gson().toJson(report));
        }
    }

    private VetionClient() {
        super(URI.create("ws://0.0.0.0:" + EverythingCommServer.PORT));

        // todo init an everything comms server?
        log("Open comm instance");
        EverythingCommServer.getInstance();


        log("Handshake and such");
        addHeader("username", Players.getLocal().getName());
        addHeader("routeCode", "vetion");
        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log("Vetion client open");
    }

    @Override
    public void onMessage(String s) {
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        VetionMessageType type = null;

        try {
            type = VetionMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + s);
            return;
        }

        Gson gson = new Gson();
        switch (type) {
            case TEAM_STATE:
                vetionTeamState = gson.fromJson(s, VetionTeamState.class);
                break;
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log("Vetion client close");
    }

    @Override
    public void onError(Exception e) {
        log("Vetion client error");
    }

    private void log(String msg) {
        Logger.log(Color.PINK, "[Vetion comms client] " + msg);
    }

    public static int getWorld() {
        if (getInstance().getVetionTeamState() == null) return -1;
        return getInstance().getVetionTeamState().getWorld();
    }

    public static void closeConnection() {
        getInstance().close();
    }
}
