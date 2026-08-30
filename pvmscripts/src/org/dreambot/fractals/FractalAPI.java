package org.dreambot.fractals;

import org.dreambot.fractals.paint.PaintInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FractalAPI implements PaintInfo {
    public static List<String> hierarchy = new ArrayList<>();
    public static String status = "";
    public static Supplier<String[]> paintArrSupplier = () -> new String[]{};

    @Override
    public String[] getPaintInfo() {
        return paintArrSupplier.get();
    }
}