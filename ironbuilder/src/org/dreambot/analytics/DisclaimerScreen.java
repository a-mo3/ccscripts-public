package org.dreambot.analytics;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public final class DisclaimerScreen {
    private static final DisclaimerScreen INSTANCE = new DisclaimerScreen();

    private Rectangle acceptButtonBounds;
    private Rectangle nevermindButtonBounds;

    private DisclaimerScreen() {
    }

    public static DisclaimerScreen getInstance() {
        return INSTANCE;
    }

    public void draw(Graphics graphics, int clientWidth, int clientHeight) {
        Graphics2D g = (Graphics2D) graphics.create();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setColor(new Color(25, 25, 25));
            g.fillRect(0, 0, clientWidth, clientHeight);

            int panelWidth = Math.min(620, clientWidth - 60);
            int panelHeight = Math.min(420, clientHeight - 60);
            int panelX = (clientWidth - panelWidth) / 2;
            int panelY = (clientHeight - panelHeight) / 2;

            g.setColor(new Color(245, 245, 245));
            g.fill(new RoundRectangle2D.Double(panelX, panelY, panelWidth, panelHeight, 20, 20));

            g.setColor(new Color(40, 40, 40));
            g.setFont(new Font("SansSerif", Font.BOLD, 26));
            drawCenteredText(g, "Disclaimer", panelX, panelY + 35, panelWidth);

            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            String disclaimer =
                    // English
//                    "This script's purpose is to collect data for public anti-ban tests. See the script thread.\n\n" +
//                            "By selecting Accept, you acknowledge that you understand your account's behaviour will be recorded.\n\n" +
//                            "Personally identifiable information (IP addresses, email addresses, usernames, etc.) is anonymized or removed before being transmitted or released.\n\n" +
//                            "Select Nevermind to turn off the script. No data will be sent.\n\n" +

                            // Chinese
                            "本脚本旨在收集公开防封测试所需的数据，详情请参阅脚本帖子。\n\n" +
                            "点击“Accept”即表示您已了解并同意记录您账号的行为数据。\n\n" +
                            "所有可识别个人身份的信息（如 IP 地址、电子邮箱、用户名等）都会在发送或公开前进行匿名化处理或移除。\n\n" +
                            "点击“Nevermind”将关闭脚本，不会发送任何数据。";
            List<String> lines = wrapText(g, disclaimer, panelWidth - 80);

            FontMetrics fm = g.getFontMetrics();
            int lineHeight = fm.getHeight();
            int textBlockHeight = lines.size() * lineHeight;

            int textY = panelY + 110;
            for (String line : lines) {
                drawCenteredText(g, line, panelX, textY, panelWidth);
                textY += lineHeight;
            }

            int buttonWidth = 140;
            int buttonHeight = 42;
            int buttonGap = 24;
            int buttonsY = panelY + panelHeight - 80;
            int buttonsX = panelX + (panelWidth - buttonWidth * 2 - buttonGap) / 2;

            acceptButtonBounds = new Rectangle(buttonsX, buttonsY, buttonWidth, buttonHeight);
            nevermindButtonBounds = new Rectangle(
                    buttonsX + buttonWidth + buttonGap,
                    buttonsY,
                    buttonWidth,
                    buttonHeight
            );

            drawButton(g, acceptButtonBounds, "Accept", new Color(45, 130, 75));
            drawButton(g, nevermindButtonBounds, "Nevermind", new Color(140, 60, 60));

        } finally {
            g.dispose();
        }
    }

    public boolean isAcceptClicked(Point point) {
        return acceptButtonBounds != null && acceptButtonBounds.contains(point);
    }

    public boolean isNevermindClicked(Point point) {
        return nevermindButtonBounds != null && nevermindButtonBounds.contains(point);
    }

    private void drawButton(Graphics2D g, Rectangle bounds, String text, Color color) {
        g.setColor(color);
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));

        FontMetrics fm = g.getFontMetrics();
        int textX = bounds.x + (bounds.width - fm.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - fm.getHeight()) / 2) + fm.getAscent();

        g.drawString(text, textX, textY);
    }

    private void drawCenteredText(Graphics2D g, String text, int x, int y, int width) {
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        g.drawString(text, textX, y);
    }

    private List<String> wrapText(Graphics2D g, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g.getFontMetrics();

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;

            if (fm.stringWidth(testLine) > maxWidth && line.length() > 0) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }

        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines;
    }


}