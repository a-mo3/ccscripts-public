package org.dreambot.behaviour.method.corp.messages;

public enum CorpMessageType {
    KILL_COMPLETE, // once a kill is had, should reset spec counters
    DWH_SPEC_HIT, // when a DHW hit lands
    BGS_SPEC_HIT, // when a BGS hit lands
    UPDATE_TEAM, // assigns a role, team leader name, world number
    REQUEST_UPDATE, // when a client has no information it sends this to get assigned something, for if onstart fails
}
