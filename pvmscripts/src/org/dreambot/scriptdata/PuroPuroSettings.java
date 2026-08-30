package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.dreambot.behaviour.method.puropuro.PuroMode;

@NoArgsConstructor @AllArgsConstructor
public class PuroPuroSettings {
    @SerializedName("useOverworldCropCircle")
    public boolean overworldCircles = true;
    @SerializedName("hunterTarget")
    public int hunterTarget = 50;
    @SerializedName("trainForSnare")
    public boolean trainForSnare = true;
    @SerializedName("puroMode")
    public PuroMode puroMode = PuroMode.RUN_AROUND;

    @SerializedName("babyImpling")
    public boolean babyImpling = true;
    @SerializedName("youngImpling")
    public boolean youngImpling = true;
    @SerializedName("gourmetImpling")
    public boolean gourmetImpling = true;
    @SerializedName("earthImpling")
    public boolean earthImpling = false;

    @SerializedName("essenceImpling")
    public boolean essenceImpling = true;
    @SerializedName("eclecticImpling")
    public boolean eclecticImpling = true;
    @SerializedName("natureImpling")
    public boolean natureImpling = true;
    @SerializedName("magpieImpling")
    public boolean magpieImpling = true;


//    @SerializedName("ninjaImpling")
//    public boolean Impling = true;
//    @SerializedName("Impling")
//    public boolean Impling = true;
//    @SerializedName("Impling")
//    public boolean Impling = true;
//    @SerializedName("Impling")
//    public boolean Impling = true;
}