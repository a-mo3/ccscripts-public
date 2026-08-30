package org.dreambot.discordwebhook;

import com.google.gson.annotations.SerializedName;

public class AutoProggySettings {
    @SerializedName("discordWebhookURL")
    public String webhookURL = "";
    @SerializedName("proggyEveryXHours")
    public int hours = 6;

}
