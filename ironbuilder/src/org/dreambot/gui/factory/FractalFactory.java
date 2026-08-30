package org.dreambot.gui.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.SettingsUtil;
import org.dreambot.gui.settings.SettingsRepository;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

/**
 * Fractal that has a UI tab
 */
public class FractalFactory extends JPaneFractal {
    // used to clone when pressing add new
    FractalDTO defaultDTOInstance;
    final String saveName;

    public FractalFactory(FractalDTO instance, String savePath) {
        super(() -> false);
        this.saveName = savePath;
        // the excepted children are 1-X level, if the last child is valid we still have levels to level up
        this.acceptCondition = () -> this.children != null && !this.children.isEmpty() && this.children.get(children.size() - 1).isValid();
        this.defaultDTOInstance = instance;
        // we need to load settings on init or else you have to open the UI before the fractals start
        try {
            log("Loading defaults");
            for (FractalDTO child : getDefaults()) {
                addChildren(child.toFractal());
            }
        } catch (IOException e) {
            log("Failed to fetch defaults " + e.getMessage());
        }
    }

    ReorderHandler handler;

    // think array will serialize properly by default
    protected FractalDTO[] getDefaults() throws IOException {
        log("load");
        Path path = Path.of(SettingsRepository.scriptPath + "/" + saveName);
        log("Load " + path.toString());

        if (!Files.exists(path)) {
            log("File does not exist: " + path.toAbsolutePath());
        }

        if (!Files.isRegularFile(path)) {
            log("Path is not a regular file: " + path.toAbsolutePath());
        }

        if (!Files.isReadable(path)) {
            log("File is not readable: " + path.toAbsolutePath());
        }

        if (Files.size(path) == 0) {
            log("File is empty: " + path.toAbsolutePath());
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            FractalDTO[] result = (FractalDTO[]) new GsonBuilder().setPrettyPrinting().create().fromJson(reader,
                    Array.newInstance(defaultDTOInstance.getClass(), 1).getClass());

            if (result == null) {
                throw new IOException("JSON content is null or invalid for FractalDTO[]: " + path.toAbsolutePath());
            }

            return result;
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON format in file: " + path.toAbsolutePath(), e);
        } catch (JsonIOException e) {
            log(e.getMessage());
            throw new IOException("Failed to read JSON file: " + path.toAbsolutePath(), e);
        }
    }

    /**
     * @return panel that contains a list of this fractals children, that can be moved up or down
     */
    private JPanel makeChildrenPanel() {
        JPanel panel = new JPanel();

        // todo this should be a generic class for a type of the fractaldto
        log("Make child pane");
        DefaultListModel<FractalDTO> childItems = new DefaultListModel<>();
        try {
            log("Loading defaults");
            for (FractalDTO child : getDefaults()) {
                childItems.addElement(child);
            }
        } catch (IOException e) {
            log("Failed to fetch defaults " + e.getMessage());
        }

        JList<FractalDTO> jList = new JList<>(childItems);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setDragEnabled(true);
        jList.setDropMode(DropMode.INSERT);
        handler = new ReorderHandler(jList, this, childItems);
        handler.syncArrayFromModel();
        jList.setTransferHandler(handler);
        jList.addListSelectionListener(a -> {
            if (jList.getSelectedIndex() < 0) return;
            log("Selection event " + a.getFirstIndex() + " " + a.getLastIndex() + " " + jList.getSelectedIndex());

            FractalDTO dto = childItems.get(jList.getSelectedIndex());
            log("Select " + dto.toString());
            parentPane.remove(2);
            Field[] fa = dto.getClass().getFields();
            JPanel cPanel = SettingsUtil.makeSettingsList(fa, dto, v -> handler.syncArrayFromModel());
            cPanel.setBorder(LineBorder.createGrayLineBorder());
            parentPane.add(cPanel, 2);
            handler.syncArrayFromModel();
            SwingUtilities.updateComponentTreeUI(parentPane);
        });

        panel.add(new JScrollPane(jList), BorderLayout.CENTER);
        panel.setBorder(LineBorder.createGrayLineBorder());
        log("Panel");
        return panel;
    }


    private JPanel makeConstructionPanel() {
        // this panel should contain all the fields in a DTO and then a add new or edit current button
        // edit current should replace the selected childs (in the children pane) with a new fractal from this dto
        JPanel constructionPanel = new JPanel();
        constructionPanel.setBorder(LineBorder.createGrayLineBorder());
        return constructionPanel;
    }

    JPanel parentPane;

    @Override
    public JPanel makePane() {
        parentPane = new JPanel();
        BoxLayout l = new BoxLayout(parentPane, BoxLayout.PAGE_AXIS);
        parentPane.setLayout(l);

        parentPane.add(makeChildrenPanel());
        parentPane.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical gap
        parentPane.add(makeConstructionPanel());

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        JButton addButton = new JButton("Add new task");
        addButton.addActionListener(a -> {
            handler.addNew(defaultDTOInstance.getInstance());
        });
        buttons.add(addButton);

        JButton deleteButton = new JButton("Delete selected task");
        deleteButton.addActionListener(a -> {
            log("Delete sel index ");
            handler.removeSelected();
        });
        buttons.add(deleteButton);

        JButton saveButton = new JButton("Save task order");
        saveButton.addActionListener(a -> {
            try {
                log("Trying to save factory");
                saveToFile();
            } catch (IOException e) {
                log("Failed to save factory " + e);
            }
        });
        buttons.add(saveButton);
        buttons.setBorder(LineBorder.createGrayLineBorder());


        parentPane.add(Box.createRigidArea(new Dimension(0, 10))); // 10px vertical gap
        parentPane.add(buttons);
        return parentPane;
    }

    private void saveToFile() throws IOException {
        Path path = Path.of(SettingsRepository.scriptPath + "/" + saveName);
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        if (Files.exists(path) && !Files.isWritable(path)) {
            throw new IOException("File is not writable: " + path.toAbsolutePath());
        }

        try (Writer writer = Files.newBufferedWriter(path)) {
            new Gson().toJson(handler.getModel().toArray(), FractalDTO[].class, writer);
        } catch (JsonIOException e) {
            throw new IOException("Failed to write JSON file: " + path.toAbsolutePath(), e);
        }
    }

    @Override
    protected int onLoop() {
        log("Factory onloop " + this.children.size());
        return super.onLoop();
    }
}
