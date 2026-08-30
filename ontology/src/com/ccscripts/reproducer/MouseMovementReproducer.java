package com.ccscripts.reproducer;

import com.ccscripts.actions.impl.soft.MouseMoveAction;
import com.ccscripts.listener.mouse.MouseMovement;
import com.ccscripts.model.TimestampedPoint;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.utilities.Sleep;

import java.util.ArrayList;
import java.util.List;

public class MouseMovementReproducer extends AbstractActionReproducer {
    final MouseMoveAction moveAction;
    List<AbstractActionReproducer> duringReproducers = new ArrayList<>();

    public MouseMovementReproducer(MouseMoveAction movement, int cont) {
        super("Mouse movement ");
        this.moveAction = movement;
    }

    @Override
    public MouseMoveAction getAction() {
        return moveAction;
    }

    @Override
    public void execute() {
        /*
        Mouse movements require a certain context
        like a movement to hover an object, needs to be transformed depending on camera angle to wind up on the obj
        a mouse movement to hover a forthcoming widget, no

        so we kind of need to forward pass the recording to be able to know what our next interaction is,
        there's also the chance we don't want to hover something the mouse just happened to hover at the end of the path
         */

        // because start time and first point have such a difference
        // 1 here because the first point is made at initialization
//        long initSleep = moveAction.getMouseMovement().getPointHistory().get(1).getTimestamp() - moveAction.getMouseMovement().getStartTime();
//        log("Initial mouse sleep " + initSleep);
//        Sleep.sleep(initSleep);

//        log("Reproduce mouse movement " + movement);
        // evaluate context of movement
        // if end is off screen, or on inventory minimap or chat widget, ignore the camera stuff

        // if we are trying to hover a certain entity, skip camera stuff for just hover on screen

        // if we are close to where the path would end up anyway lets just skip it

        MouseMovement mouseMovement = moveAction.getMouseMovement();
        int deltaPitch = Math.abs(mouseMovement.getCameraPitch() - Camera.getPitch());
        int deltaYaw = Math.abs(mouseMovement.getCameraYaw() - Camera.getYaw());
        int deltaZoom = Math.abs(mouseMovement.getCameraZoom() - Camera.getZoom());
        if (deltaYaw > 50 || deltaPitch > 50) {
            log("Adjusting camera to movement");
            Camera.rotateTo(mouseMovement.getCameraYaw(), mouseMovement.getCameraPitch());
        }
        if (deltaZoom > 50) {
            log("Adjusting camera zoom to movement");
            Camera.setZoom(mouseMovement.getCameraZoom());
        }

        int spd = MouseSettings.getSpeed();
        for (TimestampedPoint timestampedPoint : moveAction.getMouseMovement().getPointHistory()) {
            MouseSettings.setSpeed(100);
            Mouse.move(timestampedPoint.getPoint());
            Sleep.sleep(50);

            // other actions can happen mid way through a mouse movement, if that happens we will attach that action to
            // the mouse reproducer, and execute during that point
            // do that after you move onto the point!
            if (!duringReproducers.isEmpty()) {
                for (AbstractActionReproducer duringReproducer : duringReproducers) {
                    long timestamp = duringReproducer.getAction().getTimestamp();
                    long thisPointTime = timestampedPoint.getTimestamp();
                    if (timestamp > thisPointTime) continue;
                    if (thisPointTime - timestamp < 50) {
                        log("During reproduce " + duringReproducer.getName() + " " + (timestamp - thisPointTime));
                        duringReproducer.execute();
                    }
                }
            }

        }
        MouseSettings.setSpeed(spd);
    }

    public MouseMovementReproducer addDuringReproducer(AbstractActionReproducer reproducer) {
        duringReproducers.add(reproducer);
        return this;
    }
}
