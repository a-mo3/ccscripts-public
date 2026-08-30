package org.dreambot.comms.impl.callisto.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class CallistoTeamState {
    @SerializedName("messageType")
    CallistoMessageType messageType = CallistoMessageType.TEAM_STATE;
    @SerializedName("routeCode")
    String routeCode = "callisto";
    @SerializedName("teamId")
    int teamId = -1;
    @SerializedName("world")
    int world = 390;
    // theres no team leader for this
    @SerializedName("members")
    List<String> members = new ArrayList<>();
    // opposition, enemy, pkers
    // like king von....
    @SerializedName("opps")
    List<String> opps = new ArrayList<>();
}
