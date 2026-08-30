package org.dreambot.behaviour.method.corp;

import com.google.gson.Gson;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.method.corp.messages.*;
import org.dreambot.settings.WrappedLocation;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * the bots instance to communicate with the server
 */
public class CorpClient extends WebSocketClient {
    private static CorpClient instance;
    private CorpTeam team;

    private CorpClient(int teamSize, int specialsPerTeam, WrappedLocation location, boolean forceHost) {
        super(URI.create("ws://0.0.0.0:" + port));
        log("Init corp comms");

        // try and start a server, if ones already running it just fails safely like how the mule works
        try {
            log("Start corp server");
            new CorpCommServer(teamSize, specialsPerTeam, location);
        } catch (Exception e) {
            log("Corp server not started - probably already exists. " + e);
        }

        addHeader("username", Players.getLocal().getName());
        addHeader("forceHost", String.valueOf(forceHost));
        addHeader(Skill.STRENGTH.getName(), String.valueOf(Skill.STRENGTH.getLevel()));
        addHeader(Skill.DEFENCE.getName(), String.valueOf(Skill.DEFENCE.getLevel()));
        addHeader(Skill.RANGED.getName(), String.valueOf(Skill.RANGED.getLevel()));
        addHeader(Skill.ATTACK.getName(), String.valueOf(Skill.ATTACK.getLevel()));
        // todo probably range, if highest skill is range set to attacker, ignore special forces
//        addHeader(Skill.STRENGTH.getName(), String.valueOf(Skill.STRENGTH.getLevel()));
        try {
            connectBlocking();
            log("Connected");
        } catch (InterruptedException e) {
            log("Failed to connect a comms client");
        }
    }

    private static int port = 6057;

    public static CorpClient getInstance(int teamSize, int attackersPerTeam, WrappedLocation worldLocation, boolean forceHost) {
        if (instance == null) {
            instance = new CorpClient(teamSize, attackersPerTeam, worldLocation, forceHost);
        }
        return instance;
    }

    public static CorpClient getInstance() {
        // this should only be called once the server has alright started
        if (instance == null) {
            return null;
        }
        return instance;
    }

    public static boolean isInstantiated() {
        return instance != null;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log("Client open");
    }

    @Override
    public void onMessage(String s) {
        log("Team update message");
        Gson g = new Gson();
        CorpUpdateTeamMessage m = g.fromJson(s, CorpUpdateTeamMessage.class);
        team = m.team;
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log("Client close");
    }

    @Override
    public void onError(Exception e) {
        log("Client error " + e);
    }

    public static CorpTeam getTeam() {
        if (instance == null) return null;
        return instance.team;
    }

    public static CorpRole getRole() {
        if (instance == null) return null;
        if (instance.team == null) return null;
        CorpRole r = instance.team.memberRoles.getOrDefault(Players.getLocal().getName(), null);
        return r;
    }

    private void log(String msg) {
        Logger.log(Color.magenta, "[Corp comms client] " + msg);
    }

    public static int getBGSDamageDelt() {
        if (instance == null) return 0;
        if (instance.team == null) return 0;
        return instance.team.bgsDamage;
    }

    public static void requestUpdate() {
        if (instance == null) {
            Logger.info("Instance null reqing update");
            return;
        }
        instance.send(new Gson().toJson(new CorpRequestUpdate()));
    }

    public static void recordCorpDeath() {
        if (instance == null) return;
        if (instance.team == null) return;
        instance.send(new Gson().toJson(new CorpKillCompleteMessage()));
    }

    public static void recordDWHHit() {
        if (instance == null) return;
        if (instance.team == null) return;
        instance.send(new Gson().toJson(new CorpDWHMessage()));
    }

    public static void recordBGSHit(int estDmg) {
        if (instance == null) return;
        if (instance.team == null) return;
        instance.send(new Gson().toJson(new CorpBGSMessage(estDmg)));
    }

    public static int getDHWSpecsLanded() {
        if (instance == null) return 0;
        if (instance.team == null) return 0;
        return instance.team.dwhSpecCount;
    }

    public static String getLeader() {
        if (instance == null) return "";
        if (instance.team == null) return "";
        return instance.team.getLeader();
    }

    public static List<String> getMembers() {
        if (instance == null) return null;
        if (instance.team == null) return null;
        return instance.team.memberRoles.keySet().stream().filter(x -> !x.equals(Players.getLocal().getName())).collect(Collectors.toList());
    }

    public static int getCorpWorld() {
        if (instance == null) return 379;
        if (instance.team == null) return 379;
        return instance.team.world;
    }
}
