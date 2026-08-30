package org.dreambot.behaviour.method.nightmare.phosani;

import org.dreambot.api.Client;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.listener.ChatListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.api.wrappers.widgets.message.Message;
import org.dreambot.api.wrappers.widgets.message.MessageType;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

/**
 * enable run but not when you are sleepy
 */
public class PhosaniRunEnable extends Fractal implements ChatListener {
    static Timer yawnTimer = new Timer(17600);

    public PhosaniRunEnable() {
        super(() -> !isEepy() && !Walking.isRunEnabled() && Walking.getRunEnergy() > 15);
        Client.getInstance().addEventListener(this);
        Logger.info("Lace Jordans");
    }

    @Override
    public int onLoop() {
        Walking.toggleRun();
        return ReactionGenerator.getQuick();
    }

    // this maybe is a problem if you start the script and get to phosani fight in under 18 seconds
    public static boolean isEepy() {
        return !yawnTimer.finished();
    }

    @Override
    public void onMessage(Message message) {
        if (message.getType() == MessageType.PLAYER) return;
        if (message.getMessage().toLowerCase().contains("the nightmare's spores have infected you, making you feel drowsy!")) {
            yawnTimer.reset();
        }
    }
}
