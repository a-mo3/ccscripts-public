package org.dreambot.behaviour.tutorial;

import com.google.gson.annotations.SerializedName;

public class TutorialBranchSettings {
    @SerializedName("nameStrategy")
    TutorialNameStrategy nameStrategy = TutorialNameStrategy.CCSCRIPTS_API;
//    @SerializedName("customEndpoint")
//    @UIExplanation("A custom endpoint for an API that will return your 200, the name as the entire body to a GET, only used on custom name strategy")
//    String customEndpoint = "http://localhost:8080/name";
}
