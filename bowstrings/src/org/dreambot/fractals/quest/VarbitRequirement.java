package org.dreambot.fractals.quest;


import org.dreambot.api.methods.settings.PlayerSettings;

public class VarbitRequirement {
    private final int varbitId;
    private final int targetValue;
    private Operation operation = Operation.EQUAL;

    public VarbitRequirement(int varbit, int targetValue) {
        this.varbitId = varbit;
        this.targetValue = targetValue;
    }

    public VarbitRequirement(int varbitId, int targetValue, Operation operation) {
        this.varbitId = varbitId;
        this.targetValue = targetValue;
        this.operation = operation;
    }

    public boolean isComplete() {
        return operation.check(PlayerSettings.getBitValue(varbitId), targetValue);
    }

    // this exists so you can use a method reference rather than a lambda expression.
    public boolean isNotComplete() {
        return !isComplete();
    }
}
