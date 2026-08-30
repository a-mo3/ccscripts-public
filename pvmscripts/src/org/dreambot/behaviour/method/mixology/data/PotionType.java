package org.dreambot.behaviour.method.mixology.data;

import com.google.common.collect.ImmutableMap;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.dreambot.behaviour.method.mixology.data.PotionReagent.*;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(ItemID.MAMMOTHMIGHT_MIX, ItemID.MAMMOTHMIGHT_MIX_30021, 1900, MOX, MOX, MOX),
    MYSTIC_MANA_AMALGAM(ItemID.MYSTIC_MANA_AMALGAM, ItemID.MYSTIC_MANA_AMALGAM_30022, 2150, MOX, MOX, AGA),
    MARLEYS_MOONLIGHT(ItemID.MARLEYS_MOONLIGHT, ItemID.MARLEYS_MOONLIGHT_30023, 2400, MOX, MOX, LYE),
    ALCO_AUGMENTATOR(ItemID.ALCOAUGMENTATOR, ItemID.ALCOAUGMENTATOR_30024, 1900, AGA, AGA, AGA),
    AZURE_AURA_MIX(ItemID.AZURE_AURA_MIX, ItemID.AZURE_AURA_MIX_30026, 2650, AGA, AGA, MOX),
    AQUALUX_AMALGAM(ItemID.AQUALUX_AMALGAM, ItemID.AQUALUX_AMALGAM_30025, 2900, AGA, LYE, AGA),
    LIPLACK_LIQUOR(ItemID.LIPLACK_LIQUOR, ItemID.LIPLACK_LIQUOR_30027, 1900, LYE, LYE, LYE),
    MEGALITE_LIQUID(ItemID.MEGALITE_LIQUID, ItemID.MEGALITE_LIQUID_30029, 3150, MOX, LYE, LYE),
    ANTI_LEECH_LOTION(ItemID.ANTILEECH_LOTION, ItemID.ANTILEECH_LOTION_30028, 3400, AGA, LYE, LYE),
    MIXALOT(ItemID.MIXALOT, ItemID.MIXALOT_30030, 3650, MOX, AGA, LYE);

    public static final PotionType[] TYPES = PotionType.values();

    private static final Map<Integer, PotionType> ITEM_MAP;

    static {
        Map<Integer, PotionType> builder = new HashMap<Integer, PotionType>();
        for (PotionType p : PotionType.values()) {
            builder.put(p.itemId(), p);
            builder.put(p.modifiedItemId(), p);
        }
        ITEM_MAP = builder;
    }

    private final int itemId;
    private final int modifiedItemId;
//    private final String recipe;
    private final String abbreviation;
    private final int experience;
    private final PotionReagent[] components;

    PotionType(int itemId, int modifiedItemId, int experience, PotionReagent... components) {
        this.itemId = itemId;
        this.modifiedItemId = modifiedItemId;
//        this.recipe = colorizeRecipe(components);
        this.experience = experience;
        this.components = components;
        this.abbreviation = "" + components[0].character() + components[1].character() + components[2].character();
    }

    public static PotionType fromItemId(int itemId) {
        return ITEM_MAP.getOrDefault(itemId, null);
    }

    public static PotionType fromIdx(int potionTypeId) {
        if (potionTypeId < 0 || potionTypeId >= TYPES.length) {
            return null;
        }
        return TYPES[potionTypeId];
    }

//    private static String colorizeRecipe(PotionReagent[] components) {
//        if (components.length != 3) {
//            throw new IllegalArgumentException("Invalid potion components: " + Arrays.toString(components));
//        }
//        return colorizeRecipeComponent(components[0])
//                + colorizeRecipeComponent(components[1])
//                + colorizeRecipeComponent(components[2]);
//    }
//
//    private static String colorizeRecipeComponent(PotionReagent component) {
//        return "<col=" + component.colorCode() + ">" + component.character() + "</col>";
//    }

    public int itemId() {
        return itemId;
    }

    public int modifiedItemId() {
        return modifiedItemId;
    }

    public int experience() {
        return experience;
    }

    public PotionReagent[] components() {
        return components;
    }

    public String abbreviation() {
        return abbreviation;
    }
}