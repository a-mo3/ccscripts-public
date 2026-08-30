package com.ccscripts.reproducer;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.impl.hard.CameraRotation;
import com.ccscripts.actions.impl.hard.KeyPressAction;
import org.dreambot.api.input.Keyboard;

/**
 * reproduces a key action press
 *
 */
public class CameraReproducer extends AbstractActionReproducer {
    private final CameraRotation cameraRotation;

    public CameraReproducer(CameraRotation cameraRotation, int cont) {
        super("Camera " + cameraRotation);
        this.cameraRotation = cameraRotation;
    }

    @Override
    public AbstractAction getAction() {
        return cameraRotation;
    }

    @Override
    public void execute() {
        log("Reproduce camera action " + cameraRotation);
    }
}
