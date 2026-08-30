package org.dreambot.webintegration.loadoutmodel;

import com.google.gson.annotations.SerializedName;

public class WebEquipmentLoadout {
    @SerializedName("hat")
    public WebEquipmentLoadoutItem[] hat;

    @SerializedName("ammo")
    public WebEquipmentLoadoutItem[] ammo;
    @SerializedName("chest")
    public WebEquipmentLoadoutItem[] body;
    @SerializedName("cape")
    public WebEquipmentLoadoutItem[] cape;
    @SerializedName("feet")
    public WebEquipmentLoadoutItem[] feet;
    @SerializedName("hands")
    public WebEquipmentLoadoutItem[] hands;

    @SerializedName("legs")
    public WebEquipmentLoadoutItem[] legs;
    @SerializedName("neck")
    public WebEquipmentLoadoutItem[] neck;
    @SerializedName("ring")
    public WebEquipmentLoadoutItem[] ring;
    @SerializedName("shield")
    public WebEquipmentLoadoutItem[] shield;
    @SerializedName("weapon")
    public WebEquipmentLoadoutItem[] weapon;
}
