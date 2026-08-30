package org.dreambot.settings.timing;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@ToString
public class ReactionSettings {
    @SerializedName("quickLow")
    public int quickLow = 30;
    @SerializedName("quickHigh")
    public int quickHigh = 200;

    @SerializedName("normalLow")
    public int normalLow = 450;
    @SerializedName("normalHigh")
    public int normalHigh = 850;

    @SerializedName("longLow")
    public int longLow = 400;
    @SerializedName("longHigh")
    public int longHigh = 900;
}
