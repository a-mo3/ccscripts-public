package org.dreambot.fractals;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Timer;

import java.util.Collections;

public class TimedShuffleFractal extends ShuffleFractal {
    final Timer timer;

    public TimedShuffleFractal(int minutes) {
        this.timer = new Timer((long) minutes * 60 * 1000);
    }
    public TimedShuffleFractal(int minutesLow, int minutesHigh) {
        int minutes = Calculations.random(Math.min(minutesLow, minutesHigh), Math.max(minutesLow, minutesHigh));
        this.timer = new Timer((long) minutes * 60 * 1000);
    }

    @Override
    public boolean isValid() {
        if (timer.finished()) {
            timer.reset();
            log("Shuffle");
            Collections.shuffle(this.children);
        }
        return super.isValid();
    }
}
