package org.dreambot.comms.impl.callisto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Timer;
import org.dreambot.comms.AbstractCommServer;
import org.dreambot.comms.EverythingCommServer;
import org.dreambot.comms.impl.callisto.messages.CallistoMessageType;
import org.dreambot.comms.impl.callisto.messages.CallistoPkReportMessage;
import org.dreambot.comms.impl.callisto.messages.CallistoRequestTeam;
import org.dreambot.comms.impl.callisto.messages.CallistoTeamState;
import org.dreambot.scriptdata.CallistoSettings;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CallistoComms extends AbstractCommServer {
    final int teamSize;
    final WrappedLocation location;
    Map<String, WebSocket> userMap = new ConcurrentHashMap<>();
    List<CallistoTeamState> teams = new CopyOnWriteArrayList<>();
    public static int forceWorld = -1;

    public CallistoComms(int teamSize, WrappedLocation location) {
        this.teamSize = teamSize;
        this.location = location;
        log("Starting callisto comms");
    }

    private static CallistoComms instance;

    public static CallistoComms getInstance() {
        if (instance == null) {
            CallistoSettings settings = SettingsRepository.findInstanceOf(new CallistoSettings());
            instance = new CallistoComms(settings.teamSize, settings.loc);
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
            CallistoTeamState alreadyInTeam = teams.stream()
                    .filter(x -> x.getMembers().contains(user))
                    .findFirst()
                    .orElse(null);
            if (alreadyInTeam != null) {
                log("Found an already in team " + alreadyInTeam.getTeamId());
                updateTeam(alreadyInTeam);
                return;
            }


            // assign a team and send to new user
            CallistoTeamState firstOpenTeam = teams.stream()
                    .filter(x -> x.getMembers().size() < teamSize)
                    .findFirst()
                    .orElse(null);
            log("Group size " + teamSize);
            if (firstOpenTeam == null) {
                log("New team " + teams.size());
                firstOpenTeam = new CallistoTeamState()
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

        CallistoMessageType type = null;

        try {
            type = CallistoMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + msg);
            return;
        }

        Gson gson = new Gson();
        switch (type) {
            case TEAM_STATE:
                // broadcast update to all memebers
                CallistoTeamState newTeamState = gson.fromJson(msg, CallistoTeamState.class);
                log("Broadcasting to team");
                updateTeam(msg, newTeamState);
                break;
            case REQUEST_TEAM:
                // todo assign or broadcast existing team

                CallistoRequestTeam tr = gson.fromJson(msg, CallistoRequestTeam.class);
                log("team request ");
                // add to map
                userMap.put(tr.getTeamMember(), socket);
                // find a team with this username in the memberlist
                CallistoTeamState alreadyInTeam = teams.stream()
                        .filter(x -> x.getMembers().contains(tr.getTeamMember()))
                        .findFirst()
                        .orElse(null);
                if (alreadyInTeam != null) {
                    log("Found an already in team " + alreadyInTeam.getTeamId());
                    updateTeam(alreadyInTeam);
                    return;
                }


                // assign a team and send to new user
                CallistoTeamState firstOpenTeam = teams.stream()
                        .filter(x -> x.getMembers().size() < teamSize)
                        .findFirst()
                        .orElse(null);
                if (firstOpenTeam == null) {
                    log("New team " + teams.size() + 1);
                    firstOpenTeam = new CallistoTeamState()
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
                CallistoPkReportMessage pkReport = gson.fromJson(msg, CallistoPkReportMessage.class);
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

                CallistoTeamState state = teams.stream().filter(x -> x.getTeamId() == pkReport.getTeamId())
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

    private void updateTeam(String msg, CallistoTeamState state) {
        log("Team update ---");
        logState();
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                EverythingCommServer.log("Callisto update team " + state + msg);
                conn.send(msg);
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }

    private void updateTeam(CallistoTeamState state) {
        log("Team update ---");
        logState();
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                EverythingCommServer.log("Callisto update team " + state);
                conn.send(new Gson().toJson(state));
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }

    private void logState() {
        for (CallistoTeamState team : teams) {
            log(team.toString());
        }

        userMap.entrySet().forEach((e) -> log(e.getKey() + " " + e.getValue()));
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("Callisto close");
        if (userMap.containsValue(webSocket)) {
            log("Close, user removing from usermap");
            logState();
            userMap.entrySet().removeIf(x -> x.getValue().equals(webSocket));
            logState();
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("Callisto error " + e);
        if (userMap.containsValue(webSocket)) {
            log("Error, user removing from usermap " + e);
            logState();
            userMap.entrySet().removeIf(x -> x.getValue().equals(webSocket));
            logState();
        }

    }

    private void log(String log) {
        EverythingCommServer.log("[Callisto Comms] - " + log);
//        Logger.log(Color.PINK, ("[Callisto Comms] - " + log));
    }
}
