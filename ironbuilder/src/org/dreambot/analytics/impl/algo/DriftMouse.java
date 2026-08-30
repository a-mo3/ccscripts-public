package org.dreambot.analytics.impl.algo; /**
 * DriftMouse - Another Custom Mouse Algorithm
 *
 * Big shoutout to Benland100 (Benjamin J. Land) for the original WindMouse algorithm
 * and to holic for the original DreamBot port that inspired this work.
 * His work on natural mouse movement was the foundation and inspiration for this implementation.
 * Original WindMouse: https://ben.land/post/2021/04/25/windmouse-human-mouse-movement/
 * Original Dreambot Thread : https://dreambot.org/forums/index.php?/topic/21147-windmouse-custom-mouse-movement-algorithm/
 **/

import org.dreambot.api.input.Mouse;
import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;
import org.dreambot.api.methods.ViewportTools;
import org.dreambot.api.methods.input.mouse.MouseSettings;
import org.dreambot.api.utilities.Logger;
import org.dreambot.core.Instance;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Random;

public class DriftMouse implements MouseAlgorithm {

    // ── Tuneable parameters ───────────────────────────────────────────────────

    /** Gravity pulling velocity toward the target direction.          */
    private static final double GRAVITY          = 8.0;
    /** Maximum wind amplitude (pixels). Scales down with distance.    */
    private static final double MAX_WIND         = 3.5;
    /** Braking starts when remaining distance is below this (pixels). */
    private static final double BRAKE_DIST       = 22.0;
    /** Probability of overshoot per move (only on dist > 80 px).      */
    private static final double OVERSHOOT_CHANCE = 0.30;
    /** Max overshoot radius in pixels.                                */
    private static final double OVERSHOOT_MAX    = 14.0;
    /** Probability of a mid-move micro-pause per step.                */
    private static final double PAUSE_CHANCE     = 0.003;

    // ── Internal state ────────────────────────────────────────────────────────

    private final Random rng = new Random();
    /** Slowly drifts 0-1, causing slight speed/precision degradation. */
    private double fatigue = 0.0;

    // ─────────────────────────────────────────────────────────────────────────
    //  MouseAlgorithm interface
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public boolean handleMovement(AbstractMouseDestination dest) {
        Point target = dest.getSuitablePoint();
        humanMove(target);
        return euclidean(Mouse.getPosition(), target) < 3;
    }

