package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.behaviour.method.scorpia.ScorpiaLoadout;
import org.dreambot.behaviour.training.agility.wild.BHAgressionMode;
import org.dreambot.behaviour.training.agility.wild.RagLoadout;
import org.dreambot.behaviour.training.agility.wild.WildernessAgilityMode;
import org.dreambot.settings.ui.nui.UIExplanation;

public class WildernessAgilitySettings {
    @SerializedName("rangeTarget")
    public int rangeTarget = 60;
    @SerializedName("exitLootValue")
    public int exitLootValue = 300_000;

    @SerializedName("antiPkStrategy")
    public WildernessAgilityMode mode = WildernessAgilityMode.SUICIDE;

    @SerializedName("payFee")
    @UIExplanation("Untick to do the course for xp only")
    public boolean payFee = true;

    @SerializedName("forceWorld")
    public int world = -1;

    @SerializedName("clanChat")
    public String clanChat = "";

    @SerializedName("bhAgressionMode")
    public BHAgressionMode bhAgressionMode = BHAgressionMode.PEACFUL;
    @SerializedName("ragLoadout")
    public RagLoadout ragLoadout = RagLoadout.RAG_LOADOUT;
}
