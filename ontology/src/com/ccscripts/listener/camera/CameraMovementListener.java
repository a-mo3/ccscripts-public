package com.ccscripts.listener.camera;

import java.util.EventListener;

public interface CameraMovementListener extends EventListener {
    void onCameraMovement(CameraMovement currentMovement);
}
