package com.piler.constraints;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.piler.constraints.compile.MustIncludeMatchingAction;
import com.piler.constraints.compile.RemoveMatchingActions;
import lombok.Getter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * NodeConstraint is a model held by a script/ontology node
 * a constraint can have rules like a transition must at some point have an interaction with a certain item
 * other examples
 * must not walk X distance from T
 * must not take over X seconds to finish
 * <p>
 * constraint can also be used every time a replay is evaluated
 * ie if the training data clicks toggle run, it can be prevented if run is already on
 * a sleep metadata might be constrained to break within 10 seconds of a condition being true
 */
@Getter
public class NodeConstraint {
    //    private List<ActionConstraint> runtimeConstraints = new ArrayList<>();
    private List<CompileConstraints> compileConstraints = new ArrayList<>();


    public List<AbstractAction> applyCompileConstraints(List<AbstractAction> actions) {
        for (CompileConstraints compileConstraint : compileConstraints) {
            if (actions == null) return null;
            actions = compileConstraint.constrainActions(actions);
        }
        return actions;
    }

    /**
     * ie must include an entity interaction matching furnace
     *
     * @param actionPredicate one action in a replay must match this
     * @return this
     */
    public NodeConstraint mustCompileWith(Predicate<AbstractAction> actionPredicate) {
        compileConstraints.add(new MustIncludeMatchingAction(actionPredicate));
        return this;
    }

    /**
     * ie must include an entity interaction matching furnace
     *
     * @param entityPredicate one action in a replay must match this
     * @return this
     */
    public NodeConstraint mustCompileWithEntityInteraction(Predicate<EntityInteraction> entityPredicate) {
        compileConstraints.add(new MustIncludeMatchingAction(action -> {
            if (action.getType() != ActionType.ENTITY_INTERACTION) return false;
            EntityInteraction ei = (EntityInteraction) action;
            return entityPredicate.test(ei);
        }));
        return this;
    }

    public NodeConstraint mustCompileWithAtLeastOneOfTypes(ActionType... types) {
        compileConstraints.add(new MustIncludeMatchingAction(
                x -> Arrays.stream(types).anyMatch(a -> a == x.getType()))
        );
        return this;
    }

    public NodeConstraint disallowTypes(ActionType... types) {
        compileConstraints.add(new RemoveMatchingActions(
                x -> Arrays.stream(types).anyMatch(a -> a == x.getType()))
        );
        return this;
    }
}
