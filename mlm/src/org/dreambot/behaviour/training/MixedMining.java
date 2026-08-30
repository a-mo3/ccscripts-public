package org.dreambot.behaviour.training;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.settings.script.ScriptSettings;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MixedMining extends Fractal {
    final List<Area> COAL_MINES = Arrays.asList(
            new Area(3143, 3155, 3150, 3143), // west lummy swamp
            new Area(3399, 3173, 3405, 3167), // new spot in al kharid forget the name
            new Area(3078, 3424, 3084, 3417) // barb village
    );

    final List<Area> IRON_MINES = Arrays.asList(
            new Area(3399, 3172, 3405, 3167),  // new spot in al kharid
            new Area(2980, 3241, 2987, 3232), // rimmington west side
            new Area(2966, 3244, 2972, 3235) // rimmington east side
    );

    final List<Area> COPPER_MINES = Arrays.asList(
            new Area(3226, 3149, 3232, 3143), // lumbridge swamp
            new Area(3281, 3370, 3284, 3367), // north corner west varrock
            new Area(3285, 3365, 3290, 3360), // south corner west varrock
            new Area(2974, 3250, 2981, 3244) // rimmington copper
    );

    public MixedMining(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        int playerHopCount = 3;
        int userNameSeed = ShuffleFractal.getLoginValue();
        Area coalMine = COAL_MINES.get(userNameSeed % COAL_MINES.size());
        Area ironMine = IRON_MINES.get(userNameSeed % IRON_MINES.size());
        Area copperMine = COPPER_MINES.get(userNameSeed % COPPER_MINES.size());
        setSimpleName("MixedMining");
        addChildren(
                new GenericMineLeaf(() -> Skills.getRealLevel(Skill.MINING) < 15, "Copper rocks", copperMine)
                        .setHopCondition(() -> copperMine.contains(Players.getLocal()) && Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().getCopperCompetitionMax())
                        .setWorldSupplier(() -> Worlds.getRandomWorld(x -> x.isNormal() && (Client.isMembers() != x.isF2P()) && x.getMinimumLevel() < Skills.getTotalLevel()))
                        .setSimpleName("Copper mining"),

                new GenericMineLeaf(() -> Skills.getRealLevel(Skill.MINING) < 30, "Iron rocks", ironMine)
                        .setShouldBank(false)
                        .setWorldSupplier(() -> Worlds.getRandomWorld(x -> x.isNormal() && (Client.isMembers() != x.isF2P()) && x.getMinimumLevel() < Skills.getTotalLevel()))
                        .setHopCondition(() -> Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().getIronCompetitionMax()
                                && ironMine.contains(Players.getLocal()))
                        .setSimpleName("Iron mining"),

                new GenericMineLeaf(() -> true, "Coal rocks", coalMine)
                        .setShouldBank(false)
                        .setHopCondition(() -> Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().getCoalCompetitionMax()
                                && coalMine.contains(Players.getLocal()))
                        .setWorldSupplier(() -> Worlds.getRandomWorld(x -> x.isNormal() && (Client.isMembers() != x.isF2P()) && x.getMinimumLevel() < Skills.getTotalLevel()))
                        .setSimpleName("Coal mining")
        );
    }
}
