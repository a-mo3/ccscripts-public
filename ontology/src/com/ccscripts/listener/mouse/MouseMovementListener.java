package com.ccscripts.listener.mouse;

import java.util.EventListener;

public interface MouseMovementListener extends EventListener {
    void onMouseMovement(MouseMovement currentMovement);
}
