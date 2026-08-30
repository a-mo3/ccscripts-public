package org.dreambot.analytics.models;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.Unobfuscated;

@Data @Unobfuscated // too many users getting way past 99
public class AnalyticSkillExperience {
    @SerializedName("attack")
    public int attack = Skills.getExperience(Skill.ATTACK);
    @SerializedName("hitpoints")
    public int hitpoints = Skills.getExperience(Skill.HITPOINTS);
    @SerializedName("mining")
    public int mining = Skills.getExperience(Skill.MINING);
    @SerializedName("strength")
    public int strength = Skills.getExperience(Skill.STRENGTH);
    @SerializedName("agility")
    public int agility = Skills.getExperience(Skill.AGILITY);
    @SerializedName("smithing")
    public int smithing = Skills.getExperience(Skill.SMITHING);
    @SerializedName("defence")
    public int defence = Skills.getExperience(Skill.DEFENCE);
    @SerializedName("herblore")
    public int herblore = Skills.getExperience(Skill.HERBLORE);
    @SerializedName("fishing")
    public int fishing = Skills.getExperience(Skill.FISHING);
    @SerializedName("ranged")
    public int ranged = Skills.getExperience(Skill.RANGED);
    @SerializedName("thieving")
    public int thieving = Skills.getExperience(Skill.THIEVING);
    @SerializedName("cooking")
    public int cooking = Skills.getExperience(Skill.COOKING);
    @SerializedName("prayer")
    public int prayer = Skills.getExperience(Skill.PRAYER);
    @SerializedName("crafting")
    public int crafting = Skills.getExperience(Skill.CRAFTING);
    @SerializedName("firemaking")
    public int firemaking = Skills.getExperience(Skill.FIREMAKING);
    @SerializedName("magic")
    public int magic = Skills.getExperience(Skill.MAGIC);
    @SerializedName("fletching")
    public int fletching = Skills.getExperience(Skill.FLETCHING);
    @SerializedName("woodcutting")
    public int woodcutting = Skills.getExperience(Skill.WOODCUTTING);
    @SerializedName("runecrafting")
    public int runecrafting = Skills.getExperience(Skill.RUNECRAFTING);
    @SerializedName("slayer")
    public int slayer = Skills.getExperience(Skill.SLAYER);
    @SerializedName("farming")
    public int farming = Skills.getExperience(Skill.FARMING);
    @SerializedName("construction")
    public int construction = Skills.getExperience(Skill.CONSTRUCTION);
    @SerializedName("hunter")
    public int hunter = Skills.getExperience(Skill.HUNTER);
}
