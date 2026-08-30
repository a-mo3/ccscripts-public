package org.dreambot.alerts;

import org.dreambot.api.Client;
import org.dreambot.api.utilities.Timer;

import java.awt.*;

public class AlertModel {
    final Timer expiry;
    final Color color;
    Color textColor = Color.BLACK;
    final String message;

    public AlertModel(int expiryMS, Color color, String message) {
        this.expiry = new Timer(expiryMS);
        this.color = color;
        this.message = message;
    }

    public void render(Graphics g, int x, int width, int y) {
        int textLength = g.getFontMetrics().stringWidth(message);
        int center = Client.getViewportWidth() / 2;

        g.setColor(color);
        g.fillRect(x / 2, y - 15, width, 20);

        g.setColor(Color.black);
        g.drawRect(x / 2, y - 15, width, 20);
        // todo center text into rect
        g.setColor(textColor);
        g.drawString(message, center - (textLength / 2), y);
    }

    public boolean isFinished() {
        return expiry.finished();
    }
}
