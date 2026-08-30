package org.dreambot.framework;

import org.dreambot.api.script.AbstractScript;

public class Root<T extends AbstractScript> extends Branch<T> {
    @Override
    public boolean isValid() {
        return true;
    }
}
