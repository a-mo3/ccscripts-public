package org.dreambot.behaviour.misc;

import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MixedCombat extends Fractal {
    public static final Area COW_PEN_LUMMY = new Area(
            new Tile(3241, 3298, 0),
            new Tile(3263, 3298, 0),
            new Tile(3265, 3255, 0),
            new Tile(3253, 3255, 0),
            new Tile(3253, 3272, 0),
            new Tile(3249, 3278, 0),
            new Tile(3244, 3280, 0),
            new Tile(3240, 3286, 0),
            new Tile(3242, 3291, 0)
    );

    public static final Area LUMMY_GOBLINS = new Area(
            new Tile(3248, 3228, 0),
            new Tile(3254, 3228, 0),
            new Tile(3258, 3231, 0),
            new Tile(3256, 3244, 0),
            new Tile(3251, 3253, 0),
            new Tile(3240, 3252, 0),
            new Tile(3241, 3246, 0),
            new Tile(3240, 3239, 0)
    );

    public static final Area LUMMY_SWAMP = new Area(
            new Tile(3206, 3190, 0),
            new Tile(3220, 3196, 0),
            new Tile(3234, 3190, 0),
            new Tile(3235, 3176, 0),
            new Tile(3229, 3162, 0),
            new Tile(3215, 3162, 0),
            new Tile(3203, 3175, 0)
    );

    public static final Area VARROCK_COWS = new Area(3021, 3313, 3042, 3298);

    Integer[] foodIds = new Integer[]{
            ItemID.SHARK,
            ItemID.LOBSTER,
    };

    public MixedCombat(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);

        Supplier<NPC> varrockCowSupplier = () -> NPCs.closest(x -> x.getName().equals("Cow")
                && x.hasAction("Attack") && x.canReach() && VARROCK_COWS.contains(x));

        Supplier<NPC> lummyCowSupplier = () -> NPCs.closest(x -> x.getName().equals("Cow")
                && x.hasAction("Attack") && x.canReach() && COW_PEN_LUMMY.contains(x));

        Supplier<NPC> lummyGoblinSupplier = () -> NPCs.closest(x -> x.getName().contains("Goblin")
                && x.hasAction("Attack") && x.canReach() && LUMMY_GOBLINS.contains(x));

        List<String> swampMobs = new ArrayList<>(Arrays.asList(
                "Giant rat",
                "Frog",
                "Big Frog"
        ));

        Supplier<NPC> lummySwampSupplier = () -> NPCs.closest(x -> swampMobs.contains(x.getName())
                && x.hasAction("Attack") && x.canReach() && LUMMY_SWAMP.contains(x));

        Fractal[] combatOptions = new Fractal[]{
                new AdvStandardCombat(acceptCondition, VARROCK_COWS,
                        varrockCowSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Varrock cows"),

                new AdvStandardCombat(acceptCondition, COW_PEN_LUMMY,
                        lummyCowSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Lummy cows"),

                new AdvStandardCombat(acceptCondition, LUMMY_GOBLINS,
                        lummyGoblinSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Lummy goblins"),

                new AdvStandardCombat(acceptCondition, LUMMY_SWAMP,
                        lummySwampSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Lummy swamp"),
        };

        this.addChildren(
                combatOptions[ShuffleFractal.getLoginValue() % combatOptions.length]
        );
    }
}
