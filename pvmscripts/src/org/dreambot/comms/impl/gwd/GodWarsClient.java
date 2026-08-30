package org.dreambot.comms.impl.gwd;

import com.google.gson.Gson;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.utilities.Logger;
import org.dreambot.antiban.Antiban;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.agility.msg.MatchMessage;
import org.dreambot.comms.impl.gwd.msg.GWDRequestTeam;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class GodWarsClient extends WebSocketClient {
    final GodWarsBosses boss;

    public GodWarsClient(GodWarsBosses boss) {
        super(URI.create("ws://0.0.0.0:" + EverythingCommServer.PORT));
        this.boss = boss;

        log("Open comm instance");
        EverythingCommServer.getInstance();


        log("Handshake and such");
        addHeader("routeCode", "gwd");
        addHeader("msg", new Gson().toJson(new GWDRequestTeam(Players.getLocal().getName(), boss)));

        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    private static GodWarsClient instance;
    public GodWarsTeam team;


    @Nullable
    public static GodWarsTeam getTeam(GodWarsBosses boss) {
        if (instance == null) {
            Logger.info("Kree init connection instance");
            instance = new GodWarsClient(boss);
            Sleep.sleep(1000);
        }

        if (instance.team == null) {
            Logger.info("Team null request team");
            Gson gson = new Gson();
            instance.send(gson.toJson(new GWDRequestTeam(Players.getLocal().getName(), GodWarsBosses.KREE)));
            Sleep.sleep(1000);
        }

        return instance.team;
    }

    public static GodWarsClient getInstance(GodWarsBosses boss) {
        if (instance == null) {
            // no settings
            instance = new GodWarsClient(boss);
        }
        return instance;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
    }

    @Override
    public void onMessage(String s) {
        Gson gson = new Gson();
        team = gson.fromJson(s, GodWarsTeam.class);
        log("Set team mate");
    }

    @Override
    public void onClose(int i, String s, boolean b) {
    }

    @Override
    public void onError(Exception e) {
    }

    private void log(String log) {
        EverythingCommServer.log("[GWD Client] - " + log);
    }
}
