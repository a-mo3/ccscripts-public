package org.dreambot.comms.impl.gwd;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.comms.AbstractCommServer;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.gwd.msg.GWDRequestTeam;
import org.dreambot.scriptdata.KreearraSettings;
import org.dreambot.scriptdata.VenenatisSettings;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Match wilderness agility accounts with each other so they know who to box
 */
public class GodWarsComms extends AbstractCommServer {
    Map<String, WebSocket> userMap = new ConcurrentHashMap<>();
    List<GodWarsTeam> teams = new CopyOnWriteArrayList<>();
    String unmatched = null;
    // todo set this on script start
    public final int teamSize;

    public GodWarsComms(int t) {
        super();
        teamSize = t;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log("Box server open");
        String reqMsg = clientHandshake.getFieldValue("msg");
        if (reqMsg == null || reqMsg.isEmpty()) {
            log("No message attached");
            return;
        }

        Gson g = new Gson();
        GWDRequestTeam req = g.fromJson(reqMsg, GWDRequestTeam.class);
        userMap.put(req.username, webSocket);
    }

    @Override
    public void onMessage(WebSocket socket, String msg) {
        JsonObject obj = JsonParser.parseString(msg).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        GodWarsMessageType type = null;

        try {
            type = GodWarsMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + msg);
            return;
        }
        Gson g = new Gson();
        switch (type) {
            case REQUEST_TEAM:
                GWDRequestTeam a = g.fromJson(msg, GWDRequestTeam.class);
                userMap.put(a.username, socket);
                matchTeam(a);
                break;
            case CHANGE_WORLD:
                break;
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        String user = userMap.entrySet().stream()
                .filter((k) -> webSocket.equals(k.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        removePlayer(user);

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        String user = userMap.entrySet().stream()
                .filter((k) -> webSocket.equals(k.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        removePlayer(user);
    }

    private void removePlayer(String player) {
        if (player == null) return;
        for (GodWarsTeam team : teams) {
            if (team.members.contains(player)) {
                log("Removing player");
                team.members.remove(player);
                broadcastTeamUpdate(team);
            }
        }
    }

    private void matchTeam(GWDRequestTeam req) {
        // find a team that already has the user
        for (GodWarsTeam team : teams) {
            if (team.members.contains(req.username)) {
                log("Found an already in team for an update");
                broadcastTeamUpdate(team);
                return;
            }
        }

        // find a team that is not full
        for (GodWarsTeam team : teams) {
            if (team.members.size() < teamSize) {
                log("Found team below max size " + teamSize);
                team.addMember(req.username);
                broadcastTeamUpdate(team);
                return;
            }
        }
        // all teams full (or teams is empty), add a new one
        GodWarsTeam newTeam = new GodWarsTeam();
        newTeam.addMember(req.username);
        teams.add(newTeam);
        broadcastTeamUpdate(newTeam);
    }

    private void broadcastTeamUpdate(GodWarsTeam team) {
        for (String member : team.members) {
            WebSocket ws = userMap.get(member);
            if (ws == null) {
                log("Failed to find websocket for a member");
                continue;
            }
            Gson g = new Gson();
            try {
                ws.send(g.toJson(team));
            } catch (Exception e) {
                log("Failed to send update " + e);
            }
        }
    }

    private static GodWarsComms instance;

    public static GodWarsComms getInstance() {
        // todo support other bosses will require looking for other setting instances
        KreearraSettings settings = SettingsRepository.findInstanceOf(new KreearraSettings());
        if (instance == null) {
            // no settings
            instance = new GodWarsComms(settings.teamSize);
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
