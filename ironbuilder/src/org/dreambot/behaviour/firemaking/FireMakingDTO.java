package org.dreambot.behaviour.firemaking;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.utilities.Logger;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.UIExplanation;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;
import org.dreambot.loadouts.InventoryLoadout;
import org.dreambot.loadouts.data.Items;

@Accessors(chain = true) @Setter
public class FireMakingDTO extends FractalDTO {

    // if i add wintertodt, make this a ui option category
    @SerializedName("firemakingMode")
    @UIOptionCategory
    public FireMakingMode mode = FireMakingMode.CHOP_BURN;

    @SerializedName("levelTarget")
    public int level = 1;

    @SerializedName("chopModeTree")
    @RequiredCategory("CHOP_BURN")
    public FiremakingLogType logType = FiremakingLogType.LOGS;

//    @SerializedName("useBonfire")
//    @UIExplanation("Uses bonfire instead of placing multiple fires, at the cost of less ashes")
//    public boolean useBonfire = true;
    // i removed bonfire option because bonfires drop all the ash, so theres really not any reason to not use it.

    @SerializedName("collectAshes")
    @UIExplanation("Collects ashes whenever you see them - Not safe for HCIM you can get lured")
    public boolean collectAshes = false;


    @Override
    public FractalDTO getInstance() {
        return new FireMakingDTO();
    }

    @Override
    public FireMakingFractal toFractal() {
        Logger.info("Make firemaking with level " + level);
        FireMakingFractal f = new FireMakingFractal(() -> Skill.FIREMAKING.getLevel() < level, mode, collectAshes, logType);
//        if (collectAshes) f.setSafeForHCIM(false);

        f.setSimpleName(name());
        return f;
    }

    @Override
    public String name() {
        if (mode == FireMakingMode.CHOP_BURN) {
            return mode + " " + logType + " until level " + level;
        }
        return mode + " " + " until level " + level;
    }
}
