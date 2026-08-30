package org.dreambot.behaviour.training.agility.wild;

import org.dreambot.fractals.TickFractal;
import org.dreambot.scriptdata.WildernessAgilitySettings;

import java.util.function.Supplier;

/**
 * Tick based wilderness course to box with minimal ticks where we are vulnerable
 */
public class TickWilderness extends TickFractal {

    public TickWilderness(Supplier<Boolean> acceptCondition, WildernessAgilitySettings settings) {
        super(acceptCondition);
        setSimpleName("Lockstep wilderness agil");

        this.paintArraySupplier = () -> new String[]{
                "Since boxed " + TickBoxingDecision.timeSinceBoxed.elapsed(),
                " "
        };

        addChildren(
                new TickWildyGoToCourse(settings.mode, settings.world).setSimpleName("Goto course"),

                new TickAgilityPkDecision(settings.mode).setSimpleName("Anti pk"),

                new TickClanRag(settings.mode, settings.bhAgressionMode).setSimpleName("Rag mode"),

                new WildyAgilityEat(settings.mode).setSimpleName("Eat"),

                new TickBoxingDecision(settings.mode).setSimpleName("Boxing"),

                new WildyCourseDecision(settings.mode).setSimpleName("Course")
        );
    }
}
