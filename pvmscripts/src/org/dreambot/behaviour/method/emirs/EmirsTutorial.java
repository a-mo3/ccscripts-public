package org.dreambot.behaviour.method.emirs;

import org.dreambot.api.Client;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.timing.ReactionGenerator;

public class EmirsTutorial extends Fractal {
    public EmirsTutorial() {
        super(() -> Widgets.get(679, 74) != null && Widgets.get(679, 74).isVisible());
    }

    @Override
    public int onLoop() {
        WidgetChild confirm = Widgets.get(679, 74);
        if (confirm == null) {
            log("Cant confirm");
            return ReactionGenerator.getNormal();
        }

        confirm.interact();
        Client.setIdleTime(30_000);
        return ReactionGenerator.getNormal();
    }
}
