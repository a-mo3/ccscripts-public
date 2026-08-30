package org.dreambot.antiban;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Timer;
import org.dreambot.fractals.Fractal;
import org.dreambot.settings.fractalsettings.ConfigurableFractal;
import org.dreambot.settings.fractalsettings.SettingsRepository;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AntibanFractal extends Fractal implements ConfigurableFractal<AntibanSettings> {
    static boolean[] breaks;
    static Timer sinceStarted = new Timer();

    public AntibanFractal() {
        // why im not just getting one antiban settings object i dont know but i operate on intuition
        AntibanSettings settings = getSettings();
        Antiban.enabled = settings.enabled;
        Antiban.mouseOffChance = settings.mouseOffChance;
        Fractal.antibanSettings = settings;

        // calculate breaking times for this session
//        if (!settings.autoBreaks) return;
//        int sessionLength = Calculations.random(
//                Math.min(settings.minDailyBottingTime, settings.maxDailyBottingTime),
//                Math.max(settings.minDailyBottingTime, settings.maxDailyBottingTime)
//        ) * 6; // * 6 = 10 min increments
//        log("Setting up automatic breaks for a play session of length " + sessionLength);
//
//        if (settings.maxTimeBreaking <= 0) {
//            log("You have no max break time");
//            // todo something for still enforcing the session
//            return;
//        }
//
//        int totalBreakingTime = Calculations.random(
//                Math.min(settings.minTimeBreaking, settings.maxTimeBreaking),
//                Math.max(settings.minTimeBreaking, settings.maxTimeBreaking)
//        ) * 6;
//        log("Decided to take time breaking " + totalBreakingTime);
//        breaks = new boolean[sessionLength];
//        ArrayList<Integer> placedIndexes = new ArrayList<>();
//
//        int first = Calculations.random(totalBreakingTime);
//        breaks[first] = true;
//        placedIndexes.add(first);
//
//        log("Setting breaks " + System.currentTimeMillis());
//        while (placedIndexes.size() < totalBreakingTime) {
//            int idx;
//            if (Calculations.chance(settings.clustering)) {
//                int base = placedIndexes.get(Calculations.random(placedIndexes.size()));
//                idx = base + 1;
//                if (idx >= breaks.length) idx = Calculations.random(breaks.length);
//            } else {
//                idx = Calculations.random(breaks.length);
//            }
//
//            if (idx >= 0 && idx < sessionLength && !breaks[idx]) {
//                breaks[idx] = true;
//                placedIndexes.add(idx);
//            }
//        }
//        log("Breaks made " + System.currentTimeMillis());
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public AntibanSettings getSettings() {
        return SettingsRepository.getSetting(settingName(), new AntibanSettings());
    }

    @Override
    public String settingName() {
        return "Antiban";
    }

    static BreakBar bar;

    public static void paintBreaks(Graphics2D g) {
        if (breaks == null) return;
        if (bar == null) {
            Logger.info("Making bar");
            Logger.info(Arrays.toString(breaks));
            bar = new BreakBar(breaks, 10, 10, 500, 10);
        }
        int index = Math.toIntExact((sinceStarted.elapsed() / 600_000) % breaks.length);
        bar.draw(g, index);
    }
}
