package org.dreambot.behaviour;


import org.dreambot.api.Client;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.fractals.loadout.InventoryLoadout;
import org.dreambot.fractals.loadout.InventoryLoadoutItem;
import org.dreambot.settings.script.ScriptSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MixedChopping extends Fractal {
    public static final Supplier<Integer> getAppropriateAxe = () -> {
        int lvl = Skills.getRealLevel(Skill.WOODCUTTING);
        if (lvl >= 61 && Client.isMembers()) return ItemID.DRAGON_AXE;
        if (lvl >= 41) return ItemID.RUNE_AXE;
        if (lvl >= 21) return ItemID.MITHRIL_AXE;
        return ItemID.BRONZE_AXE;
    };

    public static final InventoryLoadout AXE_LOADOUT = new InventoryLoadout()
            .setStrict(true)
            .addItem(new InventoryLoadoutItem(getAppropriateAxe, 1, 1))
            .strictIgnore(ItemID.LOGS, ItemID.OAK_LOGS, ItemID.MAGIC_LOGS, ItemID.YEW_LOGS, ItemID.REDWOOD_LOGS);

    Filter<GameObject> oaks = x -> x.getName().equals("Tree")
            || x.getName().equals("Evergreen tree")
            || x.getName().equals("Oak tree");
    Filter<GameObject> yews = x -> x.getName().equals("Yew tree");

    List<Area> treeAreas = of(
//            new Area(2988, 3263, 3007, 3246), // west port sarim collection of trees
            new Area(3033, 3276, 3044, 3259), // northen port sarim
//            new Area(3121, 3219, 3140, 3207), // north wizards tower
            new Area(3266, 3343, 3281, 3335) // south varrock sheep pen, near quest start
    );

    List<Area> oakAreas = of(
//            new Area(3022, 3280, 3047, 3267), // north of port sarim
            new Area(3189, 3465, 3196, 3457), // palace 3 oaks
            new Area(3276, 3434, 3285, 3411), // varrock west
            new Area(3098, 3255, 3108, 3240), // draynor east of bank
            new Area(2995, 3369, 3007, 3354), // falador east bank
            new Area(2944, 3426, 2958, 3398) // falador north of west bank
    );

    List<Area> yewAreas = of(
            new Area(2928, 3236, 2940, 3224), // rimmington yews
            new Area(new Tile(3022, 3322, 0),
                    new Tile(3024, 3315, 0),
                    new Tile(3013, 3312, 0),
                    new Tile(3000, 3303, 0),
                    new Tile(2992, 3313, 0),
                    new Tile(3007, 3320, 0)), // south of falador yews and oaks
            new Area(3047, 3275, 3060, 3264), // port sarim yews and oaks
            new Area(3085, 3482, 3089, 3468), // classic edgeville yews
            new Area(
                    new Tile(3200, 3504, 0),
                    new Tile(3204, 3507, 0),
                    new Tile(3225, 3506, 0),
                    new Tile(3224, 3499, 0),
                    new Tile(3208, 3498, 0)
            )// close to GE yews
    );

    public MixedChopping(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        int playerNameValue = ShuffleFractal.getLoginValue();
        Area treeArea = treeAreas.get(playerNameValue % treeAreas.size());
        Area oakArea = oakAreas.get(playerNameValue % oakAreas.size());
        Area yewArea = yewAreas.get(playerNameValue % yewAreas.size());
        addChildren(
                new GenericChopLeaf(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 15, treeArea,
                        x -> x.getName().equals("Tree") || x.getName().equals("Evergreen tree"))
                        .setBankLogs(ScriptSettings.getSettingsData().bankLogs)
                        .setHopCondition(() -> treeArea.contains(Players.getLocal()) && Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().logsCompetitionThreshold)
                        .setInventoryLoadout(AXE_LOADOUT)
                        .setSimpleName("Trees"),

                new GenericChopLeaf(() -> Skills.getRealLevel(Skill.WOODCUTTING) < 60 || !ScriptSettings.getSettingsData().isChopYews(), oakArea, oaks)
                        .setBankLogs(ScriptSettings.getSettingsData().bankOak)
                        .setHopCondition(() -> oakArea.contains(Players.getLocal()) && Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().oaksCompetitionThreshold)
                        .setInventoryLoadout(AXE_LOADOUT)
                        .setSimpleName("Oaks"),
                // yews
                new GenericChopLeaf(() -> true, yewArea, yews)
                        .setBankLogs(ScriptSettings.getSettingsData().bankLogs)
                        .setHopCondition(() -> yewArea.contains(Players.getLocal()) && Players.all(x -> x.distance() < 4).size() > ScriptSettings.getSettingsData().yewsCompetitionThreshold)
                        .setInventoryLoadout(AXE_LOADOUT)
                        .setSimpleName("Yews")
        );
    }

    private ArrayList<Area> of(Area... items) {
        return new ArrayList<>(Arrays.asList(items));
    }
}
