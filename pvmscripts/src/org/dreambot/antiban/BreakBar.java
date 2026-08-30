package org.dreambot.antiban;

import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;

import java.awt.*;
import java.util.ArrayList;

public class BreakBar {

    private final ArrayList<Rectangle> workRects = new ArrayList<>();
    private final ArrayList<Rectangle> breakRects = new ArrayList<>();

    private final int x;
    private int y;
    private final int width;
    private final int height;

    private final int slotWidth;

    public BreakBar(boolean[] breaks, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.height = height;

        this.slotWidth = width / breaks.length;
        this.width = slotWidth * breaks.length;

        Logger.info("Slot w " + slotWidth + " width " + width + " break size " + breaks.length);
        parseOnce(breaks);
    }

    private void parseOnce(boolean[] breaks) {
        int start = 0;
        boolean current = breaks[0];

        for (int i = 1; i <= breaks.length; i++) {
            boolean changed = i == breaks.length || breaks[i] != current;

            if (changed) {
                int rectX = x + start * slotWidth;
                int rectW = (i - start) * slotWidth;

                Rectangle rect = new Rectangle(rectX, y, rectW, height);

                if (current) {
                    breakRects.add(rect); // red
                } else {
                    workRects.add(rect);  // green
                }

                if (i < breaks.length) {
                    start = i;
                    current = breaks[i];
                }
            }
        }
    }

    public void draw(Graphics2D g, int index) {
        g.setColor(Color.GREEN);
        int y = Client.getViewportHeight() - 220;
        for (Rectangle r : workRects) {
            g.fillRect(r.x, y, r.width, r.height);
        }

        g.setColor(Color.RED);
        for (Rectangle r : breakRects) {
            g.fillRect(r.x, y, r.width, r.height);
        }

        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);

        drawArrow(g, index);
    }

    private void drawArrow(Graphics2D g, int index) {
        int arrowX = x + index * slotWidth + slotWidth / 2;

        int y = Client.getViewportHeight() - 222;
        int tipY = y - 2;
        int baseY = y - 18;

        g.setColor(Color.CYAN);

        g.drawLine(arrowX, baseY, arrowX, tipY);

        g.fillPolygon(
                new int[]{arrowX - 5, arrowX + 5, arrowX},
                new int[]{tipY - 7, tipY - 7, tipY},
                3
        );
    }
}