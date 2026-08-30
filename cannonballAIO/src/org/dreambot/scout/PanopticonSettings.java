package org.dreambot.scout;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PanopticonSettings {
    @SerializedName("enabled")
    boolean enabled = false;
}