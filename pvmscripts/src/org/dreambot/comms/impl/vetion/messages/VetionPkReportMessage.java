package org.dreambot.comms.impl.vetion.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class VetionPkReportMessage {
    @SerializedName("messageType")
    VetionMessageType messageType = VetionMessageType.REPORT_PKER;
    @SerializedName("teamMember")
    String teamMember = "reportingMember"; // when we send a pk report we want to report whos sending it, to refresh user map
    @SerializedName("routeCode")
    String routeCode = "vetion";
    @SerializedName("teamId")
    int teamId = -1;
    @SerializedName("opp")
    String opp;
}
