package org.dreambot.behaviour.method.huey.comms.messages;

public enum HueyMsgType {
    UPDATE_ME, // update me, send the client the team its in or add it to a team and then update.
    LEADER_REGROUP, // sent from a team leader to let the team know they need to exit and enter a new instance.
    ;
}
