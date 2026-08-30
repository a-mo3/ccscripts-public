package org.dreambot.behaviour.method.huey.comms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dreambot.analytics.AnalyticsReporter;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * models a huey team, its memebers, the leader
 * this is only ever sent to the users locally hosted instance of the HueyComms server
 */
@Setter @Getter @Builder
public class HueyTeam {
    @SerializedName("teamId")
    int teamId = 0;
    @SerializedName("world")
    int world = 305;
    @SerializedName("teamLeader")
    String teamLeader;
    @SerializedName("teamMembers")
    Set<String> members;

    // no logging usernames so hash them all before logging
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(HueyTeam.builder()
                .teamId(this.teamId)
                .world(this.world)
                .teamLeader(AnalyticsReporter.hashStringSHA256(this.teamLeader))
                .members(this.members.stream().map(AnalyticsReporter::hashStringSHA256).collect(Collectors.toSet()))
                .build()
        );
    }
}
