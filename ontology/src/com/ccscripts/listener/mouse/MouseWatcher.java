package com.ccscripts.listener.mouse;

import com.ccscripts.model.EntityWrapper;
import org.dreambot.api.input.Mouse;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MouseWatcher implements Runnable {
    private List<MouseMovementListener> listeners = new ArrayList<>();

    public MouseWatcher register(MouseMovementListener listener) {
        listeners.add(listener);
        return this;
    }

    MouseMovement currentMovement;

    @Override
    public void run() {
        while (ScriptManager.getScriptManager().isRunning()) {
            try {
                sleep();
                if (listeners.isEmpty()) {
                    continue;
                }
                Point mousePoint = Mouse.getPosition();
                if (currentMovement == null) {
                    currentMovement = new MouseMovement(mousePoint);
                } else {
                    Point prevPoint = currentMovement.getLastPoint();
                    if (prevPoint == null || !prevPoint.equals(mousePoint)) {
                        currentMovement.append(mousePoint);
                    } else {
                        // mouse point same as last, mouse movement complete
                        if (currentMovement.getPointHistory().size() > 1) {
                            currentMovement.setHovered(Mouse.getEntitiesOnCursor().stream().map(EntityWrapper::new).collect(Collectors.toList()));
                            listeners.forEach(x -> x.onMouseMovement(currentMovement));
                            currentMovement = null;
                        }
                    }
                }
            } catch (Exception ignored) {
                Logger.info("Mouse watcher exception " + ignored);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Logger.info("Sleep issue");
        }
    }
}
