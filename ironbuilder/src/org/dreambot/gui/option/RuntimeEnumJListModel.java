package org.dreambot.gui.option;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RuntimeEnumJListModel extends JPanel {
    @Getter
    private final Class<? extends Enum<?>> enumClass;
    private final Enum<?>[] allConstants;
    private Enum<?>[] selectedValues;

    private final DefaultListModel<String> listModel;
    private final JList<String> jList;
    Consumer<Enum<?>[]> setField;

    public RuntimeEnumJListModel(Enum<?>[] initialSelection, Consumer<Enum<?>[]> setField) {
        this.setField = setField;
        if (initialSelection == null) {
            throw new IllegalArgumentException("initialSelection cannot be null");
        }

        Class<?> componentType = initialSelection.getClass().getComponentType();
        if (componentType == null || !componentType.isEnum()) {
            throw new IllegalArgumentException("Must be an enum array");
        }

        @SuppressWarnings("unchecked")
        Class<? extends Enum<?>> c = (Class<? extends Enum<?>>) componentType;
        this.enumClass = c;
        this.allConstants = enumClass.getEnumConstants();
        this.selectedValues = Arrays.copyOf(initialSelection, initialSelection.length);

        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        for (Enum<?> e : allConstants) {
            listModel.addElement(e.name());
        }

        jList = new JList<>(listModel);
        jList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }

            @Override
            public void addSelectionInterval(int index0, int index1) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        jList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateSelectedValuesFromJList();
            }
            setField.accept(selectedValues);
        });

        setSelectedValues(initialSelection);
        jList.setVisibleRowCount(5);
        add(new JScrollPane(jList), BorderLayout.CENTER);
    }

    private void updateSelectedValuesFromJList() {
        List<Enum<?>> selected = new ArrayList<>();
        for (String name : jList.getSelectedValuesList()) {
            selected.add(Enum.valueOf((Class) enumClass, name));
        }
        selectedValues = toEnumArray(selected);
    }

    public void setSelectedValues(Enum<?>[] values) {
        jList.clearSelection();
        List<Enum<?>> valueList = Arrays.asList(values);
        for (int i = 0; i < allConstants.length; i++) {
            if (valueList.contains(allConstants[i])) {
                jList.getSelectionModel().addSelectionInterval(i, i);
            }
        }
        selectedValues = Arrays.copyOf(values, values.length);
    }

    private Enum<?>[] toEnumArray(List<Enum<?>> values) {
        Enum<?>[] array = (Enum<?>[]) Array.newInstance(enumClass, values.size());
        return values.toArray(array);
    }
}