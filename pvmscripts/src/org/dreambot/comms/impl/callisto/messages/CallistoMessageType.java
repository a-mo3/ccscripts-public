package org.dreambot.comms.impl.callisto.messages;

public enum CallistoMessageType {
    // sent on every update
    TEAM_STATE,
    // sent by bot when it needs a team, team_state should be returned
    REQUEST_TEAM,
    // reports a PK situation for a given team, can only be passed for a time ~once per minute
    REPORT_PKER,
}
