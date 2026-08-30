package org.dreambot.behaviour.method.corp.messages;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.corp.CorpTeam;

public class CorpUpdateTeamMessage {
    @SerializedName("messageType")
    CorpMessageType messageType = CorpMessageType.UPDATE_TEAM;
    @SerializedName("team")
    public CorpTeam team;

    public CorpUpdateTeamMessage(CorpTeam team) {
        this.team = team;
    }
}
