package org.dreambot.fractals.generic;

import org.dreambot.fractals.Fractal;

import java.util.Objects;

public class AnyValidChildrenFractal extends Fractal {
    public AnyValidChildrenFractal() {
        super(() -> false);
        this.acceptCondition = () -> this.children != null
                && this.children.stream().filter(Objects::nonNull).anyMatch(Fractal::isValid);
    }

    @Override
    public int onLoop() {
        this.children.forEach(c -> log(c.getSimpleName() + "  " + c.isValid()));
        return 5_000;
    }
}
