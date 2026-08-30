package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.StateChangeAction;

public class StateChangeReproducer extends AbstractActionReproducer {
    final StateChangeAction stateChangeAction;
    public StateChangeReproducer(StateChangeAction stateChangeAction, int contNum) {
        super("State change ");
        this.stateChangeAction = stateChangeAction;
    }

    @Override
    public AbstractAction getAction() {
        return stateChangeAction;
    }

    @Override
    public void execute() {
        log("Expecting state change " + stateChangeAction);

    }
}
