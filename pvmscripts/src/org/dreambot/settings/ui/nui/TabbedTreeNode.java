package org.dreambot.settings.ui.nui;

import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.util.HashMap;

/**
 * this should either contain another tabbed pane or just settings
 */
public class TabbedTreeNode extends JPanel {
    private JTabbedPane childPane = null;
    private HashMap<String, TabbedTreeNode> childrenNodes = new HashMap<>();

//    public TabbedTreeNode() {
//        super();
//        this.setLayout(new GridLayout(0, 2));
//    }

    TabbedTreeNode getChild(String name) {
        return childrenNodes.get(name);
    }

    TabbedTreeNode addChild(String name, TabbedTreeNode child) {
        Logger.info("Add tab " + name);
        if (childPane == null) {
            childPane = new JTabbedPane();
            this.add(childPane);
        }

        childPane.addTab(name, child);
        childrenNodes.put(name, child);
        return this;
    }
}
