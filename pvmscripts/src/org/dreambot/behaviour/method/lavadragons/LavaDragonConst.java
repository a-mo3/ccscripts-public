package org.dreambot.behaviour.method.lavadragons;

import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Area;
import org.dreambot.fractals.data.ItemID;
import org.dreambot.scriptdata.LavaDragonSettings;

import java.util.HashMap;
import java.util.Map;

public class LavaDragonConst {
    public static final Area MID_DRAGON_AREA = new Area(3197, 3843, 3214, 3830);

    public static LavaDragonLocation location = LavaDragonLocation.MIDDLE_WEST;

    // some settings for  loadouts
    public static LavaDragonSettings settings = null;

    // item id , quantity, because we should bring more of the cheaper runes
    public static final Map<Normal, Integer[]> spellMap = new HashMap<>();

    static {
        spellMap.put(Normal.WATER_WAVE, new Integer[]{ItemID.BLOOD_RUNE, 500});
        spellMap.put(Normal.WATER_BLAST, new Integer[]{ItemID.DEATH_RUNE, 500});
        spellMap.put(Normal.WATER_BOLT, new Integer[]{ItemID.CHAOS_RUNE, 550});
        spellMap.put(Normal.WATER_STRIKE, new Integer[]{ItemID.MIND_RUNE, 1000});
    }

}
