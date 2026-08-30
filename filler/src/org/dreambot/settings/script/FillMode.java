package org.dreambot.settings.script;

import lombok.Getter;
import org.dreambot.fractals.data.ItemID;

@Getter
public enum FillMode {
    JUG(ItemID.JUG),
    BUCKET(ItemID.BUCKET),
    BOWL(ItemID.BOWL),
    VIAL(ItemID.VIAL),
    ;

    final int id;

    FillMode(int id) {
        this.id = id;
    }
}
