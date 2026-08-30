package org.dreambot.behaviour.smithing;

public enum SmithingMode {
    BARS, // ores to bar
    ITEM, // smith items, at any anvil
    ORE_TO_ITEM // smelts bars, then turns them to items, only happens at lum with anvil and furnace
}
