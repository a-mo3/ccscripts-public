package org.dreambot.fractals;

import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Supplier;

public class ShuffleFractal extends Fractal {
    // no args constructor for valid when any of the children are valid
    private boolean acceptWithAny = false;

    public ShuffleFractal() {
        acceptWithAny = true;
    }

    public ShuffleFractal(Supplier<Boolean> acceptCondition) {
        super(acceptCondition);
    }

    @Override
    public ShuffleFractal addChildren(Fractal... childFractals) {
        super.addChildren(childFractals);
        shuffle();
        if (acceptWithAny) this.acceptCondition = () -> this.getChildren().stream()
                .anyMatch(Fractal::isValid);
        return this;
    }

    /**
     * shuffle all the children using login value
     */
    public void shuffle() {
        Collections.shuffle(this.getChildren(), new Random(getLoginValue()));
    }

    @Override
    public int onLoop() {
        Logger.info(Arrays.toString(getChildren().stream().filter(Fractal::isValid).toArray()));
        Logger.info("Shuffle fractal onloop");
        return 8_000;
    }

    /**
     * @return the sum of your login ascii values, used for random seed so same account does things in same place same order
     */
    public static int getLoginValue() {
        String str = ScriptManager.getScriptManager().getAccountNickname();
        int sum = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            sum += c;
        }

        return sum;
    }
}
