package com.piler.constraints.compile;

import com.ccscripts.actions.AbstractAction;
import com.piler.constraints.CompileConstraints;

import java.util.List;
import java.util.function.Predicate;

/**
 * Constraint that a replay must have a matching action
 * ie closing bank must have some entity interaction, or walk.
 */
public class MustIncludeMatchingAction extends CompileConstraints {
    final Predicate<AbstractAction> mustIncludeCondition;

    public MustIncludeMatchingAction(Predicate<AbstractAction> mustIncludeCondition) {
        this.mustIncludeCondition = mustIncludeCondition;
    }

    @Override
    public List<AbstractAction> constrainActions(List<AbstractAction> actions) {
        if (actions.stream().anyMatch(mustIncludeCondition)) return actions;
        return null;
    }
}
