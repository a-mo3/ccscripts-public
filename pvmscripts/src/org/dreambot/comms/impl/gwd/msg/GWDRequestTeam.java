package org.dreambot.comms.impl.gwd.msg;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import org.dreambot.comms.impl.agility.msg.BoxingMessageTypes;
import org.dreambot.comms.impl.gwd.GodWarsBosses;
import org.dreambot.comms.impl.gwd.GodWarsMessageType;

@AllArgsConstructor
public class GWDRequestTeam {
    @SerializedName("messageType")
    final GodWarsMessageType messageType = GodWarsMessageType.REQUEST_TEAM;
    @SerializedName("routeCode")
    public final String route = "gwd";

    @SerializedName("username")
    public String username;
    @SerializedName("boss")
    public GodWarsBosses boss;
}
