package org.dreambot.behaviour.method.mixology.data;

import lombok.ToString;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.fractals.data.ItemID;

import java.awt.*;

import static org.dreambot.behaviour.method.mixology.MixologyData.*;

@ToString
public enum PotionReagent {
    MOX('M', "03a9f4", 54868, VARP_MOX_RESIN, ItemID.MOX_PASTE),
    AGA('A', "00e676", 54867, VARP_AGA_RESIN, ItemID.AGA_PASTE),
    LYE('L', "e91e63", 54869, VARP_LYE_RESIN, ItemID.LYE_PASTE);

    public static final PotionReagent[] ENTRIES = values();

    private final char character;
    private final String colorCode;
    private final Color color;
    private final int leverId;
    private final int resinVarpId;
    private final int resinId;

    PotionReagent(char character, String colorCode, int spriteId, int resinVarpId, int resinId) {
        this.character = character;
        this.colorCode = colorCode;
        this.color = Color.decode("#" + colorCode);
        this.leverId = spriteId;
        this.resinVarpId = resinVarpId;
        this.resinId = resinId;
    }

    public char character() {
        return character;
    }

    public String colorCode() {
        return colorCode;
    }

    public Color color() {
        return color;
    }

    public GameObject getLever() {
        return GameObjects.closest(leverId);
    }

    public int resinVarpId() {
        return resinVarpId;
    }

    /**
     * @return the reagents that are deposited and available to make potions with
     */
    public int getReagentCount() {
        return PlayerSettings.getBitValue(resinVarpId);
    }

    public int pasteCountInInv() {
        return Inventory.count(resinId);
    }
}