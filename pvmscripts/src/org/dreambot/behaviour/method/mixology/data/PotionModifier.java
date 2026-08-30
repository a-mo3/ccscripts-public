package org.dreambot.behaviour.method.mixology.data;

public enum PotionModifier {
    // Clicking the quick-time event on the Agitator gives 14 experience, this event can happen 1-2 times
    HOMOGENOUS(55390, 21),
    // Each click on the Retort gives 2 experience for a max of 10 clicks
    CONCENTRATED(55389, 20),
    // Clicking the quick-time event on the Alembic gives 14 experience
    CRYSTALISED(55391, 14);

    private static final PotionModifier[] TYPES = PotionModifier.values();

    private final int objectId;
    private final int quickActionExperience;

    PotionModifier(int alchemyObject, int quickActionExperience) {
        this.objectId = alchemyObject;
        this.quickActionExperience = quickActionExperience;
    }

    public static PotionModifier from(int potionModifierId) {
        if (potionModifierId < 0 || potionModifierId >= TYPES.length) {
            return null;
        }
        return TYPES[potionModifierId];
    }

    public int alchemyObjectId() {
        return objectId;
    }

    public int quickActionExperience() {
        return quickActionExperience;
    }
}