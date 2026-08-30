package org.dreambot.utility;

import java.awt.*;

/**
 * no fluffees paint its so verbose
 */
public class PaintUtil {
    static FontMetrics metrics = null;


    public static void paint(Graphics g, String[] text) {
        int xCord = 20;
        int yCord = 20;
        if (metrics == null) metrics = g.getFontMetrics();
        for (String s : text) {
            int strWdth = metrics.stringWidth(s);
            g.setColor(Color.BLACK);
            g.fillRect(xCord-5, yCord-13, strWdth+10, 20);
            g.setColor(Color.WHITE);
            g.drawString(s, xCord, yCord);
            yCord += 30;
        }
    }
}
