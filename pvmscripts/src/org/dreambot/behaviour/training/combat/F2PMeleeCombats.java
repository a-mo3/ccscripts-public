package org.dreambot.behaviour.training.combat;

import org.dreambot.api.methods.combat.CombatStyle;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.behaviour.misc.CombatLoadouts;
import org.dreambot.behaviour.training.slayer.behaviour.StandardCombat;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.ShuffleFractal;
import org.dreambot.fractals.data.ItemID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class F2PMeleeCombats extends Fractal {
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

    public F2PMeleeCombats(Supplier<Boolean> acceptCondition) {
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
//                new StandardCombat(acceptCondition, VARROCK_COWS,
//                        varrockCowSupplier,
//                        foodIds)
//                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
//                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
//                        .setSimpleName("Varrock cows"),
//
//                new StandardCombat(acceptCondition, COW_PEN_LUMMY,
//                        lummyCowSupplier,
//                        foodIds)
//                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
//                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
//                        .setSimpleName("Lummy cows"),
//
//                new StandardCombat(acceptCondition, LUMMY_GOBLINS,
//                        lummyGoblinSupplier,
//                        foodIds)
//                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
//                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
//                        .setSimpleName("Lummy goblins"),
//
//                new StandardCombat(acceptCondition, LUMMY_SWAMP,
//                        lummySwampSupplier,
//                        foodIds)
//                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
//                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
//                        .setSimpleName("Lummy swamp"),

                new StandardCombat(acceptCondition,
                        new Area(3371, 3156, 3385, 3143),
                        () -> NPCs.closest("Hill Giant"),
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Apulz hill giant"),

                new StandardCombat(acceptCondition,
                        new Area(3282, 3177, 3286, 3167),
                        () -> NPCs.closest(x -> x.getName().equals("Al Kharid warrior") && x.canReach()),
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Apulz Kharid warriors"),

//                new StandardCombat(acceptCondition,
//                        new Area(3162, 9880, 3166, 9876),
//                        "Moss giant",
//                        foodIds)
//                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
//                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
//                        .setSimpleName("Apulz moss giant"),
        };

        this.addChildren(
                combatOptions[ShuffleFractal.getLoginValue() % combatOptions.length]
        );
    }


    public F2PMeleeCombats setStyleSupplier(Supplier<CombatStyle> styleSupplier) {
        if (styleSupplier == null) return this;
        this.getChildren().forEach(x -> {
            StandardCombat a = (StandardCombat) x;
            a.setStyleSupplier(styleSupplier);
        });
        return this;
    }

    public F2PMeleeCombats(Supplier<Boolean> acceptCondition, int attackTgt, int strTgt, int defTgt, Supplier<CombatStyle> styleSupplier) {
        super(acceptCondition);

        this.getChildren().forEach(x -> {
            StandardCombat a = (StandardCombat) x;
            a.setStyleSupplier(() -> {
                int atk = Skills.getRealLevel(Skill.ATTACK);
                int str = Skills.getRealLevel(Skill.STRENGTH);
                int def = Skills.getRealLevel(Skill.DEFENCE);
                if (Skills.getRealLevel(Skill.ATTACK) >= attackTgt) atk = 100;
                if (Skills.getRealLevel(Skill.STRENGTH) >= strTgt) str = 100;
                if (Skills.getRealLevel(Skill.DEFENCE) >= defTgt) def = 100;
                if (str <= Math.min(atk, def)) return CombatStyle.STRENGTH;
                if (atk <= def) return CombatStyle.ATTACK;
                return CombatStyle.DEFENCE;
            });
        });

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

                new Fractal(acceptCondition)
                        .setSimpleName("Hill giants")
                        .addChildren(
                        new StandardCombat(() -> acceptCondition.get() && Skills.getRealLevel(Skill.STRENGTH) > 30,
                                new Area(3371, 3156, 3385, 3143),
                                () -> NPCs.closest("Hill Giant"),
                                foodIds)
                                .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                                .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                                .setSimpleName("Apulz hill giant"),

                        new StandardCombat(acceptCondition, VARROCK_COWS,
                                varrockCowSupplier,
                                foodIds)
                                .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                                .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                                .setSimpleName("Varrock cows (30 str hill reqs)")
                ),


                new Fractal(acceptCondition)
                        .setSimpleName("Kharid warriors")
                        .addChildren(

                        new StandardCombat(() -> acceptCondition.get() && Skills.getRealLevel(Skill.STRENGTH) > 15,
                                new Area(3282, 3177, 3286, 3167),
                                () -> NPCs.closest(x -> x.getName().equals("Al Kharid warrior") && x.canReach()),
                                foodIds)
                                .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                                .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                                .setSimpleName("Apulz Kharid warriors"),

                        new StandardCombat(acceptCondition, LUMMY_GOBLINS,
                                lummyGoblinSupplier,
                                foodIds)
                                .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                                .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                                .setSimpleName("Lummy goblins (15 str req for warriros)")
                ),

                new StandardCombat(acceptCondition, VARROCK_COWS,
                        varrockCowSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Varrock cows"),

                new StandardCombat(acceptCondition, COW_PEN_LUMMY,
                        lummyCowSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Lummy cows"),

                new StandardCombat(acceptCondition, LUMMY_GOBLINS,
                        lummyGoblinSupplier,
                        foodIds)
                        .setEquipmentLoadout(CombatLoadouts.SCIMITAR_LOADOUT_F2P)
                        .setInventoryLoadout(CombatLoadouts.COMBAT_INVENTORY_F2P)
                        .setSimpleName("Lummy goblins"),

                new StandardCombat(acceptCondition, LUMMY_SWAMP,
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
