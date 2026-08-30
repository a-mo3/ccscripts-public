package org.dreambot.generics;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.widget.Widget;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.IronFractal;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Widgets can be inside other widgets that are scroll panes
 * this takes a widgetchild target and widgetchild scrollPane
 * place cursor in center of scroll pane, scroll up or down, attempting to put target
 * in the center of scrollpane
 */
public class GenericWidgetScroll extends IronFractal {
    final Supplier<WidgetChild> targetSupplier;
    final Supplier<WidgetChild> paneSupplier;

    public GenericWidgetScroll(Supplier<WidgetChild> targetSupplier, Supplier<WidgetChild> paneSupplier) {
        super(() -> {
            WidgetChild t = targetSupplier.get();
            WidgetChild p = paneSupplier.get();
            if (t == null || p == null) return false;
            return scrollDirection(t, p) != 0;
        });
        this.targetSupplier = targetSupplier;
        this.paneSupplier = paneSupplier;
    }

//    public GenericWidgetScroll(Filter<WidgetChild> targetFilter, Filter<WidgetChild> paneFilter) {
//        super(() -> {
//            WidgetChild t = Widgets.get(targetFilter);
//            WidgetChild p = Widgets.get(paneFilter);
//            if (t == null || p == null) return false;
//            return scrollDirection(t, p) != 0;
//        });
//        this.targetSupplier = () -> Widgets.get(targetFilter);
//        this.paneSupplier = () -> Widgets.get(paneFilter);
//    }

    @Override
    protected int onLoop() {
        WidgetChild t = targetSupplier.get();
        WidgetChild p = paneSupplier.get();
        boolean sign = scrollDirection(t, p) >= 0;
        log("Scrolling " + (sign ? "up" : "down"));
        Mouse.move(new Point((int) p.getRectangle().getCenterX(), (int) p.getRectangle().getCenterY()));
        Mouse.scroll(sign, 600, () -> false);
        return sleep();
    }

    /**
     * checks if the target widget is within scroll pane bounds, and returns a sign for which direct to scroll
     *
     * @param target     button you are trying to get within scroll bounds
     * @param scrollPane the pane that holds the button
     * @return 1 = scroll up, 0 = within bounds,
     */
    static int scrollDirection(WidgetChild target, WidgetChild scrollPane) {
        // we only scroll up and down in this game, so lets only consider Y
        int targetTop = target.getY();
        int targetBottom = targetTop + target.getHeight();

        int paneTop = scrollPane.getY();
        int paneBottom = scrollPane.getY() + scrollPane.getHeight();

        Logger.info(String.format("Scroll direction Top t: %d Top p: %d Bottom t: %d Bottom p: %d", targetTop, paneTop, targetBottom, paneBottom));
        // Y is 0 at the top of the screen
        if (targetTop <= paneTop) return 1;
        if (targetBottom >= paneBottom) return -1;
        return 0;
    }
}
