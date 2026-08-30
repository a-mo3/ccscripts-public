package org.dreambot.behaviour.training.sailing;

/**
 * courier tasks seem to have a type
 * they are stoed in varbits
 * 19574 for 1st task
 * 19577 for 2nd
 * ??? for 3rd +
 */
public enum ShipmentType {
    COURIER_COCONUTS(39),
    COURIER_FISH(26),
    COURIER_EYEPATCH(42),
    COURIER_LOGS(4),
    COURIER_BANANAS(40)
    ;

    final int value;


    ShipmentType(int value) {
        this.value = value;
    }
}
