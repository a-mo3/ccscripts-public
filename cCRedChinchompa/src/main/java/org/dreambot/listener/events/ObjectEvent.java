package org.dreambot.listener.events;

import org.dreambot.api.Client;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.listener.base.AbstractEvent;
import org.dreambot.listener.base.EventInterface;
import org.dreambot.listener.impl.ObjectListener;

import java.util.List;

public class ObjectEvent extends AbstractEvent implements EventInterface {
    private final ObjectListener event;

    public ObjectEvent(AbstractScript script) {
        super(script);
        this.event = (ObjectListener) pEvent;
    }

    @Override
    public void run() {
        // todo this will run b4 login
        List<GameObject> current, previous = GameObjects.all(x -> x.getName().contains("trap") || x.getName().contains("snare"));
        // current gameobjects, will be updated each loop
        // previous will be set from current, and than used to eval on the next loop

        while (!shouldStop() && canRun()) {
            if (canVerify()) {
                current = GameObjects.all(x -> x.getName().contains("trap") || x.getName().contains("snare"));
//                Logger.log("current: " + current);
//                Logger.log("prev: " + previous);
                for (GameObject obj : current) {
                    if (!previous.contains(obj)) {
                        this.event.onObjectSpawn(obj);
                    }
                }
                for (GameObject obj : previous) {
                    if (!current.contains(obj)) {
                        this.event.onObjectRemove(obj);
                    }
                }
                previous = current;

            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fire(Object... params) {
        // lole
    }


    private boolean canVerify() {
        return Client.isLoggedIn() && !Client.getInstance().getRandomManager().isSolving()
                && Players.getLocal() != null && Players.getLocal().exists();
    }


    private boolean shouldStop() {
        return !Client.getInstance().getScriptManager().isRunning();
    }

}
