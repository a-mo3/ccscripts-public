package com.ccscripts.actions.impl.hard;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.listener.camera.CameraMovement;
import lombok.Getter;
import lombok.ToString;

@ToString
public class CameraRotation extends AbstractAction {
    @Getter
    final private CameraMovement movement;

    public CameraRotation(CameraMovement movement) {
        super(ActionType.CAMERA_ROTATION);
        this.movement = movement;
    }
}
