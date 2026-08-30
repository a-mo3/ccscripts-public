package org.dreambot;

import org.dreambot.api.Client;
import org.dreambot.api.input.Keyboard;
import org.dreambot.api.script.listener.HumanMouseListener;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.function.Supplier;

public class CamelPaint implements HumanMouseListener {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CamelPaint.class);
    private BufferedImage cCPaint = null;
    // these could be overridden with some string suppliers to allow for the paint to show xp/hr while training
    String scriptName;
    Supplier<Timer> runtimeSupplier;
    Supplier<Integer> gpMadeSupplier;
    Supplier<Long> timeUntilMuleOffSupplier;
    Supplier<Object> guiSupplier;


    public CamelPaint(String scriptName, Supplier<Timer> runtimeSupplier, Supplier<Integer> gpMadeSupplier, Supplier<Long> timeUntilMuleOffSupplier, Supplier<Object> guiSupplier) {
        this.scriptName = scriptName;
        this.runtimeSupplier = runtimeSupplier;
        this.gpMadeSupplier = gpMadeSupplier;
        this.timeUntilMuleOffSupplier = timeUntilMuleOffSupplier;
        this.guiSupplier = guiSupplier;

//        try {
//            Logger.info("Reading image");
//            cCPaint = ImageIO.read(new URL("https://i.imgur.com/w9DbLuL.png").openStream());
//            Logger.info("Read img " + cCPaint);
//        } catch (IOException e) {
//            Logger.error("Failed to load paint image");
//        }
        Client.getInstance().addEventListener(this);
    }

    Color DISCORD_LIGHT_GRAY = new Color(66, 69, 73);
    Color DISCORD_LIGHT_GRAY_OPAQUE = new Color(66, 69, 73, 127);
    Color DISCORD_BLUE = new Color(114, 137, 218);
    boolean hidden = false;

    private Color tOrange = makeTransparent(Color.ORANGE);
    private Color tWhite = makeTransparent(Color.GREEN);
    private Color tGreen = makeTransparent(Color.RED);

    private static Color makeTransparent(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
    }

    public void paint(Graphics g) {
        int yBase = Client.getViewportHeight() - 165;
        if (!hidden) {
            if (cCPaint != null) {
                g.drawImage(cCPaint, 0, yBase, null);
            } else {
                g.setColor(DISCORD_LIGHT_GRAY_OPAQUE);
                g.fillRect(0, yBase, 520, 140);
                g.setColor(Color.GREEN);
                Font f = g.getFont();
                g.setFont(new Font("Monospaced", Font.BOLD, 14));

                String[] camel = {
                        "                 ,,__",
                        "        ..  ..   / o._)",
                        "       /--'/--\\  \\-'||",
                        "      /        \\_/ / |",
                        "    .-\\  \\__\\  __.'.'",
                        "   /   \\__\\  \\__\\",
                        "  /____/  /____/"
                };

                FontMetrics fm = g.getFontMetrics();
                int x = 35;
                int y = yBase ;

                for (String line : camel) {
                    g.drawString(line, x, y);
                    y += fm.getHeight();
                }
                g.setFont(f);

//                g.setColor(tOrange);
//                g.fillRect(0, yBase, 520, 45);
//
//                g.setColor(tWhite);
//                g.fillRect(0, yBase + 45, 520, 45);
//
//                g.setColor(tGreen);
//                g.fillRect(0, yBase + 90, 520, 45);

//                g.setColor(Color.BLUE);
//                g.fillOval(235, yBase + 60, 10, 10);
//                g.drawOval(225, yBase + 50, 30, 30);
            }
        }
        // draw buttons
        g.setFont(new Font(g.getFont().getName(), Font.BOLD, 16));
        g.setColor(DISCORD_LIGHT_GRAY_OPAQUE);
        g.fillRect(0, yBase - 26, 520, 26);

        g.setColor(Color.WHITE);
        g.drawString(scriptName, 0, yBase - 10);

        g.setColor(DISCORD_BLUE); // discord blue
        g.drawString("Join Discord", 200, yBase - 10);

        g.setColor(Color.WHITE);
        g.drawString("Open Settings", 400, yBase - 10);

        g.setColor(DISCORD_LIGHT_GRAY_OPAQUE);
        g.fillRect(0, yBase + 145, 115, 60);
        g.setColor(Color.WHITE);
        g.drawString("Toggle img", 0, yBase + 155);
        // draw all the script stats
        boolean validRuntime = runtimeSupplier != null && runtimeSupplier.get() != null;
        if (validRuntime) {
            g.drawString(runtimeSupplier.get().formatTime(), 40, yBase + 40);
        }

        boolean validGPMade = gpMadeSupplier != null && gpMadeSupplier.get() != null;
        if (validGPMade) {
            // todo format this to eg. 100K, 100M
            g.drawString(formatNumber(gpMadeSupplier.get()), 340, yBase + 40);
        }

        if (timeUntilMuleOffSupplier != null && timeUntilMuleOffSupplier.get() != null) {
            g.drawString(formatTime(timeUntilMuleOffSupplier.get()), 40, yBase + 100);
        }

        if (validRuntime && validGPMade) {
            g.drawString(formatNumber(runtimeSupplier.get().getHourlyRate(gpMadeSupplier.get())), 340, yBase + 100);
        }

        g.setFont(new Font(g.getFont().getName(), Font.ITALIC, 12));
        g.drawString("Runtime", 35, yBase + 20);
        g.drawString("GP Made", 335, yBase + 20);
        g.drawString("Time until mule off", 35, yBase + 80);
        g.drawString("GP / Hour", 335, yBase + 80);


        g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 18));
        g.drawString("Mouse Minigame!", 180, Client.getViewportHeight() - 50);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int yBase = Client.getViewportHeight() - 165;
        // discord button
        if (wasButtonClicked(e.getPoint(), 180, yBase - 20, 140, 25)) {
            Logger.info("Open discord");
            try {
                Desktop.getDesktop().browse(new URI(""));
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        if (wasButtonClicked(e.getPoint(), 170, Client.getViewportHeight() - 80, 140, 45)) {
            Logger.info("mouse minigame");
            PvmMain.isMouseTraining = true;
            Client.getInstance().setMouseInputEnabled(true);
            return;
        }


        // settings button
        if (wasButtonClicked(e.getPoint(), 380, yBase - 20, 140, 25)) {
            Logger.info("Open settings");
            SwingUtilities.invokeLater(() -> {
                guiSupplier.get();
            });
        }


        int hideButtonYBase = Client.getViewportHeight() - 50;
        if (wasButtonClicked(e.getPoint(), 0, hideButtonYBase, 75, 50)) {
            Logger.info("Hide button clicked");
            hidden = !hidden;
        }

    }

    public static String formatNumber(double number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000).replace(".0M", "M");
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000).replace(".0K", "K");
        } else {
            return String.valueOf((int) number);
        }
    }

    private String formatTime(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        DecimalFormat format = new DecimalFormat("00");
        return String.format("%s:%s:%s",
                format.format(hours),
                format.format(minutes),
                format.format(seconds));
    }

    public static boolean wasButtonClicked(Point clickPoint, int x, int y, int width, int height) {
        boolean clickedX = clickPoint.x >= x && clickPoint.x <= x + width;
        boolean clickedY = clickPoint.y >= y && clickPoint.y <= y + height;
        return clickedY && clickedX;
    }
}
