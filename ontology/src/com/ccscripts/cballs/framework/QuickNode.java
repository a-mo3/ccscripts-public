package com.ccscripts.cballs.framework;

import com.piler.constraints.NodeConstraint;
import com.ccscripts.reproducer.EntityInteractionReproducer;
import lombok.Builder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.utilities.impl.Condition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * quick as in you only need to write the constructor
 */
@Accessors(chain = true)
public class QuickNode extends ScriptNode {
    final BooleanSupplier acceptCondition;
    final String identifier;
    final String expectedNextState;

    @Setter
    Supplier<List<Rectangle>> highlights;
    List<Consumer<EntityInteractionReproducer>> entityConfigs = new ArrayList<>();
    @Setter
    Condition sleepAfterFinished = null;

    boolean shouldConfigureReproducers;

    @Builder
    public QuickNode(BooleanSupplier acceptCondition, String identifier, String expectedNextState) {
        this.acceptCondition = acceptCondition;
        this.identifier = identifier;
        this.expectedNextState = expectedNextState;
    }

    @Override
    public boolean isValid() {
        return acceptCondition.getAsBoolean();
    }

    @Override
    public int fallBack() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getExpectedNextState() {
        return this.expectedNextState;
    }

    @Override
    public List<Rectangle> trainingHighlights() {
        if (highlights == null) return List.of();
        return highlights.get();
    }

    @Override
    public QuickNode setConstraint(NodeConstraint constraint) {
        return (QuickNode) super.setConstraint(constraint);
    }

    @Override
    protected void sleepAfterReplay() {
        if (sleepAfterFinished == null) {
            super.sleepAfterReplay();
            return;
        }
        sleepAfterFinished.verify();
    }

    @Override
    protected boolean shouldConfigureReproducers() {
        return shouldConfigureReproducers;
    }

    public QuickNode addEntityReproConfig(Consumer<EntityInteractionReproducer> config) {
        shouldConfigureReproducers = true;
        entityConfigs.add(config);
        return this;
    }

    @Override
    protected QuickNode configureEntity(EntityInteractionReproducer reproducer) {
        for (Consumer<EntityInteractionReproducer> entityConfig : entityConfigs) {
            entityConfig.accept(reproducer);
        }
        return this;
    }

    public QuickNode setSleepTime(int ms) {
        setSleepAfterFinished(() -> {
            Sleep.sleepUntil(() -> !isValid(), ms);
            return false;
        });
        return this;
    }
}
