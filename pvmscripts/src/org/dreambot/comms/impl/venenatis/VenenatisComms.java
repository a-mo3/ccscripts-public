package org.dreambot.comms.impl.venenatis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Timer;
import org.dreambot.comms.AbstractCommServer;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.venenatis.messages.VenenatisMessageType;
import org.dreambot.comms.impl.venenatis.messages.VenenatisPkReportMessage;
import org.dreambot.comms.impl.venenatis.messages.VenenatisRequestTeam;
import org.dreambot.comms.impl.venenatis.messages.VenenatisTeamState;
import org.dreambot.scriptdata.VenenatisSettings;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VenenatisComms extends AbstractCommServer {
    final int teamSize;
    final WrappedLocation location;
    Map<String, WebSocket> userMap = new ConcurrentHashMap<>();
    List<VenenatisTeamState> teams = new CopyOnWriteArrayList<>();
    public static int forceWorld = -1;

    public VenenatisComms(int teamSize, WrappedLocation location) {
        this.teamSize = teamSize;
        this.location = location;
        log("Starting Venenatis comms");
    }

    private static VenenatisComms instance;

    public static VenenatisComms getInstance() {
        if (instance == null) {
            // todo replace this with something that would read from file path, because the everything comms could host all 3
            VenenatisSettings settings = SettingsRepository.findInstanceOf(new VenenatisSettings());
            instance = new VenenatisComms(settings.teamSize, settings.loc);
        }
        return instance;
    }

    /**
     * we want to change world when getting a pk event, but pk events will come with multiple reports
     * so we only allow a team to change world once every 5 minutes
     * map teamId, 5 min timer
     */
    Map<Integer, Timer> worldSwitchTimers = new HashMap<>();

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log("On open");

        String user = clientHandshake.getFieldValue("username");
        if (user != null && !user.isEmpty()) {
            log("Open connection for " + AnalyticsReporter.hashStringSHA256(user));
            // add to map
            userMap.put(user, webSocket);
            // find a team with this username in the memberlist
            VenenatisTeamState alreadyInTeam = teams.stream()
                    .filter(x -> x.getMembers().contains(user))
                    .findFirst()
                    .orElse(null);
            if (alreadyInTeam != null) {
                log("Found an already in team " + alreadyInTeam.getTeamId());
                updateTeam(alreadyInTeam);
                return;
            }


            // assign a team and send to new user
            VenenatisTeamState firstOpenTeam = teams.stream()
                    .filter(x -> x.getMembers().size() < teamSize)
                    .findFirst()
                    .orElse(null);
            if (firstOpenTeam == null) {
                log("New team " + teams.size() + 1);
                firstOpenTeam = new VenenatisTeamState()
                        .setWorld(forceWorld <= 0 ? Worlds.getRandomWorld(x -> x.isMembers()
                                && location.isRegion(x.getLocation())
                                && x.getPopulation() < 900
                                && x.isNormal() && x.getMinimumLevel() < 100).getWorld() : forceWorld)
                        .setTeamId(teams.size() + 1);
                teams.add(firstOpenTeam);
            }
            log("Send to open");
            firstOpenTeam.getMembers().add(user);
            updateTeam(firstOpenTeam);
        }

    }

    @Override
    public void onMessage(WebSocket socket, String msg) {
        JsonObject obj = JsonParser.parseString(msg).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        VenenatisMessageType type = null;

        try {
            type = VenenatisMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + msg);
            return;
        }

        Gson gson = new Gson();
        switch (type) {
            case TEAM_STATE:
                // broadcast update to all memebers
                VenenatisTeamState newTeamState = gson.fromJson(msg, VenenatisTeamState.class);
                log("Broadcasting to team");
                updateTeam(msg, newTeamState);
                break;
            case REQUEST_TEAM:
                VenenatisRequestTeam tr = gson.fromJson(msg, VenenatisRequestTeam.class);
                log("team request ");
                // add to map
                userMap.put(tr.getTeamMember(), socket);
                // find a team with this username in the memberlist
                VenenatisTeamState alreadyInTeam = teams.stream()
                        .filter(x -> x.getMembers().contains(tr.getTeamMember()))
                        .findFirst()
                        .orElse(null);
                if (alreadyInTeam != null) {
                    log("Found an already in team " + alreadyInTeam.getTeamId());
                    updateTeam(alreadyInTeam);
                    return;
                }


                // assign a team and send to new user
                VenenatisTeamState firstOpenTeam = teams.stream()
                        .filter(x -> x.getMembers().size() < teamSize)
                        .findFirst()
                        .orElse(null);
                if (firstOpenTeam == null) {
                    log("New team " + teams.size() + 1);
                    firstOpenTeam = new VenenatisTeamState()
                            .setWorld(forceWorld <= 0 ? Worlds.getRandomWorld(x -> x.isMembers()
                                    && location.isRegion(x.getLocation())
                                    && x.getPopulation() < 900
                                    && x.isNormal() && x.getMinimumLevel() < 100).getWorld() : forceWorld)
                            .setTeamId(teams.size() + 1);
                    teams.add(firstOpenTeam);
                }
                log("Send to open");
                firstOpenTeam.getMembers().add(tr.getTeamMember());
                updateTeam(firstOpenTeam);

                break;
            case REPORT_PKER:
                VenenatisPkReportMessage pkReport = gson.fromJson(msg, VenenatisPkReportMessage.class);
                log("PK report Team: " + pkReport.getTeamId() + " Pker: " + pkReport.getOpp());

                String reporter = pkReport.getTeamMember();
                if (!userMap.containsKey(reporter)) {
                    log("Failed to find reporter in userMap so resetting their connection " + pkReport);
                    userMap.put(reporter, socket);
                }

                if (!userMap.get(reporter).equals(socket)) {
                    log("User had outdated socket on pk report so refreshing " + pkReport);
                    userMap.put(reporter, socket);
                }

                VenenatisTeamState state = teams.stream().filter(x -> x.getTeamId() == pkReport.getTeamId())
                        .findFirst().orElse(null);
                if (state == null) {
                    log("Failed to find a team with that ID");
                    return;
                }

                log("Pker " + pkReport.getOpp());
                if (!state.getOpps().contains(pkReport.getOpp())) {
                    log("Add to state " + pkReport.getOpp());
                    state.getOpps().add(pkReport.getOpp());
                }

                worldSwitchTimers.entrySet().removeIf(x -> x.getValue().finished());
                if (!worldSwitchTimers.containsKey(state.getTeamId())) {
                    worldSwitchTimers.put(state.getTeamId(), new Timer(1000 * 60 * 5));
                    int newWorld = Worlds.getRandomWorld(w -> w.isNormal()
                                    && w.isMembers()
                                    && w.getPopulation() < 900
                                    && w.getMinimumLevel() < 100
                                    && location.isRegion(w.getLocation())
                                    && w.getWorld() != state.getWorld())
                            .getWorld();
                    state.setWorld(forceWorld <= 0 ? newWorld : forceWorld);
                    updateTeam(state);
                } else {
                    updateTeam(state);
                }
                break;
        }
    }

    private void updateTeam(String msg, VenenatisTeamState state) {
        log("Team update ---");
        logState();
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                EverythingCommServer.log("Venenatis update team " + state + msg);
                conn.send(msg);
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }

    private void updateTeam(VenenatisTeamState state) {
        log("Team update ---");
        logState();
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                EverythingCommServer.log("Venenatis update team " + state);
                conn.send(new Gson().toJson(state));
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }

    private void logState() {
        for (VenenatisTeamState team : teams) {
            log(team.toString());
        }

        userMap.entrySet().forEach((e) -> log(e.getKey() + " " + e.getValue()));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("Venenatis close");
        if (userMap.containsValue(webSocket)) {
            log("Close, user removing from usermap");
            logState();
            userMap.entrySet().removeIf(x -> x.getValue().equals(webSocket));
            logState();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("Venenatis error " + e);
        if (userMap.containsValue(webSocket)) {
            log("Error, user removing from usermap " + e);
            logState();
            userMap.entrySet().removeIf(x -> x.getValue().equals(webSocket));
            logState();
        }

    }

    private void log(String log) {
        EverythingCommServer.log("[Venenatis Comms] - " + log);
    }
}
