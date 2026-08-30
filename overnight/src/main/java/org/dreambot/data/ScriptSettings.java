package org.dreambot.data;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ScriptSettings {
    @SerializedName("waitingPeriodInHours")
    int waitingPeriodInHours = 4;
    @SerializedName("flipItems")
    FlipItem[] flipItems;

}
