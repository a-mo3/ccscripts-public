package org.dreambot.ui.mappings.prayer;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PrayerData {
    private int targetLvl;
    private boolean buryCowBones;
}
