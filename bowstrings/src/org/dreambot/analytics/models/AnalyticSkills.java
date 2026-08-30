package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Unobfuscated;

@Data @Unobfuscated
public class AnalyticSkills {
    @SerializedName("attack")
    public int attack = Skills.getRealLevel(Skill.ATTACK);
    @SerializedName("hitpoints")
    public int hitpoints = Skills.getRealLevel(Skill.HITPOINTS);
    @SerializedName("mining")
    public int mining = Skills.getRealLevel(Skill.MINING);
    @SerializedName("strength")
    public int strength = Skills.getRealLevel(Skill.STRENGTH);
    @SerializedName("agility")
    public int agility = Skills.getRealLevel(Skill.AGILITY);
    @SerializedName("smithing")
    public int smithing = Skills.getRealLevel(Skill.SMITHING);
    @SerializedName("defence")
    public int defence = Skills.getRealLevel(Skill.DEFENCE);
    @SerializedName("herblore")
    public int herblore = Skills.getRealLevel(Skill.HERBLORE);
    @SerializedName("fishing")
    public int fishing = Skills.getRealLevel(Skill.FISHING);
    @SerializedName("ranged")
    public int ranged = Skills.getRealLevel(Skill.RANGED);
    @SerializedName("thieving")
    public int thieving = Skills.getRealLevel(Skill.THIEVING);
    @SerializedName("cooking")
    public int cooking = Skills.getRealLevel(Skill.COOKING);
    @SerializedName("prayer")
    public int prayer = Skills.getRealLevel(Skill.PRAYER);
    @SerializedName("crafting")
    public int crafting = Skills.getRealLevel(Skill.CRAFTING);
    @SerializedName("firemaking")
    public int firemaking = Skills.getRealLevel(Skill.FIREMAKING);
    @SerializedName("magic")
    public int magic = Skills.getRealLevel(Skill.MAGIC);
    @SerializedName("fletching")
    public int fletching = Skills.getRealLevel(Skill.FLETCHING);
    @SerializedName("woodcutting")
    public int woodcutting = Skills.getRealLevel(Skill.WOODCUTTING);
    @SerializedName("runecrafting")
    public int runecrafting = Skills.getRealLevel(Skill.RUNECRAFTING);
    @SerializedName("slayer")
    public int slayer = Skills.getRealLevel(Skill.SLAYER);
    @SerializedName("farming")
    public int farming = Skills.getRealLevel(Skill.FARMING);
    @SerializedName("construction")
    public int construction = Skills.getRealLevel(Skill.CONSTRUCTION);
    @SerializedName("hunter")
    public int hunter = Skills.getRealLevel(Skill.HUNTER);
}
