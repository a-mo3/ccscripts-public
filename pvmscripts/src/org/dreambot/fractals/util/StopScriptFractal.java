package org.dreambot.fractals.util;

import org.dreambot.api.Client;
import org.dreambot.fractals.Fractal;

public class StopScriptFractal extends Fractal {
    public StopScriptFractal() {
        super(() -> true);
        setSimpleName("Stop script");
    }

    @Override
    public int onLoop() {
        log("Script finished");
        Client.getInstance().getScriptManager().stop();
        return -1;
    }
}
