package org.dreambot.settings.timing;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ReactionSettings {
    @SerializedName("quickLow")
    private int quickLow = 30;
    @SerializedName("quickHigh")
    private int quickHigh = 200;

    @SerializedName("normalLow")
    private int normalLow = 450;

    @SerializedName("normalHigh")
    private int normalHigh = 850;

    @SerializedName("longLow")
    private int longLow = 400;

    @SerializedName("longHigh")
    private int longHigh = 900;
}
