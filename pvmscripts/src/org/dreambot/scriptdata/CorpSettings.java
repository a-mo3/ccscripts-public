package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.world.Location;
import org.dreambot.behaviour.method.corp.CorpLoadout;
import org.dreambot.behaviour.method.undeaddruids.UndeadDruidLoadout;
import org.dreambot.settings.WrappedLocation;
import org.dreambot.settings.ui.nui.UIExplanation;

public class CorpSettings {
    @SerializedName("loadout")
    public CorpLoadout loadout = CorpLoadout.MELEE_OBY;
    @SerializedName("shouldFlick")
    @UIExplanation("If the script should flick prayers when killing druids")
    public boolean shouldFlick = true;

    @SerializedName("teamSize")
    @UIExplanation("This is how many accounts will team up to kill corp, eg teamSize of 5, running 23 accounts =  4 teams of 5 & 1 team of 3")
    public int teamSize = 5;
    @SerializedName("specersPerTeam")
    @UIExplanation("This is how many accounts will be using spec weapons on corp (expensive!)")
    public int specialForcesCount = 3;
    @SerializedName("worldLocation")
    @UIExplanation("Teams will be assigned worlds in this region, keep it close to your server and proxy for low ping")
    public WrappedLocation worldPreference = WrappedLocation.GERMANY;


    @SerializedName("prayerTarget")
    public int prayerTarget = 37;
    // todo ranged is slightly better than melee (with cheap melee gear) and quicker to train, should be used for non spec accounts

    @SerializedName("rangeTarget")
    public int rangeTarget = 0;
    @SerializedName("attackTarget")
    public int attackTarget = 75;

    // todo
//    @SerializedName("useCannon")
//    public boolean useCannon;

}