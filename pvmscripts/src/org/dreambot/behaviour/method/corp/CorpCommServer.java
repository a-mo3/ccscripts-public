package org.dreambot.behaviour.method.corp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.method.corp.messages.*;
import org.dreambot.settings.WrappedLocation;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle state updates and broadcast to the entire team
 * <p>
 * todo it is possible multiple teams have the same username in them, this shouldnt be possible
 */
public class CorpCommServer extends WebSocketServer {
    ConcurrentHashMap<String, WebSocket> connectedAccounts = new ConcurrentHashMap<>();
    List<CorpTeam> teams = new ArrayList<>();

    final int teamSize;
    final int specialsPerTeam;
    final WrappedLocation location;

    public CorpCommServer(int teamSize, int specialsPerTeam, WrappedLocation location) {
        super(new InetSocketAddress(6057), Runtime.getRuntime().availableProcessors(), null);
        this.teamSize = teamSize;
        this.specialsPerTeam = specialsPerTeam;
        this.location = location;
        start();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log("- Current teams -");
        teams.forEach(e -> log(String.format("Team " + e.teamId + " - " + e)));

        // fetch username from handshake
        String username = clientHandshake.getFieldValue("username");
        if (username == null || username.isEmpty()) {
            log("Connection from a client with null username");
            return;
        }
        log("Open connection for user " + AnalyticsReporter.hashStringSHA256(username));
        connectedAccounts.put(username, webSocket);

        CorpTeam t = getTeamFromUsername(username);
        if (t != null) {
            log("Connected account had a previous team, reassigning");
            updateTeam(t);
            return;
        }

//        updateUser(username);

        // check for force host flag
        String forceHost = clientHandshake.getFieldValue("forceHost");
        if ("true".equals(forceHost)) {
            log("This connection started with the forceHost flag");
            // force this account to be a host, of a new team
            Map<String, CorpRole> memberRoles = new HashMap<>();
            memberRoles.put(username, CorpRole.HOST);
            CorpTeam newTeam = CorpTeam.builder()
                    .world(Worlds.getRandomWorld(x -> x.getMinimumLevel() < 100
                                    && x.isNormal()
                                    && x.isMembers()
                                    && location.isRegion(x.getLocation())
                            )
                            .getWorld())
                    .teamId(Calculations.random(9000000))
                    .memberRoles(memberRoles)
                    .build();
            teams.add(newTeam);
            // update all team members of roles (just self)
            updateTeam(newTeam);
            return;
        }


        CorpTeam corpTeam = teams.stream()
                .filter(x -> x.memberRoles.size() < teamSize)
                .findFirst().orElse(null);
        if (corpTeam == null) {
            log("No team, become host");
            Map<String, CorpRole> memberRoles = new HashMap<>();
            memberRoles.put(username, CorpRole.HOST);
            CorpTeam newTeam = CorpTeam.builder()
                    .world(Worlds.getRandomWorld(x -> x.getMinimumLevel() < 100
                                    && x.isNormal()
                                    && x.isMembers()
                                    && location.isRegion(x.getLocation())
                            )
                            .getWorld())
                    .teamId(Calculations.random(9000000))
                    .memberRoles(memberRoles)
                    .build();
            teams.add(newTeam);
            // update all team members of roles (just self)
            updateTeam(newTeam);
            return;
        }
        // check stat headers for what role to assign
        int str = Integer.parseInt(clientHandshake.getFieldValue(Skill.STRENGTH.getName()));
        int def = Integer.parseInt(clientHandshake.getFieldValue(Skill.DEFENCE.getName()));
        int range = Integer.parseInt(clientHandshake.getFieldValue(Skill.RANGED.getName()));
        int atk = Integer.parseInt(clientHandshake.getFieldValue(Skill.ATTACK.getName()));
        if ((range < 60) && (atk < 75 || str < 75)) {
            log("This account cannot spec because its stats are too low " + str + " " + atk);
            // find the first team below capacity
            log("Set attacked");
            corpTeam.setMember(username, CorpRole.ATTACKER);
            updateTeam(corpTeam);
        } else {
            // todo assign attacker or special forces depending on the count
            String forceSpec = clientHandshake.getFieldValue("forceSpec");
            if (corpTeam.activeSpecialForces() < specialsPerTeam) {
                log("Special forces slot open, assigning this account to delta force. semper fi");
                corpTeam.setMember(username, CorpRole.SPECIAL_FORCES);
                updateTeam(corpTeam);
            } else {
                log("Special forces slot closed, assigning spearmen");
                corpTeam.setMember(username, CorpRole.ATTACKER);
                updateTeam(corpTeam);
            }
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("On close");
        disconnectUser(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        CorpMessageType type = null;

        try {
            type = CorpMessageType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
        }

        if (type == null) {
            log("Couldn't find type " + s);
            return;
        }

        Gson gson = new Gson();
        log("Msg received " + type);

        switch (type) {
            case REQUEST_UPDATE:
                // do the entire startup procedure again if account has no socket
                CorpRequestUpdate reqUpdateMsg = gson.fromJson(s, CorpRequestUpdate.class);
                log("Req update message");
                connectedAccounts.put(reqUpdateMsg.name, webSocket);
                CorpTeam foundTeam = getTeamFromUsername(reqUpdateMsg.name);
                if (foundTeam == null) {
                    log("No team found lets assign this account a team");

                    CorpTeam corpTeam = teams.stream()
                            .filter(x -> x.memberRoles.size() < teamSize)
                            .findFirst().orElse(null);
                    if (corpTeam == null) {
                        log("No team with slot open, become host");
                        Map<String, CorpRole> memberRoles = new HashMap<>();
                        memberRoles.put(reqUpdateMsg.name, CorpRole.HOST);
                        CorpTeam newTeam = CorpTeam.builder()
                                .world(Worlds.getRandomWorld(x -> x.getMinimumLevel() < 100
                                                && x.isNormal()
                                                && x.isMembers()
                                                && location.isRegion(x.getLocation())
                                        )
                                        .getWorld())
                                .teamId(Calculations.random(9000000))
                                .memberRoles(memberRoles)
                                .build();
                        teams.add(newTeam);
                        // update all team members of roles (just self)
                        updateTeam(newTeam);
                        return;
                    }

                    if (corpTeam.activeSpecialForces() < specialsPerTeam) {
                        log("Special forces slot open, assigning this account to delta force. semper fi");
                        corpTeam.setMember(reqUpdateMsg.name, CorpRole.SPECIAL_FORCES);
                        updateTeam(corpTeam);
                    } else {
                        log("Special forces slot closed, assigning spearmen");
                        corpTeam.setMember(reqUpdateMsg.name, CorpRole.ATTACKER);
                        updateTeam(corpTeam);
                    }

                } else {
                    updateTeam(foundTeam); // broadcast the update so the requesting account will get its information
                }


                break;
            case BGS_SPEC_HIT:
                // increment spec damage on this team
                CorpBGSMessage msg = gson.fromJson(s, CorpBGSMessage.class);
                CorpTeam team = getTeamFromUsername(getUsernameForSocket(webSocket));
                if (team == null) {
                    log("BGS spec but not in a team?");
                    return;
                }
                log("Register BGS spec " + msg.damage);
                team.bgsDamage += msg.damage;
                updateTeam(team);
                break;
            case DWH_SPEC_HIT:
                // increment spec count on this team
                team = getTeamFromUsername(getUsernameForSocket(webSocket));
                if (team == null) {
                    log("not in a team?");
                    return;
                }
                log("Register DWH spec ");
                team.dwhSpecCount += 1;
                updateTeam(team);
                break;
            case KILL_COMPLETE:
                // reset spec counters and increment the kill count
                team = getTeamFromUsername(getUsernameForSocket(webSocket));
                if (team == null) {
                    log("not in a team?");
                    return;
                }
                team.dwhSpecCount = 0;
                team.bgsDamage = 0;
                team.killCount++;
                updateTeam(team);
                break;
        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("Corp server error " + AnalyticsReporter.hashStringSHA256(getUsernameForSocket(webSocket)) + " " + e);
        Arrays.stream(e.getStackTrace()).forEach(Logger::info);
        if (webSocket != null) disconnectUser(webSocket);
    }

    @Override
    public void onStart() {
        log("Corp server start");
    }

    private void updateFromSocket(WebSocket socket) {
        updateTeam(getTeamFromUsername(getUsernameForSocket(socket)));
    }

    private CorpTeam getTeamFromUsername(String username) {
        if (username == null) return null;
        return teams.stream()
                .filter(x -> x.memberRoles.containsKey(username))
                .findFirst().orElse(null);
    }

    private String getUsernameForSocket(WebSocket socket) {
        if (socket == null) return null;
        return connectedAccounts.entrySet().stream()
                .filter(x -> x.getValue().equals(socket))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private void updateTeam(CorpTeam team) {
        if (team == null) {
            log("Tried to update a null team");
            return;
        }
        log("Updating team " + team.teamId);
        Gson gson = new Gson();
        for (String username : team.memberRoles.keySet()) {
            log("Broadcasting for team member " + AnalyticsReporter.hashStringSHA256(username));
            if (!connectedAccounts.containsKey(username)) {
                log("Oh no! account unable to update, no connected socket. ");
                continue;
            }
            connectedAccounts.get(username).send(gson.toJson(new CorpUpdateTeamMessage(team)));
        }
    }

    private void disconnectUser(WebSocket socket) {
        String name = getUsernameForSocket(socket);
        if (name == null) {
            log("Disconnecting but failed to find name");
            return;
        }
        log("Disconnecting " + AnalyticsReporter.hashStringSHA256(name));
        CorpTeam team = getTeamFromUsername(name);
        if (team == null) {
            log("Not in a team");
            return;
        }

        if (name.equalsIgnoreCase(team.getLeader())) {
            log("Is team leader" + AnalyticsReporter.hashStringSHA256(name));
            // todo repatriate accounts when this happens
            return;
        }
        team.memberRoles.remove(name);
        if (team.memberRoles.isEmpty()) teams.remove(team);
        updateTeam(team);
    }

    private void log(String msg) {
        Logger.log(Color.GREEN, "[Corp lobby server] " + msg);
    }

    @Override
    public void start() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Uncaught exception (server already running probably, can ignore) " + e));
        super.start();
    }

    @Override
    public void run() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> Logger.info("Uncaught exception (server already running probably, can ignore) " + e));
        super.run();
    }
}
