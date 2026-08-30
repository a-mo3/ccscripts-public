package org.dreambot.behaviour.fishing;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.UIExplanation;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;
import org.dreambot.loadouts.data.ShopLocation;

@Setter
@Accessors(chain = true)
public class FishingDTO extends FractalDTO {
    @SerializedName("levelTarget")
    public int levelTaget = 99;

    @SerializedName("bankFish")
    @UIExplanation("Banks all the fish when you have a full inventory")
    public boolean bankAll;

    @SerializedName("sellFish")
    @UIExplanation("Goes to port sarim to sell your fish")
    public boolean sellAll;

    @SerializedName("mode")
    @UIOptionCategory
    public FishingMode mode = FishingMode.SMALL_NET;

    @SerializedName("smallNetLocation")
    @RequiredCategory("SMALL_NET")
    public SmallNetLocation[] smallNetLocations = SmallNetLocation.values();

    @SerializedName("baitLocation")
    @RequiredCategory("BAIT")
    public BaitLocation[] baitLocations = BaitLocation.values();

    @SerializedName("flyLocation")
    @RequiredCategory("FLY")
    public FlyFishLocation[] flyLocations = FlyFishLocation.values();

    @Override
    public FractalDTO getInstance() {
        return new FishingDTO();
    }

    int areaIndex = -1;

    private int getAreaIndex() {
        if (areaIndex >= 0) return areaIndex;
        switch (mode) {
            case SMALL_NET:
                return areaIndex = Calculations.random(smallNetLocations.length);
            case BAIT:
                return areaIndex = Calculations.random(baitLocations.length);
            case FLY:
                return areaIndex = Calculations.random(flyLocations.length);
            case CAGE:
            default:
                return areaIndex;
        }
    }


    private Area getFishingArea() {
        switch (mode) {
            case SMALL_NET:
                return smallNetLocations[getAreaIndex()].location;
            case BAIT:
                return baitLocations[getAreaIndex()].location;
            case FLY:
                return flyLocations[getAreaIndex()].location;
            case CAGE:
            default:
                Logger.info("Invalid mode " + mode);
                return null;
        }
    }

    // its Net at Lum and Small net in other places
    private String getFishingAction() {
        switch (mode) {
            case SMALL_NET:
                return smallNetLocations[getAreaIndex()].action;
            case BAIT:
                return baitLocations[getAreaIndex()].action;
            case CAGE:
            default:
                Logger.info("Invalid mode " + mode);
                return null;
        }
    }

    @Override
    public IronFractal toFractal() {
        return new FishingFractal.Builder()
                .setAcceptCondition(() -> Skill.FISHING.getLevel() < levelTaget)
                .setLocation(getFishingArea())
                .setSpotFilter(mode.spotFilter)
                .setAction(getFishingAction())
                .setBankAll(bankAll)
                .setSellAll(sellAll)
                .setStore(ShopLocation.GERRANTS_FISHY_BUSINESS)
//                .cookingArea()
                .setLoadout(mode.loadout.get())
                .build()
                .setSimpleName(name());
    }

    @Override
    public String name() {
        return mode + " fishin until lvl " + levelTaget;
    }
}
