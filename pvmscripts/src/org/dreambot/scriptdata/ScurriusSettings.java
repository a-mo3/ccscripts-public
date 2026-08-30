package org.dreambot.scriptdata;

import com.google.gson.annotations.SerializedName;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.settings.ui.nui.UIExplanation;

public class ScurriusSettings {
    // todo settings for pre lvl 50 training, perhaps in ftp

    @SerializedName("doWitchesHouse")
    public boolean doWitchesHouse = false;
    @SerializedName("enableFightArena")
    public boolean fightArena = false;
    @SerializedName("enableVampyreSlayer")
    public boolean vampyreSlayer = false;
    @SerializedName("prayerTarget")
    public int prayerTarget = 44;

    @SerializedName("attackTarget")
    public int attackTarget = 99;
    @SerializedName("strengthTarget")
    public int strengthTarget = 99;
    @SerializedName("defenceTarget")
    public int defenceTarget = 99;

    @SerializedName("magicDefenceTarget")
    public int magicDefenceTarget = 0;
    @SerializedName("magicTarget")
    public int magicTarget = 0;

    @SerializedName("rangeDefenceTarget")
    public int rangeDefenceTarget = 0;
    @SerializedName("rangeTarget")
    public int rangeTarget = 0;

    // todo spend spine xp on
    @SerializedName("redeemSpinesOn")
    public Skill spineLampSkill = Skill.PRAYER;

    @SerializedName("dropFoodForLoot")
    public boolean dropFoodForLoot = false;

    @SerializedName("useFlicking")
    @UIExplanation("1T flicks to conserve prayer when using a method that supports it")
    public boolean flicking = true;

}
