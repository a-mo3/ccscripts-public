package com.ccscripts.actions.impl.hard;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import lombok.Getter;

import java.awt.event.KeyEvent;

@Getter
public class KeyPressAction extends AbstractAction {
    private final char key;
    private final int keyCode;

    public KeyPressAction(char key, int keyCode) {
        super(ActionType.KEY_PRESS);
        this.key = key;
        this.keyCode = keyCode;
    }
}
