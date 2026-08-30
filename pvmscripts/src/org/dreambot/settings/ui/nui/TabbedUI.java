package org.dreambot.settings.ui.nui;

import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.utilities.Logger;
import org.dreambot.settings.ui.GuiOption;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;

public class TabbedUI extends JFrame {
    //    HashMap<String, TabbedTreeNode> childrenNodes = new HashMap<>();
    TabbedTreeNode root = null;
    JPanel rootPanel = null;

    public TabbedUI(Object dataClass) {
        // examine data class and make a tree of tabbed panes to make the ui tab annotations
        // it is expected that a tab that contains sub tabs will not contain other settings.
        Field[] fields = dataClass.getClass().getDeclaredFields();

        // construct the tree of tabbed panels
        for (Field field : fields) {
            UITab tab = field.getAnnotation(UITab.class);
            if (tab == null) {
                // trust
                if (root != null) this.setLayout(new GridLayout(0, 2));
                Logger.info("Root panel null");
                if (rootPanel == null) {
                    // for tabless
                    rootPanel = new JPanel();
                    rootPanel.setLayout(new GridLayout(0, 2));
                }
                rootPanel.add(new GuiOption(field, dataClass));
                continue;
            }
            // trust
            if (rootPanel != null) this.setLayout(new GridLayout(0, 2));
            if (root == null) root = new TabbedTreeNode();

            String[] tabs = tab.value();
//            HashMap<String, TabbedTreeNode> tempTree = null;
            TabbedTreeNode tempTreeNode = null;
            Iterator<String> tabPathIterator = Arrays.stream(tabs).iterator();
            while (tabPathIterator.hasNext()) {
                // first iteration, get from root (this children)
                String tabName = tabPathIterator.next();
                if (tempTreeNode == null) {
                    Logger.info("temp tree null first iteration");
                    if (root.getChild(tabName) == null) {
                        TabbedTreeNode node = new TabbedTreeNode();
                        root.addChild(tabName, node);
                        tempTreeNode = node;
                    } else {
                        tempTreeNode = root.getChild(tabName);
                    }

                    if (!tabPathIterator.hasNext()) {
                        // child node, add the GuiOption for the field we are checking
                        Logger.info("Add ui option to root");
                        tempTreeNode.setLayout(new GridLayout(0, 2));
                        tempTreeNode.add(new GuiOption(field, dataClass));
                    }
                    continue;
                }

                if (tempTreeNode.getChild(tabName) == null) {
                    TabbedTreeNode node = new TabbedTreeNode();
                    tempTreeNode.addChild(tabName, node);
                    tempTreeNode = node;
                } else {
                    tempTreeNode = tempTreeNode.getChild(tabName);
                }

                if (!tabPathIterator.hasNext()) {
                    // child node, add the GuiOption for the field we are checking
                    tempTreeNode.setLayout(new GridLayout(0, 2));
                    tempTreeNode.add(new GuiOption(field, dataClass));
                }
            }
        }

        Logger.info("tabbed ui appear!");
        setSize(600, 500);
        setTitle(ScriptManager.getScriptManager().getCurrentScript().getSDNName());
        if (rootPanel != null) this.add(rootPanel);
        if (root != null) this.add(root);
        SwingUtilities.updateComponentTreeUI(this);
        this.setVisible(true);
    }
}
