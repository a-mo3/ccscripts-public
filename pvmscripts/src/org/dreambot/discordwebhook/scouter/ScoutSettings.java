package org.dreambot.discordwebhook.scouter;

import com.google.gson.annotations.SerializedName;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ScoutSettings {
    @SerializedName("generalWebhook")
    @UIExplanation("Sends all scouts of all players to this webhook")
    String generalWebhook;

    @SerializedName("skulledWebhook")
    @UIExplanation("Sends all skulled people")
    String skulledWebhook;

    @SerializedName("plusOneWebhook")
    @UIExplanation("Sends players with a plus one above a certain")
    String plusOneWebhook;

    @SerializedName("plusOneValue")
    @UIExplanation("The value of a single item we seperate it from skulled")
    int plusOneValue = 5_000_000;

    @SerializedName("screenshotOnGeneral")
    boolean screenshotOnGeneral = true;
    @SerializedName("screenshotOnSkulled")
    boolean screenshotOnSkulled = true;
    @SerializedName("screenshotOnPlusOne")
    boolean screenshotOnPlusOne= true;
}
