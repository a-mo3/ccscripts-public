package org.dreambot.gui.factory;

import lombok.Getter;
import org.dreambot.fractals.IronFractal;
import org.dreambot.gui.FractalDTO;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

class ReorderHandler extends TransferHandler {
    private final JList<FractalDTO> list;
    @Getter
    private final DefaultListModel<FractalDTO> model;
    private int fromIndex = -1;
    private final IronFractal fractal;

    ReorderHandler(JList<FractalDTO> list, IronFractal fractal, DefaultListModel<FractalDTO> model) {
        this.list = list;
        this.model = model;
        this.fractal = fractal;

        // we call this instantly to make the children of the fractal factory init from default DTOs
        syncArrayFromModel();
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        fromIndex = list.getSelectedIndex();
        return new StringSelection("");
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.isDrop();
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support) || fromIndex < 0) return false;

        JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
        int toIndex = dropLocation.getIndex();
        System.out.println("To index " + toIndex);
        System.out.println("From index " + fromIndex);

        if (fromIndex == toIndex) return false;

        FractalDTO movedItem = model.get(fromIndex);
        model.remove(fromIndex);

        if (toIndex > fromIndex) {
            toIndex--;
        }

        model.add(toIndex, movedItem);
        list.setSelectedIndex(toIndex);

        syncArrayFromModel();
        return true;
    }

    public void syncArrayFromModel() {
        // todo i need to deregister existing fractal from any listeners otherwise ill cause a mem leak prolly
        fractal.getChildren().clear();
        for (int i = 0; i < model.size(); i++) {
            fractal.getChildren().add(model.get(i).toFractal());
        }
    }

    public void removeSelected() {
        model.remove(list.getSelectedIndex());
        syncArrayFromModel();
    }

    public void addNew(FractalDTO dto) {
        model.addElement(dto);
        syncArrayFromModel();
    }
}