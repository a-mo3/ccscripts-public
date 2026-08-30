package org.dreambot.ui.mappings.cooking;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CookingData {
    private int targetLvl;

    private boolean isCookShrimp;
    private boolean isCookSalmon;
    private boolean isCookLobster;
    private boolean isCookSwordfish;
}
