package org.dreambot.muling.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
abstract public class AbstractMessage {
    @SerializedName("type")
    public final MessageType type;

    public JsonElement toJson() {
        return (new Gson()).toJsonTree(this);
    }
}

