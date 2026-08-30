package com.ccscripts.listener.camera;

import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CameraWatcher implements Runnable {
    private List<CameraMovementListener> listeners = new ArrayList<>();

    public CameraWatcher register(CameraMovementListener listener) {
        listeners.add(listener);
        return this;
    }

    CameraMovement currentMovement;

    @Override
    public void run() {
        while (ScriptManager.getScriptManager().isRunning()) {
            sleep();
            if (listeners.isEmpty()) {
                continue;
            }

            Point cameraPos = new Point(Camera.getYaw(), Camera.getPitch());
            if (currentMovement == null) {
                currentMovement = new CameraMovement(cameraPos, Camera.getZoom());
            } else {
                Point prevPoint = currentMovement.getLastPoint();
                if (prevPoint == null || !prevPoint.equals(cameraPos)) {
                    currentMovement.append(cameraPos);
                } else {
                    if (currentMovement.getPointHistory().size() > 1) {
                        listeners.forEach(x -> x.onCameraMovement(currentMovement));
                        currentMovement = null;
                    }
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Logger.info("Sleep issue");
        }
    }
}
