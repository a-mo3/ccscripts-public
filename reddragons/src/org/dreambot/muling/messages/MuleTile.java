package org.dreambot.muling.messages;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class MuleTile {
    @SerializedName("x")
    public int x;
    @SerializedName("y")
    public int y;
    @SerializedName("z")
    public int z;
}
