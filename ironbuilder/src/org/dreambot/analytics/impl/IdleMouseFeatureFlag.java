package org.dreambot.analytics.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdleMouseFeatureFlag {
    @SerializedName("flagA")
    final boolean flagA = true; // mouse off screen
    @SerializedName("flagB")
    final boolean flagB = true; // hover random entities
    @SerializedName("flagC")
    final boolean flagC = true; // camera movements
    @SerializedName("flagD")
    final boolean flagD = false; // completely randomly move mouse around the screen every time, no matter what
    @SerializedName("flagE")
    final boolean flagE = true;
}
