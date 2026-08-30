package org.dreambot.comms.impl.venenatis;

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
import org.dreambot.comms.impl.venenatis.messages.VenenatisMessageType;
import org.dreambot.comms.impl.venenatis.messages.VenenatisPkReportMessage;
import org.dreambot.comms.impl.venenatis.messages.VenenatisRequestTeam;
import org.dreambot.comms.impl.venenatis.messages.VenenatisTeamState;
import org.dreambot.discordwebhook.pojo.DiscordEmbed;
import org.dreambot.discordwebhook.pojo.DiscordEmbedField;
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
public class VenenatisClient extends WebSocketClient {
    @Getter
    VenenatisTeamState venenatisTeamState;

    private static VenenatisClient instance;

    public static VenenatisClient getInstance() {
        if (instance == null) instance = new VenenatisClient();
        return instance;
    }

    public static VenenatisClient getInstance(int forceWorld) {
        if (instance == null) instance = new VenenatisClient();
        return instance;
    }

    public void requestTeam() {
        log("Requesting team refresh");
        send(new Gson().toJson(new VenenatisRequestTeam()));
    }

    public static VenenatisTeamState getState() {
        if (instance == null) return null;
        return instance.venenatisTeamState;
    }

    public static List<String> getPkers() {
        return getInstance().getVenenatisTeamState().getOpps();
    }

    public static Player getFirstPker() {
        if (instance == null) return null;
        VenenatisTeamState state = getInstance().getVenenatisTeamState();
        if (state == null) return null;
        return state.getOpps()
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
                                            .setFields(
                                                    new DiscordEmbedField("Pker ", pkerName, true)
                                            )
                            )
                            .send(webhook, Client.getCanvasImage());
                } catch (IOException e) {
                    Logger.info("Failed to send webhook " + e);
                }
            }
        }


        VenenatisTeamState state = getInstance().getVenenatisTeamState();
        if (!state.getOpps().contains(pkerName)) {
            VenenatisPkReportMessage report = new VenenatisPkReportMessage();
            report.setTeamId(state.getTeamId());
            report.setOpp(pkerName);
            report.setTeamMember(Players.getLocal().getName());
            Logger.info("Reporting PKer " + pkerName + " Team " + state.getTeamId() + " " + getInstance().isOpen());
            getInstance().send(new Gson().toJson(report));
        }
    }

    private VenenatisClient() {
        super(URI.create("ws://0.0.0.0:" + EverythingCommServer.PORT));

        // todo init an everything comms server?
        log("Open comm instance");
        EverythingCommServer.getInstance();


        log("Handshake and such");
        addHeader("username", Players.getLocal().getName());
        addHeader("routeCode", "venenatis");
        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log("venenatis client open");
    }

    @Override
    public void onMessage(String s) {
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        VenenatisMessageType type = null;

        try {
            type = VenenatisMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + s);
            return;
        }

        Gson gson = new Gson();
        switch (type) {
            case TEAM_STATE:
                VenenatisTeamState state = gson.fromJson(s, VenenatisTeamState.class);
                log("Update team state " +state );
                venenatisTeamState = state;
                break;
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log("venenatis client close");
        venenatisTeamState = null;
    }

    @Override
    public void onError(Exception e) {
        log("venenatis client error");
        venenatisTeamState = null;
    }

    private void log(String msg) {
        Logger.log(Color.PINK, "[Venenatis comms client] " + msg);
    }

    public static int getWorld() {
        if (getInstance().getVenenatisTeamState() == null) return -1;
        return getInstance().getVenenatisTeamState().getWorld();
    }

    public static void closeConnection() {
        getInstance().close();
    }
}
