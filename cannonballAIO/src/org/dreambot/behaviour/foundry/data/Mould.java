package org.dreambot.behaviour.foundry.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.dreambot.behaviour.foundry.data.CommissionType.*;
import static org.dreambot.behaviour.foundry.data.MouldType.*;


public enum Mould {
    CHOPPER_FORTE("Chopper Forte", FORTE, createImmutableMap(BROAD, 4, LIGHT, 4, FLAT, 4)),
    GALDIUS_RICASSO("Gladius Ricasso", FORTE, createImmutableMap(BROAD, 4, HEAVY, 4, FLAT, 4)),
    DISARMING_FORTE("Disarming Forte", FORTE, createImmutableMap(NARROW, 4, LIGHT, 4, SPIKED, 4)),
    MEDUSA_RICASSO("Medusa Ricasso", FORTE, createImmutableMap(BROAD, 8, HEAVY, 6, FLAT, 8)),
    SERPENT_RICASSO("Serpent Ricasso", FORTE, createImmutableMap(NARROW, 6, LIGHT, 8, FLAT, 8)),
    SERRATED_FORTE("Serrated Forte", FORTE, createImmutableMap(NARROW, 8, HEAVY, 8, SPIKED, 6)),
    //    STILETTO_FORTE("Stiletto Forte", FORTE, createImmutableMap(NARROW, 8, LIGHT, 10, FLAT, 8)),
//    DEFENDER_BASE("Defender Base", FORTE, createImmutableMap(BROAD, 8, HEAVY, 10, FLAT, 8)),
//    JUGGERNAUT_FORTE("Juggernaut Forte", FORTE, createImmutableMap(BROAD, 4, HEAVY, 4, SPIKED, 16)),
//    CHOPPER_FORTE_1("Chopper Forte +1", FORTE, createImmutableMap(BROAD, 3, LIGHT, 4, FLAT, 18)),
//    SPIKER("Spiker!", FORTE, createImmutableMap(NARROW, 1, HEAVY, 2, SPIKED, 22)),
    SAW_BLADE("Saw Blade", BLADE, createImmutableMap(BROAD, 4, LIGHT, 4, SPIKED, 4)),
    DEFENDERS_EDGE("Defenders Edge", BLADE, createImmutableMap(BROAD, 4, HEAVY, 4, SPIKED, 4)),
    FISH_BLADE("Fish Blade", BLADE, createImmutableMap(NARROW, 4, LIGHT, 4, FLAT, 4)),
    MEDUSA_BLADE("Medusa Blade", BLADE, createImmutableMap(BROAD, 8, HEAVY, 8, FLAT, 6)),
    STILETTO_BLADE("Stiletto Blade", BLADE, createImmutableMap(NARROW, 8, LIGHT, 6, FLAT, 8)),
    GLADIUS_EDGE("Gladius Edge", BLADE, createImmutableMap(NARROW, 6, HEAVY, 8, FLAT, 8)),
    //    FLAMBERGE_BLADE("Flamberge Blade", BLADE, createImmutableMap(NARROW, 8, LIGHT, 8, SPIKED, 10)),
//    SERPENT_BLADE("Serpent Blade", BLADE, createImmutableMap(NARROW, 10, LIGHT, 8, FLAT, 8)),
//    CLAYMORE_BLADE("Claymore Blade", BLADE, createImmutableMap(BROAD, 16, HEAVY, 4, FLAT, 4)),
//    FLEUR_DE_BLADE("Fleur de Blade", BLADE, createImmutableMap(BROAD, 4, HEAVY, 18, SPIKED, 1)),
//    CHOPPA("Choppa!", BLADE, createImmutableMap(BROAD, 1, LIGHT, 22, FLAT, 2)),
    PEOPLE_POKER_POINT("People Poker Point", TIP, createImmutableMap(NARROW, 4, HEAVY, 4, FLAT, 4)),
    CHOPPER_TIP("Chopper Tip", TIP, createImmutableMap(BROAD, 4, LIGHT, 4, SPIKED, 4)),
    MEDUSAS_HEAD("Medusa's Head", TIP, createImmutableMap(BROAD, 4, HEAVY, 4, SPIKED, 4)),
    SERPENTS_FANG("Serpent's Fang", TIP, createImmutableMap(NARROW, 8, LIGHT, 6, SPIKED, 8)),
    GLADIUS_POINT("Gladius Point", TIP, createImmutableMap(NARROW, 8, HEAVY, 8, FLAT, 6)),
    SAW_TIP("Saw Tip", TIP, createImmutableMap(BROAD, 6, HEAVY, 8, SPIKED, 8)),
//    CORRUPTED_POINT("Corrupted Point", TIP, createImmutableMap(NARROW, 8, LIGHT, 10, SPIKED, 8)),
//    DEFENDERS_TIP("Defenders Tip", TIP, createImmutableMap(BROAD, 10, HEAVY, 8, SPIKED, 8)),
//    SERRATED_TIP("Serrated Tip", TIP, createImmutableMap(NARROW, 4, LIGHT, 16, SPIKED, 4)),
//    NEEDLE_POINT("Needle Point", TIP, createImmutableMap(NARROW, 18, LIGHT, 3, FLAT, 4)),
//    THE_POINT("The Point!", TIP, createImmutableMap(BROAD, 2, LIGHT, 1, FLAT, 22)),
    ;

    private final String name;
    private final MouldType mouldType;
    private final Map<CommissionType, Integer> typeToScore;

    Mould(String name, MouldType mouldType, Map<CommissionType, Integer> typeToScore) {
        this.name = name;
        this.mouldType = mouldType;
        this.typeToScore = typeToScore;
    }

    public static final Mould[] values = Mould.values();

    public static Mould forName(String text) {
        for (Mould mould : values) {
            if (mould.name.equalsIgnoreCase(text)) {
                return mould;
            }
        }
        return null;
    }

    public int getScore(CommissionType type1, CommissionType type2) {
        int score = 0;
        score += typeToScore.getOrDefault(type1, 0);
        score += typeToScore.getOrDefault(type2, 0);
        return score;
    }

    public static Map<CommissionType, Integer> createImmutableMap(CommissionType key1, int value1, CommissionType key2, int value2, CommissionType key3, int value3) {
        Map<CommissionType, Integer> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return Collections.unmodifiableMap(map);
    }
}
