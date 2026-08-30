package org.dreambot.comms.impl.venenatis.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class VenenatisPkReportMessage {
    @SerializedName("messageType")
    VenenatisMessageType messageType = VenenatisMessageType.REPORT_PKER;
    @SerializedName("teamMember")
    String teamMember = "reportingMember"; // when we send a pk report we want to report whos sending it, to refresh user map
    @SerializedName("routeCode")
    String routeCode = "venenatis";
    @SerializedName("teamId")
    int teamId = -1;
    @SerializedName("opp")
    String opp;
}
