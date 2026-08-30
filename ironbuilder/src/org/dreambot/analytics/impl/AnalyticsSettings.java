package org.dreambot.analytics.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dreambot.gui.UIExplanation;

@Accessors(chain = true) @Setter
@ToString
public class AnalyticsSettings {
    @SerializedName("enabled")
    @UIExplanation("Participates in antiban testing and bug dection by sharing information every 15 minutes")
    public boolean enabled = false; // has to be opt in

    @SerializedName("keepLocalCopy")
    @UIExplanation("Saves heartbeats locally in scripts/ccanalytics/{nickname}/{timestamp}.bin")
    public boolean localCopy = false; // has to be opt in
}
