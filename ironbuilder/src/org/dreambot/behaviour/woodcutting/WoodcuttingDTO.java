package org.dreambot.behaviour.woodcutting;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;

@Accessors(chain = true)
@Setter
public class WoodcuttingDTO extends FractalDTO {
    @SerializedName("levelTarget")
    public int level = 75;

    @SerializedName("banklogs")
    public boolean bank;

    @SerializedName("treeType")
    @UIOptionCategory
    public TreeType treeType = TreeType.NORMAL;

    @SerializedName("treeLocation")
    @RequiredCategory("NORMAL")
    NormalTreeLocation[] normalTreeLocations = NormalTreeLocation.values();

    @SerializedName("willowLocation")
    @RequiredCategory("WILLOW")
    WillowTreeLocation[] willowTreeLocations = WillowTreeLocation.values();

    @SerializedName("yewLocation")
    @RequiredCategory("YEW")
    YewTreeLocation[] yewTreeLocations = YewTreeLocation.values();

    @Override
    public FractalDTO getInstance() {
        return new WoodcuttingDTO();
    }

    private Area getLocation() {
        switch (treeType) {
            case YEW:
                return yewTreeLocations[Calculations.random(yewTreeLocations.length)].treeLocation;
            case WILLOW:
                return willowTreeLocations[Calculations.random(willowTreeLocations.length)].treeLocation;
            case NORMAL:
                return normalTreeLocations[Calculations.random(normalTreeLocations.length)].treeLocation;
        }
        Logger.info("Unexpected case " + treeType);
        return null;
    }

    @Override
    public WoodcutFractal toFractal() {
        Area a = getLocation();
        WoodcutFractal f = new WoodcutFractal(() -> Skill.WOODCUTTING.getLevel() < level,
                x -> a.contains(x) && treeType.treeFilter.match(x), a, bank);
        f.setSimpleName(name());
        return f;
    }

    @Override
    public String name() {
        return "Chop " + treeType + " until lvl " + level + " Banking: " + bank;
    }
}
