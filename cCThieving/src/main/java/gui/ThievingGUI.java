package gui;

import gui.components.DreamFrame;
import gui.components.DreamTabbedPane;

import java.awt.*;

public class ThievingGUI extends DreamFrame {
    public ThievingGUI() {
        super("cCThieving");
        DreamTabbedPane tabbedPane = new DreamTabbedPane();
        tabbedPane.add("Pickpocketing", new PickpocketPanel(this));
        tabbedPane.add("Stalls", new StallPanel(this));
        tabbedPane.add("Chests", new ChestPanel(this));
        setResizable(true);
        setSize(500,400);
        add(tabbedPane, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        ThievingGUI gui = new ThievingGUI();
        gui.setVisible(true);
    }
}
