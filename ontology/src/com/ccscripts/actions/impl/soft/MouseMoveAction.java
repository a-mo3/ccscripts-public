package com.ccscripts.actions.impl.soft;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.ccscripts.listener.mouse.MouseMovement;
import lombok.Getter;

public class MouseMoveAction extends AbstractAction {
    @Getter
    final MouseMovement mouseMovement;

    public MouseMoveAction(MouseMovement mouseMovement) {
        super(ActionType.MOUSE_MOVEMENT);
        this.mouseMovement = mouseMovement;
    }
}
