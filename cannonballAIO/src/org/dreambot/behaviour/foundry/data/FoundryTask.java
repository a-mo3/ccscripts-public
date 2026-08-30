package org.dreambot.behaviour.foundry.data;

/**
 * hold the sprite id & corresponding heat
 */
public enum FoundryTask {
    HAMMER(4442, Heat.HIGH, "Hammer Task"),
    GRINDSTONE(4443, Heat.MED, "Grindstone Task"),
    POLISH(4444, Heat.LOW, "Polishing wheel"),
    NONE(0000, Heat.NONE, "No task.");

    public final int spriteID;
    public final Heat heat;
    public final String name;

    FoundryTask(int spriteID, Heat heat, String name) {
        this.spriteID = spriteID;
        this.heat = heat;
        this.name = name;
    }
}
