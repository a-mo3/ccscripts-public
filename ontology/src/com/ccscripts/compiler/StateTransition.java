package com.piler;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.StateChangeAction;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StateTransition {
    private final StateChangeAction stateChangeAction;
    private final List<AbstractAction> actionHistory;

    public StateTransition(StateChangeAction stateChangeAction, List<AbstractAction> actionHistory) {
        this.stateChangeAction = stateChangeAction;
        this.actionHistory = actionHistory;
    }

    @Override
    public String toString() {
        String summary = actionHistory.stream()
                .collect(Collectors.groupingBy(AbstractAction::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));

        if (actionHistory.isEmpty()) return "Empty history? ";
        return stateChangeAction.getPrevNodeId() + " " + stateChangeAction.getNewNodeId()
                + " Valid? "
                + stateChangeAction.isExpectedTransition()
                + " Took " + (actionHistory.get(actionHistory.size() - 1).getTimestamp() - actionHistory.get(0).getTimestamp()) + "ms"
                + " \n" + summary;
    }
}
