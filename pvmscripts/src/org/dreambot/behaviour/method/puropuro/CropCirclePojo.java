package org.dreambot.behaviour.method.puropuro;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

/**
 * PLAIN OLD JAVA OBJECT BABY
 * model class for the crop circle api
 */
@ToString
public class CropCirclePojo {
    @SerializedName("location")
    CropCircleLocation location;
    @SerializedName("dateSeen")
    String dateSeen; // a js date object as a string.
}
