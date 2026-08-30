package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.impl.hard.KeyPressAction;
import org.dreambot.api.input.Keyboard;

/**
 * reproduces a key action press
 *
 */
public class KeyReproducer extends AbstractActionReproducer {
    private final KeyPressAction keyPressAction;

    public KeyReproducer(KeyPressAction keyPressAction, int cont) {
        super("Keyboard " + keyPressAction.getKey());
        this.keyPressAction = keyPressAction;
    }

    @Override
    public AbstractAction getAction() {
        return keyPressAction;
    }

    @Override
    public void execute() {
        log("Reproduce key action " + keyPressAction.getKey());
        Keyboard.type(keyPressAction.getKey(), false);
    }
}
