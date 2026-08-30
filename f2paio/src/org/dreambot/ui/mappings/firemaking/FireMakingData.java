package org.dreambot.ui.mappings.firemaking;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FireMakingData {
    private int targetLevel;

    private boolean burnLogs;
    private boolean burnOaks;
    private boolean burnWillow;
    private boolean burnMaple;
    private boolean burnYew;

    private boolean collectAshes;
}
