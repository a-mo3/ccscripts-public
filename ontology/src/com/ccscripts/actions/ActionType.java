package com.ccscripts.actions;

import com.ccscripts.actions.impl.hard.CameraRotation;
import com.ccscripts.actions.impl.hard.EntityInteraction;
import com.ccscripts.actions.impl.hard.KeyPressAction;
import com.ccscripts.actions.impl.hard.WalkAction;
import com.ccscripts.actions.impl.soft.MouseMoveAction;
import lombok.Getter;

@Getter
public enum ActionType {
    CAMERA_ROTATION(CameraRotation.class),
    ENTITY_INTERACTION(EntityInteraction.class),
    WALK(WalkAction.class),
    MOUSE_MOVEMENT(MouseMoveAction.class),
    STATE_CHANGE(StateChangeAction.class),
    KEY_PRESS(KeyPressAction.class)
    ;

    /*
    I've decided type of actions cant really be soft/hard generally
    an interaction with a widget, like a tab, may be soft, walking is always hard
     */
//    private final boolean soft;
    private final Class<? extends AbstractAction> clazz;


    ActionType(Class<? extends AbstractAction> clazz) {
        this.clazz = clazz;
    }
}
