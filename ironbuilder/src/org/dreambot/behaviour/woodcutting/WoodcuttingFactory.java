package org.dreambot.behaviour.woodcutting;

import org.dreambot.gui.FractalDTO;
import org.dreambot.gui.factory.FractalFactory;

import java.util.List;
import java.util.function.BooleanSupplier;

public class WoodcuttingFactory extends FractalFactory {
    public WoodcuttingFactory(FractalDTO instance, String savePath) {
        super(instance, savePath);
    }
}
