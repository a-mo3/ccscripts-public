package org.dreambot.discordwebhook.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DiscordImage {
    @SerializedName("url")
    String url;
}
