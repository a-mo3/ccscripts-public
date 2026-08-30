package org.dreambot.discordwebhook.pojo;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter
@Accessors(chain = true)
public class DiscordEmbedField {
    @SerializedName("name")
    private String name = "field name";
    @SerializedName("value")
    private String value = "value";
    @SerializedName("inline")
    private boolean inline = true;

    public DiscordEmbedField(String name, String value, boolean inline) {
        this.name = name;
        this.value = value;
        this.inline = inline;
    }

    public DiscordEmbedField() {
    }

    public DiscordEmbedField setInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    public DiscordEmbedField setValue(String value) {
        this.value = value;
        return this;
    }

    public DiscordEmbedField setName(String name) {
        this.name = name;
        return this;
    }
}
