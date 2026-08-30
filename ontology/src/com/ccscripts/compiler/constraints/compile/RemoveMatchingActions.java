package com.piler.constraints.compile;

import com.ccscripts.actions.AbstractAction;
import com.piler.constraints.CompileConstraints;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Constraint that a replay must have a matching action
 * ie closing bank must have some entity interaction, or walk.
 */
public class RemoveMatchingActions extends CompileConstraints {
    final Predicate<AbstractAction> matching;

    public RemoveMatchingActions(Predicate<AbstractAction> mustIncludeCondition) {
        this.matching = mustIncludeCondition;
    }

    @Override
    public List<AbstractAction> constrainActions(List<AbstractAction> actions) {
        return actions.stream().filter(a -> !matching.test(a)).collect(Collectors.toList());
    }
}
