package org.dreambot.behaviour.method.blastfurnace;

import lombok.Getter;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.data.ItemID;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BlastFurnaceModes {
    STEEL(30, new BlastFurnaceSteel(() -> true).setSimpleName("Steel"),
            ItemID.IRON_ORE, ItemID.COAL),
    MITHRIL(50, new BlastFurnaceMithril(() -> true).setSimpleName("Mithril"),
            ItemID.COAL, ItemID.MITHRIL_ORE),
    ADAMANT(70, new BlastFurnaceAdamantite(() -> true).setSimpleName("Adamant"),
            ItemID.COAL, ItemID.ADAMANTITE_ORE),
    RUNE(85, new BlastFurnaceRune(() -> true).setSimpleName("Rune"),
            ItemID.COAL, ItemID.RUNITE_ORE),
    PROGRESSIVE(30, new Fractal()) //
    ;

    final int level;
    final Fractal fractal;
    final List<Integer> requiredOres;

    BlastFurnaceModes(int level, Fractal f, Integer... requiredOres) {
        this.level = level;
        fractal = f;
        this.requiredOres = Arrays.asList(requiredOres);
    }
}
