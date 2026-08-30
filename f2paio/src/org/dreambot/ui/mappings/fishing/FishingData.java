package org.dreambot.ui.mappings.fishing;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FishingData {
    private int targetLevel;

    private boolean catchShrimp;
    private boolean catchSalmon;
    private boolean catchLobster;
    private boolean catchSwordfish;

    private boolean bankLoot;
}
