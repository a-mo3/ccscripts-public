package com.ccscripts.actions;

import lombok.Getter;
import lombok.ToString;

/**
 * this is not actually an action but we want it in the action log
 * indicates script decision path changing
 */
@ToString
@Getter
public class StateChangeAction extends AbstractAction {
    final String prevNodeId;
    final String newNodeId;
    final boolean expectedTransition;

    public StateChangeAction(String prevNodeId, String newNodeId, boolean expectedTransition) {
        super(ActionType.STATE_CHANGE);
        this.prevNodeId = prevNodeId;
        this.newNodeId = newNodeId;
        this.expectedTransition = expectedTransition;
    }
}
