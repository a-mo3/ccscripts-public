package org.dreambot.settings.script;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.script.Unobfuscated;

@Data
@Unobfuscated
public class SettingsData {
    @SerializedName("initialGp")
    public int initalGp;
    @SerializedName("bankLogs")
    public boolean bankLogs = false;
    @SerializedName("bankOaks")
    public boolean bankOak = true;
    @SerializedName("chopYews")
    public boolean chopYews = false;
    @SerializedName("chopMagic")
    public boolean chopMagic = true;
    @SerializedName("chopRedwood")
    public boolean chopRedwood = false;
    @SerializedName("f2pOnly")
    public boolean ftpOnly = true;
    @SerializedName("muleOffQuantity")
    public int muleOffQuantity = 800;
    @SerializedName("logsCompetitionThreshold")
    public int logsCompetitionThreshold = 3;
    @SerializedName("oaksCompetitionThreshold")
    public int oaksCompetitionThreshold = 3;
    @SerializedName("yewsCompetitionThreshold")
    public int yewsCompetitionThreshold = 3;
    @SerializedName("hoursUntilMuleOff")
    public int hoursUntilMuleOff = 12;
    @SerializedName("gpRemainingAfterMuleOff")
    public int gpRemainingAfterMuling = 50_000;
}
