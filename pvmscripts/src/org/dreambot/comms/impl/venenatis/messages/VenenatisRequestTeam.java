package org.dreambot.comms.impl.venenatis.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import org.dreambot.api.methods.interactive.Players;

@Getter
public class VenenatisRequestTeam {
    @SerializedName("messageType")
    VenenatisMessageType messageType = VenenatisMessageType.REQUEST_TEAM;
    @SerializedName("teamMember")
    String teamMember = Players.getLocal().getName();
    @SerializedName("routeCode")
    String routeCode = "venenatis";
}
