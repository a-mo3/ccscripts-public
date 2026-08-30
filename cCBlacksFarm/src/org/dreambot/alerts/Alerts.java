package org.dreambot.alerts;

import org.dreambot.api.Client;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Alerts {
    public static final List<AlertModel> alertModelList = new ArrayList<>();

    public static void addAlert(int expiry, Color c, String message) {
        alertModelList.add(new AlertModel(expiry, c, message));
    }

    public static void renderList(Graphics g) {
        // todo empty expired alerts
        alertModelList.removeIf(AlertModel::isFinished);

        int viewportBase = Client.getViewportHeight();
        int viewportBaseOffset = 20;
        int width = (int) (Client.getViewportWidth() * 0.75f);
        int xRenderPoint = (int) (Client.getViewportWidth() * 0.25f);

        for (AlertModel alertModel : alertModelList) {
            alertModel.render(g, xRenderPoint, width, viewportBase - viewportBaseOffset);
            viewportBaseOffset += 25;
        }
    }
}
