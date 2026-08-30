package org.dreambot.generics;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.widgets.WidgetChild;
import org.dreambot.fractals.IronFractal;

import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Accessors(chain = true)
public class GenericWidgetInteraction extends IronFractal {

    final Supplier<WidgetChild> widgetChildSupplier;
    @Setter
    String action;

    public GenericWidgetInteraction(BooleanSupplier acceptCondition, Supplier<WidgetChild> widgetChildSupplier) {
        super(acceptCondition);
        this.widgetChildSupplier = widgetChildSupplier;
    }

    public GenericWidgetInteraction(BooleanSupplier acceptCondition, Filter<WidgetChild> widgetChildFilter) {
        super(acceptCondition);
        this.widgetChildSupplier = () -> Widgets.get(widgetChildFilter);
    }

    /**
     * defaults accept condition to interact if widget is present
     *
     * @param widgetChildSupplier supplier for widget child
     */
    public GenericWidgetInteraction(Supplier<WidgetChild> widgetChildSupplier) {
        super(() -> {
            WidgetChild wc = widgetChildSupplier.get();
            return wc != null && wc.isVisible();
        });
        this.widgetChildSupplier = widgetChildSupplier;
    }

    /**
     * defaults accept condition to interact if widget is present, filter to save characters
     *
     * @param widgetChildFilter supplier for widget child
     */
    public GenericWidgetInteraction(Filter<WidgetChild> widgetChildFilter) {
        super(() -> {
            WidgetChild wc = Widgets.get(widgetChildFilter);
            return wc != null && wc.isVisible();
        });
        this.widgetChildSupplier = () -> Widgets.get(widgetChildFilter);
    }

    @Override
    protected int onLoop() {
        WidgetChild wc = widgetChildSupplier.get();
        if (wc != null) {
            log("Found wc " + wc.getName() + " " + wc.getWidgetId() + "  " + Arrays.toString(wc.getActions()));
            if (action != null) {
                log("Interacting : " + action);
                wc.interact(action);
            }
            log("Interacting any action");
            wc.interact();
        }

        return sleep();
    }
}
