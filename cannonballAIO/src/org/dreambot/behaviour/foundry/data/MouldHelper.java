package org.dreambot.behaviour.foundry.data;

import org.dreambot.api.input.Mouse;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class MouldHelper {
    static final int MOULD_LIST_PARENT = 47054857;
    static final int DRAW_MOULD_LIST_SCRIPT = 6093;
    static final int REDRAW_MOULD_LIST_SCRIPT = 6095;
    static final int RESET_MOULD_SCRIPT = 6108;
    public static final int SELECT_MOULD_SCRIPT = 6098;
    static final int SWORD_TYPE_1_VARBIT = 13907; // 4=Broad
    static final int SWORD_TYPE_2_VARBIT = 13908; // 3=Flat
    private static final int DISABLED_TEXT_COLOR = 0x9f9f9f;
    private static final int GREEN = 0xdc10d;
    private static boolean scrollFlag;

    public static void selectBest() {
        WidgetChild mouldParent = Widgets.get(718, 9);
        if (mouldParent == null) return;
        if (mouldParent.getChildren() == null || !mouldParent.isVisible()) {
            Logger.info("parent was null");
            return;
        }
        int bestScore = -1;
        WidgetChild bestWidget = null;
        CommissionType type1 = CommissionType.forVarbit(PlayerSettings.getBitValue(SWORD_TYPE_1_VARBIT));
        CommissionType type2 = CommissionType.forVarbit(PlayerSettings.getBitValue(SWORD_TYPE_2_VARBIT));
        Map<Mould, WidgetChild> mouldTOChild = getOptions(mouldParent.getChildren());
        // loop through map, find the best widget, click it
        for (Map.Entry<Mould, WidgetChild> entry : mouldTOChild.entrySet()) {
            Logger.info("[Start select best]" + entry.getKey() + " " + entry.getValue());
            Mould mould = entry.getKey();
            int score = mould.getScore(type1, type2);
            if (score > bestScore) {
                bestScore = score;
                bestWidget = entry.getValue();
            }
        }
        Logger.info("[BEST WIDGET] - " + bestWidget + " Score: " + bestScore);
        WidgetChild screen = Widgets.get(718, 9);
        if (bestWidget == null || screen == null) {
            Logger.info("Best widget or screen was null");
            return;
        }

        if (!screen.getRectangle().contains(bestWidget.getRectangle().getLocation())) {
            Logger.info("Scrolling");
            Point p = screen.getRectangle().getLocation();
            p.translate(50, 50);
            Mouse.hop(p);

            Mouse.scroll(scrollFlag, 100, () -> false);
            scrollFlag = !scrollFlag;
            return;
        }

        if (bestWidget != null) {
            WidgetChild finalBestWidget = bestWidget;
            // todo get real name replacement
//            Logger.info(finalBestWidget.getText());
//            WidgetChild interactable = Widgets.get(w -> w.getText().equals(finalBestWidget.getText())
//                    && w.hasAction("Select"));
            finalBestWidget.interact();
//            Logger.info("[INTERACTABLE WIDGET] - " + interactable);
//            if (interactable != null) {
//                interactable.interact("Select");
//            }
        }
    }

    private static Map<Mould, WidgetChild> getOptions(WidgetChild[] children) {
        Map<Mould, WidgetChild> mouldToChild = new LinkedHashMap<>();
        for (int i = 2; i < children.length; i += 17) {
            WidgetChild child = children[i];
//            Logger.info("[GET OPTIONS] - " + child);
            Mould mould = Mould.forName(child.getText());
            if (mould != null && child.getTextColor() != DISABLED_TEXT_COLOR) {
                mouldToChild.put(mould, child);
            }
        }
        return mouldToChild;
    }
}
