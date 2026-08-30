package org.dreambot.discordwebhook;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DiscordSettings {
    @SerializedName("webhookURL")
    String url;
    @SerializedName("hideCreds")
    boolean hideCreds;
    @SerializedName("compactWebhooks")
    boolean compact;
}
