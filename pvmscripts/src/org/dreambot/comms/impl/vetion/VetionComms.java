package org.dreambot.comms.impl.vetion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.comms.AbstractCommServer;
import org.dreambot.comms.impl.vetion.messages.VetionMessageType;
import org.dreambot.comms.impl.vetion.messages.VetionPkReportMessage;
import org.dreambot.comms.impl.vetion.messages.VetionTeamState;
import org.dreambot.scriptdata.VetionSettings;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.fractalsettings.SettingsRepository;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class VetionComms extends AbstractCommServer {
    final int teamSize;
    final WrappedLocation location;
    Map<String, WebSocket> userMap = new ConcurrentHashMap<>();
    List<VetionTeamState> teams = new CopyOnWriteArrayList<>();

    public VetionComms(int teamSize, WrappedLocation location) {
        this.teamSize = teamSize;
        this.location = location;
        log("Starting vetion comms");
    }

    private static VetionComms instance;

    public static VetionComms getInstance() {
        if (instance == null) {
            VetionSettings settings = SettingsRepository.findInstanceOf(new VetionSettings());
            instance = new VetionComms(settings.teamSize, settings.loc);
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
        if (user != null) {
            log("Open connection for " + AnalyticsReporter.hashStringSHA256(user));
            // add to map
            userMap.put(user, webSocket);
            // find a team with this username in the memberlist
            VetionTeamState alreadyInTeam = teams.stream()
                    .filter(x -> x.getMembers().contains(user))
                    .findFirst()
                    .orElse(null);
            if (alreadyInTeam != null) {
                log("Found an already in team " + alreadyInTeam.getTeamId());
                webSocket.send(new Gson().toJson(alreadyInTeam));
                return;
            }


            // assign a team and send to new user
            VetionTeamState firstOpenTeam = teams.stream()
                    .filter(x -> x.getMembers().size() < teamSize)
                    .findFirst()
                    .orElse(null);
            if (firstOpenTeam == null) {
                log("New team " + teams.size() + 1);
                firstOpenTeam = new VetionTeamState()
                        .setWorld(Worlds.getRandomWorld(x -> x.isMembers()
                                && location.isRegion(x.getLocation())
                                && x.getPopulation() < 900
                                && x.isNormal() && x.getMinimumLevel() < 100).getWorld())
                        .setTeamId(teams.size() + 1);
                teams.add(firstOpenTeam);
            }
            log("Send to open");
            firstOpenTeam.getMembers().add(user);
            webSocket.send(new Gson().toJson(firstOpenTeam));
        }

    }

    @Override
    public void onMessage(WebSocket socket, String msg) {
        JsonObject obj = JsonParser.parseString(msg).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        VetionMessageType type = null;

        try {
            type = VetionMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
            log("Couldn't find type " + msg);
            return;
        }

        Gson gson = new Gson();
        switch (type) {
            case TEAM_STATE:
                // broadcast update to all memebers
                VetionTeamState newTeamState = gson.fromJson(msg, VetionTeamState.class);
                log("Broadcasting to team");
                updateTeam(msg, newTeamState);
                break;
            case REQUEST_TEAM:
                // todo assign or broadcast existing team
                break;
            case REPORT_PKER:
                VetionPkReportMessage pkReport = gson.fromJson(msg, VetionPkReportMessage.class);
                log("PK report Team: " + pkReport.getTeamId() + " Pker: " + pkReport.getOpp());
                VetionTeamState state = teams.stream().filter(x -> x.getTeamId() == pkReport.getTeamId())
                        .findFirst().orElse(null);
                if (state == null) {
                    log("Failed to find a team with that ID");
                    return;
                }

                String reporter = pkReport.getTeamMember();
                if (!userMap.containsKey(reporter)) {
                    log("Failed to find reporter in userMap so resetting their connection " + pkReport);
                    userMap.put(reporter, socket);
                }

                if (!userMap.get(reporter).equals(socket)) {
                    log("User had outdated socket on pk report so refreshing " + pkReport);
                    userMap.put(reporter, socket);
                }

                if (!state.getOpps().contains(pkReport.getOpp())) state.getOpps().add(pkReport.getOpp());



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
                    state.setWorld(newWorld);
                    updateTeam(state);
                } else {
                    updateTeam(state);
                }
                break;
        }
    }

    private void updateTeam(String msg, VetionTeamState state) {
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                conn.send(msg);
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }


    private void updateTeam( VetionTeamState state) {
        for (String member : state.getMembers()) {
            WebSocket conn = userMap.get(member);
            if (conn != null) {
                log("Sending to member " + AnalyticsReporter.hashStringSHA256(member));
                conn.send(new Gson().toJson(state));
            } else {
                log("Could find conn for " + AnalyticsReporter.hashStringSHA256(member));
            }
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("Vetion close");
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("Vetion close");
    }

    private void log(String log) {
        Logger.log(Color.PINK, ("[Vetion Comms] - " + log));
    }
}
