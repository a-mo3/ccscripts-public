package org.dreambot.behaviour.method.mta;

import lombok.Getter;
import org.dreambot.api.methods.settings.PlayerSettings;

public enum MTAReward {
    INFINITY_HAT(350, 350, 3000, 400,
            "Infinity hat", 5),
    INFINITY_TOP(400, 400, 4000, 450,
            "Infinity top", 4),
    INFINITY_BOTTOMS(450, 450, 5000, 500,
            "Infinity bottoms", 8),
    INFINITY_GLOVES(175, 175, 1500, 225,
            "Infinity gloves", 7),
    INFINITY_BOOTS(120, 120, 1200, 120,
            "Infinity boots", 6),
//    BEGINNER_WAND(30, 30, 300, 30,
//            "Beginner wand", 0),
//    APPRENTICE_WAND(60, 60, 600, 60,
//            "Apprentice wand", 1),
//    TEACHER_WAND(150, 150, 1500, 200,
//            "Teacher wand", 3),
//    MASTER_WAND(240, 240, 2400, 240,
//            "Master wand", 4),
    MAGES_BOOK(500, 500, 6000, 550,
            "Mage's book", 9),
    BONES_TO_PEACEHES(200, 200, 2000, 300,
            "Bones to peaches", 10),
    ;

    @Getter
    final int requiredTelekineticPoints;
    @Getter
    final int requiredGraveyardPoints;
    @Getter
    final int requiredEnchantPoints;
    @Getter
    final int requiredAlchemyPoints;
    @Getter
    final String itemName;
    final int selectedVarbitState;
    static final int SELECETED = 10059; // varbit for what item in the store is selected

    public boolean isSelected() {
        // todo maybe check the stores open
        return PlayerSettings.getBitValue(SELECETED) == selectedVarbitState;
    }

    MTAReward(int requiredTelekineticPoints, int requiredGraveyardPoints, int requiredEnchantPoints, int requiredAlchemyPoints, String name, int varbitState) {
        this.requiredTelekineticPoints = requiredTelekineticPoints;
        this.requiredGraveyardPoints = requiredGraveyardPoints;
        this.requiredEnchantPoints = requiredEnchantPoints;
        this.requiredAlchemyPoints = requiredAlchemyPoints;
        this.itemName = name;
        this.selectedVarbitState = varbitState;
    }
}
