package org.dreambot.fractals.events;

import java.util.function.Supplier;

public class EventExitCondition {
    final Supplier<Boolean> condition;
    final String name;

    public EventExitCondition(Supplier<Boolean> condition, String name) {
        this.condition = condition;
        this.name = name;
    }

    public boolean shouldExit() {
        return condition.get();
    }
}
