package org.dreambot.behaviour.method.barrows;


import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.prayer.Prayer;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.fractals.paint.PaintInfo;

public enum BarrowsBrother implements PaintInfo {
    // optimize dig tiles to be https://oldschool.runescape.wiki/images/thumb/Barrows_optimal_path.png/281px-Barrows_optimal_path.png?9aaf0
    DHAROK("Dharok", new Tile(3575, 3298, 0), new Area(3548, 9720, 3560, 9710, 3), BarrowsVarbits.BARROWS_KILLED_DHAROK, Skill.MAGIC, Prayer.PROTECT_FROM_MELEE, 115),
    AHRIM("Ahrim", new Tile(3566, 3289, 0), new Area(3549, 9706, 3562, 9694, 3), BarrowsVarbits.BARROWS_KILLED_AHRIM, Skill.RANGED, Prayer.PROTECT_FROM_MAGIC, 98),
    VERAC("Verac", new Tile(3557, 3298, 0), new Area(3580, 9702, 3567, 9710, 3), BarrowsVarbits.BARROWS_KILLED_VERAC, Skill.MAGIC, Prayer.PROTECT_FROM_MELEE, 115),
    TORAG("Torag", new Tile(3554, 3283, 0), new Area(3563, 9693, 3576, 9682, 3), BarrowsVarbits.BARROWS_KILLED_TORAG, Skill.MAGIC, Prayer.PROTECT_FROM_MELEE, 115),
    KARIL("Karil", new Tile(3566, 3275, 0), new Area(3544, 9689, 3557, 9678, 3), BarrowsVarbits.BARROWS_KILLED_KARIL, Skill.MAGIC, Prayer.PROTECT_FROM_MISSILES, 98),
    GUTHAN("Guthan", new Tile(3577, 3283, 0), new Area(3532, 9709, 3545, 9699, 3), BarrowsVarbits.BARROWS_KILLED_GUTHAN, Skill.MAGIC, Prayer.PROTECT_FROM_MELEE, 115),
    ;

    public final String name;
    public final Tile digTile;
    public final Area tombArea;
    public final int killedVarbit;
    public final Skill weakness;
    public final Prayer prayerStyle;
    public final int combatLevel;

    /**
     * @param name         name.
     * @param digTile      tile for the top of their hill
     * @param killedVarbit self explanatory
     * @param weakness     ranged or magic for what style
     * @param prayerStyle  prayer for their attack style, naming kinda fucked
     * @param combatLevel
     */
    BarrowsBrother(String name, Tile digTile, Area tombArea, int killedVarbit, Skill weakness, Prayer prayerStyle, int combatLevel) {
        this.name = name;
        this.digTile = digTile;
        this.tombArea = tombArea;
        this.killedVarbit = killedVarbit;
        this.weakness = weakness;
        this.prayerStyle = prayerStyle;
        this.combatLevel = combatLevel;
    }

    public static int killedBrothersCount() {
        int kc = 0;
        if (AHRIM.hasKilled()) kc++;
        if (GUTHAN.hasKilled()) kc++;
        if (TORAG.hasKilled()) kc++;
        if (VERAC.hasKilled()) kc++;
        if (DHAROK.hasKilled()) kc++;
        if (KARIL.hasKilled()) kc++;
        return kc;
    }

    public int getBitValue() {
        return PlayerSettings.getBitValue(killedVarbit);
    }

    public boolean hasKilled() {
        return getBitValue() > 0;
    }

    @Override
    public String[] getPaintInfo() {
        return new String[]{
                "Ahrim - " + AHRIM.getBitValue() + " : " + AHRIM.hasKilled(),
                "Guthan - " + GUTHAN.getBitValue() + " : " + GUTHAN.hasKilled(),
                "Torag - " + TORAG.getBitValue() + " : " + TORAG.hasKilled(),
                "Dharok - " + DHAROK.getBitValue() + " : " + DHAROK.hasKilled(),
                "Karil - " + KARIL.getBitValue() + " : " + KARIL.hasKilled(),
        };
    }
}
