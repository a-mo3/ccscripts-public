package org.dreambot.webintegration.loadoutmodel;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
public class WebEquipmentLoadoutItem {
    @SerializedName("min")
    public Integer min = 1;
    @SerializedName("max")
    public Integer max = 1;
    @SerializedName("refill")
    public Integer refill = 1;

    @SerializedName("tradable")
    public Boolean tradable = true;

    @SerializedName("id")
    public Integer id = -1;
    @SerializedName("name")
    public String name = "";
//    @SerializedName("icon")
//    public String icon = "";

    @SerializedName("reqs")
    public List<Map<String, Integer>> requirements;
}
