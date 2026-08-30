package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ClaySettings {
    @SerializedName("stopAfterUnrestricted")
    @UIExplanation("Stops the script when you have trade unrestricted < 50k coins and no loot to sell")
    public boolean stage = false;
    @SerializedName("ignorePlaytime")
    @UIExplanation("Stops the script when you have 100 ttl 10qp.")
    public boolean ignorePlaytime = false;

    @SerializedName("useCraftingGuild")
    @UIExplanation("Gets 40 Crafting to access the crafting guild")
    public boolean craftingGuild = false;

    @SerializedName("allAreas")
    @UIExplanation("Uses not just he edgeville mine but randomly assigns multiple clay location")
    public boolean allAreas = false;

    @SerializedName("singleRocks")
    @UIExplanation("Only targets 1 single rock per account")
    public boolean singleRock = false;

    @SerializedName("competitionLimit")
    @UIExplanation("how many people can be next to you before you hop")
    public int compLimit = 10;
}