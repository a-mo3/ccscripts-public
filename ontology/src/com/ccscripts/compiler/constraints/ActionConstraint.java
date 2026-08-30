package com.piler.constraints;

import com.ccscripts.actions.AbstractAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Tests a single action to see if its okay
 * if its not the action may be thrown away, or a similar action can take place with replaced vars
 * ie might modify sleep
 */
public abstract class ActionConstraint {
    /**
     *
     * @param action the action we're running
     * @return null if disallowed, modified action otherwise     */
    abstract AbstractAction applyConstraints(AbstractAction action);
}
