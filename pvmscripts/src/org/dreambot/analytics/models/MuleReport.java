package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;

public class MuleReport {
    @SerializedName("moneyIn")
    int moneyIn;
    @SerializedName("moneyOut")
    int moneyOut;
}
