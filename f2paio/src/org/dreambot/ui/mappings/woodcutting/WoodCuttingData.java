package org.dreambot.ui.mappings.woodcutting;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WoodCuttingData {
    private int targetLevel;

    private boolean chopLogs;
    private boolean chopOaks;
    private boolean chopWillow;
    //    private boolean chopMaple;
    private boolean chopYew;

    private boolean shouldBank;
}
