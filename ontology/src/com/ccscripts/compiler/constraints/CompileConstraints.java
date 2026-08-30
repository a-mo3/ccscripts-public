package com.piler.constraints;

import com.ccscripts.actions.AbstractAction;

import java.util.List;

/**
 * when actions are being "compiled", ignore the action set if it meets any of these conditions
 */
public abstract class CompileConstraints {
    /**
     * used when compiling reproducers from an action log, for filtering out invalid training data
     *
     * @param actions actions in a replay
     * @return null if we completely disallow the replay from being used, otherwise modified or the same as what was given
     */
    public abstract List<AbstractAction> constrainActions(List<AbstractAction> actions);
}