    @Override
    public boolean handleClick(MouseButton btn) {
        sleep(gaussianInt(55, 30, 8, 180));
        boolean ok = Mouse.getDefaultMouseAlgorithm().handleClick(btn);
        sleep(gaussianInt(35, 20, 5, 120));
        return ok;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Public entry point
    // ─────────────────────────────────────────────────────────────────────────

    public void humanMove(Point target) {
        Point from = Mouse.getPosition();
        double dist = euclidean(from, target);
        if (dist < 1) return;

        // Drift fatigue
        fatigue = clamp(fatigue + rng.nextDouble() * 0.04 - 0.01, 0.0, 1.0);

        // Pixels-per-second budget derived from DreamBot speed setting
        double pxPerSec = speedFromSettings();

        if (dist <= 80) {
            // ── Short move: straight smooth glide, no wind ────────────────
            smoothGlide(from.x, from.y, target.x, target.y, pxPerSec * 0.8);
        } else {
            // ── Long move: optional Bezier arc + wind simulation ──────────
            if (rng.nextDouble() < 0.72) {
                Point ctrl = arcControl(from, target, dist);
                Point mid  = sampleBezier(from, ctrl, target,
                        0.40 + rng.nextDouble() * 0.20);
                windGlide(from.x, from.y, mid.x, mid.y,
                        GRAVITY * 0.75, MAX_WIND * 1.2,
                        pxPerSec * 0.9, BRAKE_DIST * 1.4);
                sleep(gaussianInt(10, 6, 0, 35));
                from = Mouse.getPosition();
            }

            // Optional reaction pause
            if (rng.nextDouble() < 0.07)
                sleep(gaussianInt(160, 80, 50, 400));

            // Main leg to target
            windGlide(from.x, from.y, target.x, target.y,
                    GRAVITY, MAX_WIND, pxPerSec, BRAKE_DIST);

            // Overshoot + correction
            if (rng.nextDouble() < OVERSHOOT_CHANCE) {
                Point over = overshootPoint(target);
                sleep(gaussianInt(18, 9, 5, 50));
                smoothGlide(Mouse.getPosition().x, Mouse.getPosition().y,
                        over.x, over.y, pxPerSec * 0.55);
                sleep(gaussianInt(35, 18, 10, 90));
                smoothGlide(Mouse.getPosition().x, Mouse.getPosition().y,
                        target.x, target.y, pxPerSec * 0.45);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Smooth glide  (no wind — used for short moves and corrections)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Moves from (xs,ys) to (xe,ye) in a straight line with a smooth
     * ease-in / ease-out speed profile. No wind, no jitter.
     */
    private void smoothGlide(double xs, double ys, double xe, double ye,
                             double pxPerSec) {
        double totalDist = Math.hypot(xe - xs, ye - ys);
        if (totalDist < 1) return;

        // Step size: 2-6 px keeps movement visibly smooth on screen
        double stepPx = clamp(totalDist / 12.0, 2.0, 6.0);

        double cx = xs, cy = ys;
        long deadline = System.currentTimeMillis() + 8_000;

        while (Math.hypot(cx - xe, cy - ye) > 1.0) {
            if (System.currentTimeMillis() > deadline) break;

            double remaining = Math.hypot(cx - xe, cy - ye);
            double progress  = clamp(1.0 - remaining / totalDist, 0.0, 1.0);
            double mul       = speedProfile(progress);

            double step = Math.min(stepPx * mul, remaining);
            double nx   = (xe - cx) / remaining;
            double ny   = (ye - cy) / remaining;

            double lastX = cx, lastY = cy;
            cx += nx * step;
            cy += ny * step;

            if ((int) Math.round(cx) != (int) Math.round(lastX) ||
                    (int) Math.round(cy) != (int) Math.round(lastY)) {
                setMousePosition(new Point((int) Math.round(cx), (int) Math.round(cy)));
            }

            sleep(delayForStep(step, pxPerSec * mul));
        }

        setMousePosition(new Point((int) Math.round(xe), (int) Math.round(ye)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Wind glide  (wind + gravity — used for long moves)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Moves from (xs,ys) to (xe,ye) using a wind + gravity velocity model.
     */
    private void windGlide(double xs, double ys, double xe, double ye,
                           double gravity, double wind,
                           double pxPerSec, double brakeRadius) {

        final double sqrt2 = Math.sqrt(2);
        final double sqrt3 = Math.sqrt(3);
        final double sqrt5 = Math.sqrt(5);

        double totalDist = Math.hypot(xe - xs, ye - ys);
        if (totalDist < 1) return;

        // Wind amplitude scales with distance so short legs stay calm
        double windScale = clamp(totalDist / 250.0, 0.0, 1.0);
        double w = wind * windScale;

        // Base step budget — larger for long moves, smaller near target
        double maxStepBase = clamp(totalDist / 10.0, 4.0, 32.0);

        double veloX = 0, veloY = 0;
        double windX = 0, windY = 0;

        long deadline = System.currentTimeMillis() + 12_000;

        while (Math.hypot(xs - xe, ys - ye) >= 1.0) {
            if (System.currentTimeMillis() > deadline) break;

            double dist     = Math.hypot(xs - xe, ys - ye);
            double progress = clamp(1.0 - dist / totalDist, 0.0, 1.0);
            double mul      = speedProfile(progress);

            // ── Wind: Gaussian drift with low-pass decay + hard magnitude cap ─
            // The cap is critical — without it wind can accumulate enough to
            // overpower gravity and send the cursor into an orbit.
            if (dist >= brakeRadius) {
                double wAmp = Math.min(w, dist);
                windX = windX / sqrt3 + gauss(0, wAmp / sqrt5);
                windY = windY / sqrt3 + gauss(0, wAmp / sqrt5);
                // Hard cap: wind magnitude can never exceed half the current distance
                double windMag = Math.hypot(windX, windY);
                double windCap = Math.min(w, dist * 0.35);
                if (windMag > windCap) {
                    windX = (windX / windMag) * windCap;
                    windY = (windY / windMag) * windCap;
                }
            } else {
                // Braking zone: aggressively kill wind AND damp velocity so
                // accumulated momentum doesn't carry the cursor past the target.
                windX /= sqrt2;
                windY /= sqrt2;
                veloX *= 0.75;
                veloY *= 0.75;
            }

            // ── Gravity: pulls velocity toward target ─────────────────────
            veloX += windX + gravity * (xe - xs) / dist;
            veloY += windY + gravity * (ye - ys) / dist;

            // ── Soft velocity clamp: pure rescale, no random wiggle ───────
            double maxStep = Math.max(Math.min(maxStepBase * mul, dist), 1.5);
            double veloMag = Math.hypot(veloX, veloY);
            if (veloMag > maxStep) {
                veloX = (veloX / veloMag) * maxStep;
                veloY = (veloY / veloMag) * maxStep;
            }

            // ── Apply step ────────────────────────────────────────────────
            int lastX = (int) Math.round(xs);
            int lastY = (int) Math.round(ys);
            xs += veloX;
            ys += veloY;

            if ((int) Math.round(xs) != lastX || (int) Math.round(ys) != lastY) {
                setMousePosition(new Point((int) Math.round(xs), (int) Math.round(ys)));
            }

            // ── Delay tied to pixel budget ────────────────────────────────
            double actualStep = Math.hypot(xs - lastX, ys - lastY);
            int delay = Math.max(delayForStep(Math.max(actualStep, 0.5), pxPerSec * mul), 4);

            if (windScale > 0.25 && rng.nextDouble() < PAUSE_CHANCE) {
                sleep(gaussianInt(90, 45, 35, 220));
            } else {
                sleep(delay);
            }
        }

        setMousePosition(new Point((int) Math.round(xe), (int) Math.round(ye)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Speed profile
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Maps movement progress (0=start, 1=end) to a speed multiplier [0.28, 1.0].
     * Shape: fast ramp-up, plateau, gradual deceleration.
     */
    private double speedProfile(double t) {
        return 0.28 + 0.72 * Math.sin(Math.PI * Math.pow(clamp(t, 0, 1), 0.6));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Geometry helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Perpendicular arc control point, offset 5-20% of distance to one side. */
    private Point arcControl(Point a, Point b, double dist) {
        double mx = (a.x + b.x) / 2.0, my = (a.y + b.y) / 2.0;
        double dx = b.x - a.x,         dy = b.y - a.y;
        double len = Math.hypot(dx, dy);
        double px = -dy / len,          py = dx / len;
        double offset = (0.05 + rng.nextDouble() * 0.15) * dist
                * (rng.nextBoolean() ? 1 : -1);
        return new Point((int) Math.round(mx + px * offset),
                (int) Math.round(my + py * offset));
    }

    /** Sample point on quadratic Bezier p0->p1(ctrl)->p2 at t in [0,1]. */
    private Point sampleBezier(Point p0, Point p1, Point p2, double t) {
        double mt = 1 - t;
        return new Point(
                (int) Math.round(mt*mt*p0.x + 2*mt*t*p1.x + t*t*p2.x),
                (int) Math.round(mt*mt*p0.y + 2*mt*t*p1.y + t*t*p2.y));
    }

    /** A point slightly past the target in a random direction (3-14 px). */
    private Point overshootPoint(Point target) {
        double angle  = rng.nextDouble() * 2 * Math.PI;
        double radius = 3 + rng.nextDouble() * OVERSHOOT_MAX;
        return new Point(
                (int) Math.round(target.x + Math.cos(angle) * radius),
                (int) Math.round(target.y + Math.sin(angle) * radius));
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  Utility
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the sleep time in ms needed to cover stepPx pixels at pxPerSec.
     * This is what keeps visual speed consistent regardless of step size.
     */
    private int delayForStep(double stepPx, double pxPerSec) {
        if (pxPerSec <= 0) return 5;
        return (int) clamp((stepPx / pxPerSec) * 1000.0, 2, 18);
    }

    /**
     * Maps DreamBot's MouseSettings speed (1-10, lower = faster)
     * to a pixels-per-second value ~[600, 1800], nudged by fatigue.
     */
    private double speedFromSettings() {
        int s = MouseSettings.getSpeed();
        double base = 600 + (10 - clamp(s, 1, 10)) * 120;
        return clamp(base + gauss(0, base * 0.04) - fatigue * 40, 300, 1800);
    }

    private double gauss(double mean, double sd) {
        return mean + rng.nextGaussian() * sd;
    }

    private int gaussianInt(double mean, double sd, int lo, int hi) {
        return (int) clamp(Math.round(gauss(mean, sd)), lo, hi);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static double euclidean(Point a, Point b) {
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    private void setMousePosition(Point p) {
        if (Mouse.getMouseSettings().isDrag()) {
            Instance.dispatchCanvasEvent(new MouseEvent(
                    Instance.getCanvas(), MouseEvent.MOUSE_DRAGGED,
                    System.currentTimeMillis(), 0,
                    p.x, p.y,
                    ViewportTools.getAbsoluteXCoordinate() + p.x,
                    ViewportTools.getAbsoluteYCoordinate() + p.y,
                    0, false, MouseEvent.BUTTON2));
        } else {
            Mouse.hop(p);
        }
    }

    private static void sleep(int ms) {
        if (ms <= 0) return;
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Logger.log(e.getMessage()); }
    }
}
