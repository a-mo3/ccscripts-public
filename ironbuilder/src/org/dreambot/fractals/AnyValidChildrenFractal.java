package org.dreambot.fractals;

import java.util.Objects;

public class AnyValidChildrenFractal extends IronFractal {
    public AnyValidChildrenFractal() {
        super(() -> false);
        this.acceptCondition = () -> this.children != null && this.children.stream().filter(Objects::nonNull).anyMatch(IronFractal::isValid);
    }
}
