package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SettingsData {
    @SerializedName("buryBones")
    boolean buryBones;
    @SerializedName("trainTo73")
    boolean trainTo73;
}
