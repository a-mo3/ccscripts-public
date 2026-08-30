package org.dreambot.behaviour.training.herblore;

import org.dreambot.behaviour.quests.druidicritual.DruidicRitual;
import org.dreambot.fractals.Fractal;

import java.util.function.Supplier;

public class HerbloreBranch extends Fractal {
    public static final int ATTACK_POTION3 = 121;
    public static final int GUAM_POTION_UNF = 91;
    public static final int MARRENTILL_POTION_UNF = 93;
    public static final int TARROMIN_POTION_UNF = 95;
    public static final int HARRALANDER_POTION_UNF = 97;
    public static final int RANARR_POTION_UNF = 99;
    public static final int IRIT_POTION_UNF = 101;
    public static final int AVANTOE_POTION_UNF = 103;
    public static final int KWUARM_POTION_UNF = 105;
    public static final int CADANTINE_POTION_UNF = 107;
    public static final int DWARF_WEED_POTION_UNF = 109;
    public static final int TORSTOL_POTION_UNF = 111;
    public static final int EYE_OF_NEWT = 221;
    public static final int RED_SPIDERS_EGGS = 223;
    public static final int LIMPWURT_ROOT = 225;
    public static final int VIAL_OF_WATER = 227;
    public static final int VIAL = 229;
    public static final int SNAPE_GRASS = 231;
    public static final int STRENGTH_POTION3 = 115;

    public HerbloreBranch(Supplier<Boolean> acceptCondition, boolean cleanHerbs) {
        super(acceptCondition);

        addChildren(
                new DruidicRitual().setSimpleName("Druidic rit"),
                new Fractal(() -> cleanHerbs).addChildren(
                        // clean some herbs
                ).setSimpleName("Clean herbs"),

                new GenericCombine(12, GUAM_POTION_UNF, EYE_OF_NEWT, 14, 14, 600, 600)
                        .setResult(ATTACK_POTION3)
                        .setSimpleName("Atk Potions"),

                new GenericCombine(99, TARROMIN_POTION_UNF, LIMPWURT_ROOT, 14, 14, 600, 600)
                        .setResult(STRENGTH_POTION3)
                        .setSimpleName("Str Potions")

        );
    }
}
