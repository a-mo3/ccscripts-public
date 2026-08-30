package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import lombok.Getter;
import org.dreambot.api.utilities.Logger;

import java.awt.*;

/**
 * a question arises as to why this exists, and why AbstractAction doesn't just have an execute method
 * maybe it should, im not sure
 * but i think there are conditions where constraints might need to cause an action to be compiled to a
 * different kind of reproducer
 */
@Getter
public abstract class AbstractActionReproducer {
    final String name;

    protected AbstractActionReproducer(String name) {
        this.name = name;
    }

    public abstract AbstractAction getAction();

    public abstract void execute();

    protected void log(String msg) {
        Logger.log(Color.PINK , "[Inference] " + msg);
    }

    @Override
    public String toString() {
        return name;
    }
}
