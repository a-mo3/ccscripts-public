package org.dreambot.listener.impl;

import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.EventListener;

public interface ObjectListener extends EventListener {

    void onObjectSpawn(GameObject object);

    void onObjectRemove(GameObject object);
}
