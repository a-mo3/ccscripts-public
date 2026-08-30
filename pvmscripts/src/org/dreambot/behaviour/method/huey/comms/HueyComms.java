package org.dreambot.behaviour.method.huey.comms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.dreambot.analytics.AnalyticsReporter;
import org.dreambot.api.methods.world.Location;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.utilities.Logger;
import org.dreambot.behaviour.GetOff330;
import org.dreambot.behaviour.method.huey.comms.messages.HueyLeaderRegroup;
import org.dreambot.behaviour.method.huey.comms.messages.HueyMsgType;
import org.dreambot.behaviour.method.huey.comms.messages.HueyUpdateMeMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WS server for arranging lobbies of hueycoatl private instance fight teams
 */
public class HueyComms extends WebSocketServer {
    ConcurrentHashMap<String, WebSocket> connectedAccounts = new ConcurrentHashMap<>();
    List<HueyTeam> teams = new CopyOnWriteArrayList<>();
    // how many people are on a team, needs to be at least 1
    final int teamSize;
    final Location location;

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

    public HueyComms(int teamSize, Location location) {
        super(new InetSocketAddress(6099), Runtime.getRuntime().availableProcessors(), null);
        this.teamSize = Math.max(teamSize, 1);
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
        connectedAccounts.put(username, webSocket);
        updateUser(username);
    }

    /**
     * send the json for a team object to all its members, updating them on the count of others they are playing with
     *
     * @param team team to update all members
     */
    private void updateTeam(HueyTeam team) {
        // update all team members
        Gson gson = new Gson();
        for (String member : team.members) {
            log("Updating team " + team.teamId);
            WebSocket conn = connectedAccounts.get(member);
            if (conn == null) {
                // no connection for member, should probably remove them
                log("Failed to find a connection associated with team member " + AnalyticsReporter.hashStringSHA256(member));
                return;
            }
            conn.send(gson.toJson(team));
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log("On close");
        // if team member we can just remove from the team
        // if team leader, elect a new user to be the team leader and update everyone in that team
        Map.Entry<String, WebSocket> disconnectingAccount = connectedAccounts.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(webSocket))
                .findFirst()
                .orElse(null);

        if (disconnectingAccount == null) {
            log("Account disconnected but was not found");
            return;
        }

        log("Account disconnecting " + AnalyticsReporter.hashStringSHA256(disconnectingAccount.getKey()));
        connectedAccounts.remove(disconnectingAccount.getKey());

        HueyTeam leadTeam = teams.stream()
                .filter(x -> x.getTeamLeader().equals(disconnectingAccount.getKey()))
                .findFirst().orElse(null);
        if (leadTeam != null) {
            // todo a teams member list should be concurrent its possible an account is assigned to the team while we're doing this
            //  (although it shouldnt when the user isnt being crazy)
            leadTeam.members.remove(disconnectingAccount.getKey());
            if (leadTeam.members.isEmpty()) {
                log("Removing the leader emptied the team, removing it " + leadTeam);
                leadTeam.setTeamLeader("Disconnected " + disconnectingAccount.getKey());
                teams.remove(leadTeam);
                return;
            }
            // elect new team leader
            String firstMember = leadTeam.members.toArray(new String[0])[0];
            log("Electing new leader for team " + leadTeam);
            leadTeam.setTeamLeader(firstMember);
            updateTeam(leadTeam);
            return;
        }

        // find a team that has this user as the member
        HueyTeam team = teams.stream()
                .filter(x -> x.members.contains(disconnectingAccount.getKey()))
                .findFirst().orElse(null);
        if (team != null) {
            log("disconnecting user was part of team " + team);
            team.members.remove(disconnectingAccount.getKey());
            updateTeam(team);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
        if (obj == null || !obj.has("messageType")) {
            log("message was null or did not have type " + obj);
            return;
        }

        HueyMsgType type = null;

        try {
            type = HueyMsgType.valueOf(String.valueOf(obj.get("messageType").getAsString()));
        } catch (Exception ignored) {
        }
        if (type == null) {
            log("Couldn't find type " + s);
            return;
        }

        Gson gson = new Gson();
        log("Msg received " + type);

        switch (type) {
            case UPDATE_ME:

                HueyUpdateMeMessage updateMeMesg = gson.fromJson(s, HueyUpdateMeMessage.class);
                connectedAccounts.put(updateMeMesg.username, webSocket);
                updateUser(updateMeMesg.username);
                return;
            case LEADER_REGROUP:
                HueyLeaderRegroup msg = gson.fromJson(s, HueyLeaderRegroup.class);
                if (msg == null || msg.username == null || msg.username.isEmpty()) {
                    log("Failed to get username during leader regroup msg");
                    return;
                }
                connectedAccounts.put(msg.username, webSocket);
                HueyTeam team = teams.stream()
                        .filter(x -> msg.username.equals(x.getTeamLeader()))
                        .findFirst().orElse(null);
                if (team == null) {
                    log("Failed to find the leader this regroup message is from");
                    return;
                }

                log("Broadcasting regroup message to team " + team);
                for (String member : team.getMembers()) {
                    // not efficient, dont care
                    WebSocket ws = connectedAccounts.get(member);
                    // broadcast to all team members;
                    log("Broadcasting regroup message");
                    ws.send(s);
                }
        }
    }

    private void updateUser(String username) {
        if (username == null || username.isEmpty()) {
            log("Null or empty username not updating.");
            return;
        }
        // if user is already part of a team, from being recently connected or otherwise requiring an update, send their team back to them
        HueyTeam inTeam = teams.stream().filter(x -> x.members.contains(username)).findFirst().orElse(null);
        if (inTeam != null) {
            log("User updated and was found in team " + inTeam);
            updateTeam(inTeam);
            return;
        }

        // assign user a team, or make it the leader of a new team
        if (teams.isEmpty()) {
            log("Teams is empty make first team");
            // no teams exist, make this user the leader of the first team
            HueyTeam newTeam = HueyTeam.builder()
                    .teamId(1)
                    .world(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER.and(x -> x.getLocation() == location)).getWorld())
                    .teamLeader(username)
                    .members(new HashSet<>())
                    .build();
            newTeam.members.add(username);
            teams.add(newTeam);
            updateTeam(newTeam);
            log("New team created " + newTeam);
            return;
        }

        // find the first team with less than the team size, join this team.
        for (HueyTeam team : teams) {
            log("Team exists " + team.teamId + " " + team.members.size() + "/" + this.teamSize);
            if (team.members.size() < this.teamSize) {
                // add this user
                team.members.add(username);
                updateTeam(team);
                return;
            }
        }

        // all teams full, in need of a new team
        log("All teams full, new team " + this.teamSize + " " + teams.size());
        HueyTeam newTeam = HueyTeam.builder()
                .teamId(teams.size() + 1)
                .world(Worlds.getRandomWorld(GetOff330.MEMBERS_WORLD_FILTER).getWorld())
                .teamLeader(username)
                .members(new HashSet<>())
                .build();
        newTeam.members.add(username);
        teams.add(newTeam);
        log("New team " + newTeam);
        updateTeam(newTeam);
        return;
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        log("Huey server error " + e);
    }

    @Override
    public void onStart() {
        log("Huey comms server started " + teamSize);
    }

    private void log(String msg) {
        Logger.log(Color.PINK, "[Huey lobby server] " + msg);
    }
}
