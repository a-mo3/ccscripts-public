package com.piler;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.actions.StateChangeAction;
import com.ccscripts.actions.impl.hard.CameraRotation;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.ccscripts.actions.impl.hard.KeyPressAction;
import com.ccscripts.actions.impl.hard.WalkAction;
import com.ccscripts.actions.impl.soft.MouseMoveAction;
import com.ccscripts.cballs.framework.Node;
import com.ccscripts.cballs.framework.Replay;
import com.ccscripts.cballs.framework.ScriptNode;
import com.piler.constraints.NodeConstraint;
import com.ccscripts.reproducer.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.dreambot.api.utilities.Logger;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

/**
 * The point of this is to parse the action log,
 * generate objects that reproduce behavior in an observed transition, subject to some constraints
 * (constraints being something like only allow item interactions with items that match (ID, container)
 */
@Slf4j
public class ActionLogParser {
    private List<AbstractAction> loadList() {
        Type listType = new TypeToken<List<AbstractAction>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AbstractAction.class, new ActionDeserializer())
                .create();
        try (FileReader reader = new FileReader(System.getProperty("scripts.path") + "/actionLog.json")) {
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            Logger.log(Color.RED, "Failed to load action log " + e.getMessage());
        }
        return null;
    }


    public List<StateTransition> parse() {
        List<StateTransition> transitions = new LinkedList<>();

        List<AbstractAction> loaded = loadList();
        if (loaded == null) {
            Logger.log(Color.RED, "Failed to load action log");
            return null;
        }

        Logger.log(Color.YELLOW, "Loaded log size " + loaded.size());
        int prevStateChangeIndex = 0;
        for (int i = 0; i < loaded.size(); i++) {
            AbstractAction abstractAction = loaded.get(i);
//            Logger.log(Color.YELLOW, "Action " + abstractAction.getType());
            if (abstractAction.getType() == ActionType.STATE_CHANGE) {
                List<AbstractAction> a = new ArrayList<>(loaded.subList(prevStateChangeIndex, i + 1));
//                for (AbstractAction action : a) {
//                    Logger.log(Color.PINK, action);
//                }
                transitions.add(new StateTransition((StateChangeAction) abstractAction, a));
                prevStateChangeIndex = i;
            }
        }

        return transitions;
    }

    public void populate(Node scriptTree, List<StateTransition> training) {
        Logger.log(Color.green, "Setting up inference");
        Stack<Node> search = new Stack<>();
        List<ScriptNode> scriptNodes = new ArrayList<>();
        search.add(scriptTree);
        // id, scriptnode
        // might be to be collection of ScriptNode if we have multiple of the same node
        Map<String, ScriptNode> nodeIdMap = new HashMap<>();

        while (!search.isEmpty()) {
            Node n = search.pop();
            if (n instanceof ScriptNode) {
                scriptNodes.add((ScriptNode) n);
                nodeIdMap.put(((ScriptNode) n).getIdentifier(), (ScriptNode) n);
                continue;
            }
            search.addAll(n.getChildren());
        }

        // node identifier, transitions to next state;
        Map<String, List<StateTransition>> taskTransitions = new HashMap<>();
        for (StateTransition stateTransition : training) {
            if (!stateTransition.getStateChangeAction().isExpectedTransition()) {
                Logger.log(Color.green, "Unexpected transition, ignored " + stateTransition);
                continue;
            }


            String firstState = stateTransition.getStateChangeAction().getPrevNodeId();
            if (!taskTransitions.containsKey(firstState)) {
                taskTransitions.put(firstState, new ArrayList<>());
            }
            taskTransitions.get(firstState).add(stateTransition);
        }

        for (Map.Entry<String, List<StateTransition>> stringListEntry : taskTransitions.entrySet()) {
            Logger.log(Color.green, stringListEntry.getKey() + " Trained on " + stringListEntry.getValue().size());

//            String scriptNodeId = stringListEntry.getKey();
//            // todo delete this, debugging why waiting is rly short sometimes
//            Logger.info(scriptNodeId + " ----------- ");
//            for (StateTransition stateTransition : stringListEntry.getValue()) {
//                Logger.info("Ms time " +
//                        (stateTransition.getActionHistory().get(stateTransition.getActionHistory().size() - 1).getTimestamp() - stateTransition.getActionHistory().get(0).getTimestamp())
//                        + " " + stateTransition.getActionHistory().size()
//                );
//
//                if (stateTransition.getActionHistory().size() < 3) {
//                    for (AbstractAction abstractAction : stateTransition.getActionHistory()) {
//                        Logger.info(abstractAction.toString() + " ");
//                    }
//                }
//            }
//            // debug end -------------

            ScriptNode scriptNode = nodeIdMap.get(stringListEntry.getKey());
            if (scriptNode == null) {
                Logger.error("Script node id null " + stringListEntry.getKey());
                continue;
            }
            // check nodes constraints
            // a constraint may require at least 1 menu action in which case we'd return null if it didnt match
            // stuff like that
            NodeConstraint nodeConstraint = scriptNode.getConstraint();

            // for each node in the tree give them their list of reproducers, by compiling
            // compile method compiles 1 history to 1 list of action reproducers
            // so here we iterate the list of transitions for a state, then add them all to the pool to be used by that node
            for (int i = 0; i < stringListEntry.getValue().size(); i++) {
//            for (StateTransition stateTransition : stringListEntry.getValue()) {
                StateTransition stateTransition = stringListEntry.getValue().get(i);
                if (nodeConstraint != null) {
                    Logger.log(Color.ORANGE, " ------------ Testing constraint ---------");
                    List<AbstractAction> constrainedActionHistory = nodeConstraint.applyCompileConstraints(stateTransition.getActionHistory());
                    if (constrainedActionHistory == null) {
                        Logger.log(Color.ORANGE, " ------------ Cleaning dirty data ---------");
                        continue;
                    }
                    scriptNode.getReplays().add(compileToReproducers(constrainedActionHistory, i));
                } else {
                    scriptNode.getReplays().add(compileToReproducers(stateTransition.getActionHistory(), i));
                }
            }

            // after the script nodes have been populated with reproducers, call their methods to set flags on the reproducers
            scriptNode.configureReproducers();
        }
    }

    AbstractAction lastOne = null;

    public Replay compileToReproducers(List<AbstractAction> actions, int continuity) {
        // big switch for types here? reproducers in action type enum? not sure will review later
        List<AbstractActionReproducer> reproducers = new ArrayList<>();

        for (int i = 0; i < actions.size(); i++) {
            AbstractAction action = actions.get(i);
            switch (action.getType()) {
                case ENTITY_INTERACTION:
                    EntityInteraction ei = (EntityInteraction) action;
                    int opCode = ei.getRow().getOpcode();
                    if (opCode == 23 || opCode == 1006) break;
                    addReproducer(new EntityInteractionReproducer(ei, i), reproducers);
                    break;
                case MOUSE_MOVEMENT:
                    MouseMovementReproducer r = new MouseMovementReproducer((MouseMoveAction) action, i);
                    // backwards pass the reproducers, if they are within this mouse action, add them to r & remove from reproducers

                    // todo maybe, we should split a path into multiple actions if its interrupted by another action
                    reproducers.stream().filter(x -> {
                        if (x.getAction().getType() == ActionType.MOUSE_MOVEMENT
                                || x.getAction().getType() == ActionType.STATE_CHANGE)
                            return false;
                        return r.getAction().getMouseMovement().happensWithin(x.getAction().getTimestamp());
                    }).forEach(r::addDuringReproducer);

                    reproducers.removeIf(x -> {
                        if (x.getAction().getType() == ActionType.MOUSE_MOVEMENT || x.getAction().getType() == ActionType.STATE_CHANGE)
                            return false;
                        return r.getAction().getMouseMovement().happensWithin(x.getAction().getTimestamp());
                    });
                    reproducers.add(r);
                    break;
                case WALK:
                    // check previous should always be a entity interaction
                    AbstractAction prevAction = null;
                    EntityInteraction menuRowAction = null;
                    if (i > 0) {
                        prevAction = actions.get(i - 1);
                        if (prevAction != null && prevAction.getType() == ActionType.ENTITY_INTERACTION) {
                            menuRowAction = (EntityInteraction) prevAction;
                            // should only be 23 or 1006
//                            int opCode = menuRowAction.getRow().getOpcode();
//                            if (opCode == 23 || opCode == 1006) {
//                                Logger.log(Color.green, "Walk action preceded by Valid menu row");
//                            } else {
//                                Logger.log(Color.green, "Walk action preceded by Invalid menu row");
//                            }
                        } else {
                            Logger.log(Color.green, "Walk action not preceded by menu row");
                            continue;
                        }
                    }
                    addReproducer(new WalkReproducer((WalkAction) action, menuRowAction), reproducers);
                    break;
                case KEY_PRESS:
                    addReproducer(new KeyReproducer((KeyPressAction) action, i), reproducers);
                    break;
                case CAMERA_ROTATION:
                    addReproducer(new CameraReproducer((CameraRotation) action, i), reproducers);
                    break;
                case STATE_CHANGE:
                    // state change has no reproducer, but needs to be included so we know when to sleep
                    addReproducer(new StateChangeReproducer((StateChangeAction) action, i), reproducers);
                    break;

                default:
                    Logger.log(Color.green, "Unhandled type " + action.getType());
            }
        }
        return new Replay(reproducers, continuity);
    }

    /**
     * (Nevermind, the mouse movement gets reported after the actions that happened during it)
     *
     * @param reproducer the reproducer list we're making to add to a script node
     *                   lastMouse  because mouse movements have actions happen during them, we keep the last mouse and if its during
     *                   that period, we add it to that instead of the list
     */
    private void addReproducer(AbstractActionReproducer reproducer,
//                               MouseMovementReproducer lastMouse,
                               List<AbstractActionReproducer> reproducers) {
//        lastOne = reproducer.getAction();
//        if (lastMouse == null) {
//            reproducers.add(reproducer);
//            return;
//        }
//        // start, when it was initialized in the mouse listener
//        long lastMouseStartTime = lastMouse.getAction().getMouseMovement().getStartTime();
//        // 1 not 0 because first movement in a mouse path is at init
//        List<TimestampedPoint> ts = lastMouse.getAction().getMouseMovement().getPointHistory();
//        // last point, when it stayed still again
//        long lastPointTime = ts.get(ts.size() - 1).getTimestamp();
//
//        long reproducerStartTime = reproducer.getAction().getTimestamp();
//
//
////        Logger.log(Color.green, lastMouseStartTime + " till " + lastPointTime + " or " + (lastPointTime - lastMouseStartTime));
////        Logger.log(Color.green, lastMouseStartTime + " till " + lastPointTime + " or " + (lastPointTime - lastMouseStartTime));
//        if (reproducerStartTime > lastMouseStartTime && reproducerStartTime < lastPointTime) {
//            Logger.log(Color.green, "--------- Action happens within a mouse movement ---------");
//            lastMouse.addDuringReproducer(reproducer);
//            return;
//        }

        reproducers.add(reproducer);
    }
}
