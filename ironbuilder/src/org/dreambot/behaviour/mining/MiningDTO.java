package org.dreambot.behaviour.mining;

import com.google.gson.annotations.SerializedName;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.option.RequiredCategory;
import org.dreambot.gui.option.UIOptionCategory;
import org.dreambot.loadouts.data.ItemID;

@Setter
@Accessors(chain = true)
public class MiningDTO extends FractalDTO {
    @SerializedName("level")
    public int level = 1;
    @SerializedName("miningMode")
    @UIOptionCategory
    public MiningMode mode = MiningMode.CLAY;

    @SerializedName("bankOre")
    public boolean bankOre = true;

    @SerializedName("clayLocations")
    @RequiredCategory("CLAY")
    public ClayLocation[] clayLocations = ClayLocation.values();

    @SerializedName("bronzeLocations")
    @RequiredCategory("BRONZE")
    public BronzeLocation[] bronzeLocations = BronzeLocation.values();

    @SerializedName("tinLocations")
    @RequiredCategory("TIN")
    public TinLocation[] tinLocations = TinLocation.values();

    @SerializedName("copperLocations")
    @RequiredCategory("COPPER")
    public CopperLocation[] copperLocations = CopperLocation.values();

    @SerializedName("coalLocations")
    @RequiredCategory("COAL")
    public CoalLocation[] coalLocations = CoalLocation.values();

    @SerializedName("ironLocations")
    @RequiredCategory("IRON")
    public IronLocation[] ironLocations = IronLocation.values();

    @SerializedName("silverLocation")
    @RequiredCategory("SILVER")
    public SilverLocation[] silverLocations = SilverLocation.values();

    @SerializedName("goldLocations")
    @RequiredCategory("GOLD")
    public GoldLocation[] goldLocations = GoldLocation.values();

    @SerializedName("mithrilLocations")
    @RequiredCategory("MITHRIL")
    public MithrilLocation[] mithrilLocations = MithrilLocation.values();

    @SerializedName("adamentLocations")
    @RequiredCategory("ADAMANTITE")
    public AdamantLocation[] adamantiteLocations = AdamantLocation.values();

//    @SerializedName("runiteLocations")
//    @RequiredCategory("RUNITE")
//    public [] adamantiteLocations = AdamantLocation.values();


    @Override
    public FractalDTO getInstance() {
        return new MiningDTO();
    }

    private MiningLocation loc() {

        switch (mode) {
            case CLAY:
                return clayLocations[Calculations.random(clayLocations.length)];
            case COPPER:
                return copperLocations[Calculations.random(copperLocations.length)];
            case TIN:
                return tinLocations[Calculations.random(tinLocations.length)];
            case BRONZE:
                return bronzeLocations[Calculations.random(bronzeLocations.length)];
            case COAL:
                return coalLocations[Calculations.random(coalLocations.length)];
            case IRON:
                return ironLocations[Calculations.random(ironLocations.length)];
            case SILVER:
                return silverLocations[Calculations.random(silverLocations.length)];
            case GOLD:
                return goldLocations[Calculations.random(goldLocations.length)];
            case ADAMANTITE:
                return adamantiteLocations[Calculations.random(adamantiteLocations.length)];
            case MITHRIL:
                return mithrilLocations[Calculations.random(mithrilLocations.length)];
            case RUNITE:
        }
        return null;
    }

    @Override
    public IronFractal toFractal() {
        // we allow an empty array of locations here to just throw an exception, idc.
        switch (mode) {
            /*
            you can sell ores to drogos mining emporium, that might be a nice option for honest to god ironmen
            but its like 1/10th the GE price so im not sure it'd be used if i did it rn.
             */

            case CLAY:
                ClayLocation cl = clayLocations[Calculations.random(clayLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Clay rocks".equals(x.getName()),
                        cl.location.getArea(1), bankOre)
                        .setSimpleName(name() + cl);
            case COPPER:
                CopperLocation copperLoc = copperLocations[Calculations.random(copperLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Copper rocks".equals(x.getName()),
                        copperLoc.location, bankOre)
                        .setSimpleName(name() + " " + copperLoc);
            case TIN:
                TinLocation tinLoc = tinLocations[Calculations.random(tinLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Tin rocks".equals(x.getName()),
                        tinLoc.location, bankOre)
                        .setSimpleName(name() + " " + tinLoc);
            case BRONZE:
                BronzeLocation bronzeLocation = bronzeLocations[Calculations.random(bronzeLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        // collect 14 tin 14 copper for bronze farming
                        x -> Inventory.count(ItemID.TIN_ORE) < 14 ? "Tin rocks".equals(x.getName()) : "Copper rocks".equals(x.getName()),
                        bronzeLocation.location, bankOre)
                        .setSimpleName(name() + " " + bronzeLocation);
            case COAL:
                CoalLocation coalLocation = coalLocations[Calculations.random(coalLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Coal rocks".equals(x.getName()),
                        coalLocation.location, bankOre)
                        .setSimpleName(name() + " " + coalLocation);
            case IRON:
                IronLocation ironLocation = ironLocations[Calculations.random(ironLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Iron rocks".equals(x.getName()),
                        ironLocation.location, bankOre)
                        .setSimpleName(name() + " " + ironLocation);
            case SILVER:
                SilverLocation silverLocation = silverLocations[Calculations.random(silverLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Silver rocks".equals(x.getName()),
                        silverLocation.location, bankOre)
                        .setSimpleName(name() + " " + silverLocation);

            case GOLD:
                GoldLocation goldLocation = goldLocations[Calculations.random(goldLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Gold rocks".equals(x.getName()),
                        goldLocation.location, bankOre)
                        .setSimpleName(name() + " " + goldLocation);
            case ADAMANTITE:
                AdamantLocation adaLocation = adamantiteLocations[Calculations.random(adamantiteLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Adamantite rocks".equals(x.getName()),
                        adaLocation.location, bankOre)
                        .setSimpleName(name() + " " + adaLocation);
            case MITHRIL:
                MithrilLocation mLocation = mithrilLocations[Calculations.random(mithrilLocations.length)];
                return new MiningFractal(() -> Skill.MINING.getLevel() < level,
                        x -> "Adamantite rocks".equals(x.getName()),
                        mLocation.location, bankOre)
                        .setSimpleName(name() + " " + mLocation);
            case RUNITE:
        }
        return new MiningFractal(() -> true, x -> false, new Area(0, 0, 0, 0, 0), false)
                .setSimpleName("SHOULD NOT HAPPEN - " + mode);
    }

    @Override
    public String name() {
        return "Mining " + mode + " Until lvl " + level + " Bank? " + bankOre;
    }
}
