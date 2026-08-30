package org.dreambot.discordwebhook.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

@Getter @Setter
@Accessors(chain = true)
public class DiscordEmbed {
    @SerializedName("title")
    private String title = "embed title";
    @SerializedName("type")
    private String type = "rich";
    @SerializedName("description")
    private String description = "embed desc.";
    @SerializedName("color")
    private int color = 1127128;
    @SerializedName("fields")
    private DiscordEmbedField[] fields;
    @SerializedName("image")
    DiscordImage image;

    public void setImage(BufferedImage image) {
    }

    public DiscordEmbed() {
    }

    public DiscordEmbed setFields(DiscordEmbedField... fields) {
        this.fields = fields;
        return this;
    }
}
