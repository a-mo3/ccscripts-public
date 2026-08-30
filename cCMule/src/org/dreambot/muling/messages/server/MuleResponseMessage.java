package org.dreambot.muling.messages.server;


import com.google.gson.annotations.SerializedName;
import org.dreambot.muling.messages.AbstractMessage;
import org.dreambot.muling.messages.MessageType;
import org.dreambot.muling.messages.MuleTile;

public class MuleResponseMessage extends AbstractMessage {
    @SerializedName("success")
    public final boolean success;
    @SerializedName("errorMessage")
    public final String errorMessage;
    @SerializedName("world")
    public final int world;
    @SerializedName("location")
    public final MuleTile location;
    @SerializedName("mule")
    public final String muleName;
    @SerializedName("debug")
    public final String debugMessage;

    public MuleResponseMessage(boolean success, String errorMessage, int world, MuleTile location, String muleName, String debugMessage) {
        super(MessageType.MULE_RESPONSE);
        this.success = success;
        this.errorMessage = errorMessage;
        this.world = world;
        this.location = location;
        this.muleName = muleName;
        this.debugMessage = debugMessage;
    }
}
