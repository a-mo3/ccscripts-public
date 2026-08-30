package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dreambot.BrutalBlues;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.settings.script.SettingsData;
import org.dreambot.settings.timing.ReactionSettings;

@Data @Accessors(chain = true)
public class HeartBeat {
    @SerializedName("account_hash")
    public String accountHash;
    @SerializedName("dreambot_user")
    public String dreambotUser;
    @SerializedName("script_name")
    public String scriptName;
    @SerializedName("minutes")
    public int minutes;
    @SerializedName("no_click_walk")
    public String noClickWalk;
    @SerializedName("menu_manip")
    public String menuManip;
    @SerializedName("active_fractal")
    public String activeFractal;
    @SerializedName("is_breaking")
    public String breaking;
    @SerializedName("is_member")
    public String isMember;
    @SerializedName("is_covert")
    public String isCovert;
    @SerializedName("skills")
    public AnalyticSkills skills;
    @SerializedName("reaction_times")
    public ReactionSettings reactionSettings;
    @SerializedName("script_settings")
    public SettingsData scriptSettings;
    @SerializedName("region")
    public int region;
    @SerializedName("world")
    public int world = Worlds.getCurrentWorld();
    @SerializedName("experience")
    public AnalyticSkillExperience exp = new AnalyticSkillExperience();
    @SerializedName("deathCount")
    public int deathCount = BrutalBlues.deathCount;
}
