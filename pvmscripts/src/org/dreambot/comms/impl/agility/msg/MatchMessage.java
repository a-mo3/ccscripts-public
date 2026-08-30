package org.dreambot.comms.impl.agility.msg;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class MatchMessage {
    @Getter
    @SerializedName("teamMate")
    String teamMate;
    @Getter
    @Setter
    @SerializedName("world")
    int world = 384;

    @SerializedName("messageType")
    BoxingMessageTypes messageType = BoxingMessageTypes.MATCH;
    @SerializedName("routeCode")
    String routeCode = "boxing";

    public MatchMessage(String teamMate) {
        this.teamMate = teamMate;
    }

    public MatchMessage(String teamMate, int world) {
        this.teamMate = teamMate;
        this.world = world;
    }
}
