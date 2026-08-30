package org.dreambot.comms.impl.callisto.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.wrappers.interactive.Player;

@Getter
public class CallistoRequestTeam {
    @SerializedName("messageType")
    CallistoMessageType messageType = CallistoMessageType.REQUEST_TEAM;
    @SerializedName("teamMember")
    String teamMember = Players.getLocal().getName();
    @SerializedName("routeCode")
    String routeCode = "callisto";
}
