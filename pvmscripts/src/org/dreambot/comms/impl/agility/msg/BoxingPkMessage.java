package org.dreambot.comms.impl.agility.msg;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class BoxingPkMessage {
    @SerializedName("routeCode")
    final String route = "boxing";
    @SerializedName("username")
    String username; // username for the account sending the warning
    @SerializedName("currentWorld")
    int currentWorld;
}
