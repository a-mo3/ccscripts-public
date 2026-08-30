package org.dreambot.settings.ui;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.dreambot.api.Client;
import org.dreambot.api.script.listener.HumanMouseListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

@Setter
@Accessors(chain = true)
public class PaintButton implements HumanMouseListener {
    int trX = 513;
    int trY = 5;
    int height = 30;
    int width = 100;
    String label = "";
    Consumer<MouseEvent> onClick;
    Color borderColor = Color.WHITE;

    public PaintButton() {
        Client.getInstance().addEventListener(this);
    }

    public void paintButton(Graphics g) {
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(trX, trY, width, height);
        g.setColor(borderColor);
        g.drawRect(trX, trY, width, height);
        g.drawString(label, trX + 10, trY + 20);
    }

    public boolean wasButtonClicked(Point point) {
        boolean clickedX = point.x >= trX && point.x <= trX + width;
        boolean clickedY = point.y >= trY && point.y <= trY + height;
        return clickedY && clickedX;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (wasButtonClicked(e.getPoint())) onClick.accept(e);
    }
}
