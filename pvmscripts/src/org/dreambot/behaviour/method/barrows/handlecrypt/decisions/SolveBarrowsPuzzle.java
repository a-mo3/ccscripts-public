package org.dreambot.behaviour.method.barrows.handlecrypt.decisions;

import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.TickDecision;

public class SolveBarrowsPuzzle extends TickDecision {
    @Override
    public boolean evaluate() {
        WidgetChild sol = getSolution();
        if (sol != null) {
            log("Found solution");
            sol.interact();
            return true;
        } else {
            log("Failed to find solution widget");
        }
        return false;
    }

    private WidgetChild getSolution() {
        WidgetChild first = Widgets.get(25, 3);
        if (first == null) {
            return null;
        }

        int target = first.getDisabledMediaType() - 3;
        return Widgets.get(x -> x.getDisabledMediaType() == target);
    }
}
