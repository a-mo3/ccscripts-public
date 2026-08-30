package org.dreambot.behaviour.method.mta.enchant;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public enum EnchantShape {
    YELLOW_CUBE(6899, 10, "Cube Pile"),
    GREEN_CYLINDER(6898, 12, "Cylinder Pile"),
    RED_PENTAMID(6901, 14, "Pentamid Pile"),
    BLUE_ICOSAHEDRON(6900, 16, "Icosahedron Pile"),
    ;

    final int itemID;
    final int widgetChild;
    final String objName;
    private static int WIDGET_PARENT = 195;

    EnchantShape(int itemID, int widgetCild, String objName) {
        this.itemID = itemID;
        this.widgetChild = widgetCild;
        this.objName = objName;
    }

    public boolean isBonus() {
        WidgetChild wc = Widgets.get(WIDGET_PARENT, widgetChild);
        return wc != null && wc.isVisible();
    }

    public GameObject getNearest() {
        return GameObjects.closest(objName);
    }
}
