package org.dreambot.fractals.util;

import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.fractals.quest.Operation;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VarplayerRequirement {
    private int varplayerId;
    private List<Integer> values;
    private Operation operation;
    private String displayText;

    private int bitPosition;
    private boolean bitIsSet;

    private int bitShiftRight;

    public VarplayerRequirement(int varplayerId, int value) {
        this.varplayerId = varplayerId;
        this.values = Arrays.asList(value);
        this.operation = Operation.EQUAL;
        this.displayText = null;

        this.bitPosition = -1;
        this.bitIsSet = false;
        this.bitShiftRight = -1;
//        shouldCountForFilter = true;
    }

    public VarplayerRequirement(int varplayerId, int value, Operation operation) {
        this.varplayerId = varplayerId;
        this.values = Arrays.asList(value);
        this.operation = operation;
        this.displayText = null;

        this.bitPosition = -1;
        this.bitIsSet = false;
        this.bitShiftRight = -1;
//        shouldCountForFilter = true;
    }

    public VarplayerRequirement(int varplayerId, boolean bitIsSet, int bitPosition) {
        this.varplayerId = varplayerId;
        this.values = Arrays.asList(-1);
        this.operation = Operation.EQUAL;
        this.displayText = null;

        this.bitPosition = bitPosition;
        this.bitIsSet = bitIsSet;
        this.bitShiftRight = -1;
    }

    public boolean check() {
        int varpValue = PlayerSettings.getConfig(varplayerId);
        if (bitPosition >= 0) {
            return bitIsSet == BigInteger.valueOf(varpValue).testBit(bitPosition);
        } else if (bitShiftRight >= 0) {
            return values.stream().anyMatch(value -> operation.check(varpValue >> bitShiftRight, value));
        }
        return values.stream().anyMatch(value -> operation.check(varpValue, value));
    }

    public String getDisplayText() {
        if (displayText != null) {
            return displayText;
        }
        if (bitPosition >= 0) {
            return varplayerId + " must have the " + bitPosition + " bit set.";
        }
        return varplayerId + " must be + " + operation.name().toLowerCase(Locale.ROOT) + " " + values;
    }
}
