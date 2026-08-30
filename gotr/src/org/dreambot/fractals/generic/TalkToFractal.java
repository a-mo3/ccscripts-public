package org.dreambot.fractals.generic;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.Entity;
import org.dreambot.fractals.Fractal;
import org.dreambot.fractals.util.Dialog;
import org.dreambot.settings.timing.ReactionGenerator;

import java.util.Arrays;
import java.util.function.Supplier;

@Accessors(chain = true)
public class TalkToFractal extends Fractal {
    private final Area area;
    @Setter
    private Tile targetTile;
    private final Supplier<Entity> target;
    private String interaction[] = new String[]{"Talk-to"};
    private String[] dialogueOptions = new String[]{};

    @Setter // some quests might require dialogue based on something that changes e.g. tree gnome village
    private Supplier<String[]> dialogueOptionsSupplier;
    @Setter
    private boolean doReachCheck = true;
    @Setter
    private int sleepTimeout = 4400;

    public TalkToFractal(Supplier<Boolean> acceptCondition, Area area, Supplier<Entity> target, String interaction, String... dialogueOptions) {
        super(acceptCondition);
        this.area = area;
        this.interaction = new String[]{interaction};
        this.target = target;
        this.dialogueOptions = dialogueOptions;
    }

    public TalkToFractal(Supplier<Boolean> acceptCondition, Area area, Supplier<Entity> target) {
        super(acceptCondition);
        this.area = area;
        this.target = target;
    }

    // ik elliot nem like to use tiles and i can see how it would be useful sometimes
    public TalkToFractal(Supplier<Boolean> acceptCondition, Tile targetTile, Supplier<Entity> target) {
        super(acceptCondition);
        this.area = targetTile.getArea(10);
        this.target = target;
        this.targetTile = targetTile;
    }

    public TalkToFractal setDialogueOptions(String... dialogueOptions) {
        this.dialogueOptions = dialogueOptions;
        return this;
    }

    public TalkToFractal setInteraction(String interaction) {
        this.interaction = new String[]{interaction};
        return this;
    }

    // its an array for things like chests and cupboards that have 2 options eg "Open", "Search
    public TalkToFractal setInteraction(String... interactions) {
        this.interaction = interactions;
        return this;
    }

    @Override
    public boolean isValid() {
        return acceptCondition.get();
    }

    @Override
    public int onLoop() {
        if (dialogueOptionsSupplier != null) dialogueOptions = dialogueOptionsSupplier.get();
        if (Dialogues.inDialogue()) {
            Dialog.solve(dialogueOptions);
            return ReactionGenerator.getNormal();
        }

        if (area != null && !area.contains(Players.getLocal())) {
            if (!Walking.shouldWalk()) return ReactionGenerator.getNormal();
            if (targetTile == null) {
                if (Walking.shouldWalk(6)) Walking.walk(area.getCenter());
            } else {
                if (Walking.shouldWalk(6)) Walking.walk(targetTile);
            }
            return ReactionGenerator.getNormal();
        }

        Entity npc = target.get();
        if (doReachCheck) {
            if (npc != null && !npc.canReach()) {
                if (Walking.shouldWalk()) Walking.walk(npc.getTile());
                return ReactionGenerator.getNormal();
            }
        }

        if (npc != null && npc.interact(x -> Arrays.stream(interaction).anyMatch(i -> i.contains(x)))) {
            Sleep.sleepUntil(Dialogues::inDialogue, sleepTimeout);
        }
        return ReactionGenerator.getNormal();
    }
}
