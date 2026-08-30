package org.dreambot.comms.impl.agility;

import com.google.gson.Gson;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.agility.msg.MatchMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class BoxingClient extends WebSocketClient {

    public BoxingClient() {
        super(URI.create("ws://0.0.0.0:" + EverythingCommServer.PORT));

        log("Open comm instance");
        EverythingCommServer.getInstance();


        log("Handshake and such");
        addHeader("username", Players.getLocal().getName());
        addHeader("routeCode", "boxing");
        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    private static BoxingClient instance;
    public String teamMate = null;
    public int world = -1;

    public static int getWorld()  {
        if (instance == null) return -1;
        return instance.world;
    }

    public static String getTeammate()  {
        if (instance == null) return "";
        return instance.teamMate;
    }

    public static BoxingClient getInstance() {
        if (instance == null) {
            // no settings
            instance = new BoxingClient();
        }
        return instance;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String s) {
        Gson gson = new Gson();
        MatchMessage matchMessage = gson.fromJson(s, MatchMessage.class);
        log("Set team mate");
        teamMate = matchMessage.getTeamMate();
        world = matchMessage.getWorld();
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
    }

    private void log(String log) {
        EverythingCommServer.log("[Lemon Client] - " + log);
    }
}
