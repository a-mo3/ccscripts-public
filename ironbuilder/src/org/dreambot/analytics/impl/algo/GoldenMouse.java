package org.dreambot.analytics.impl.algo;

import org.dreambot.api.input.event.impl.mouse.MouseButton;
import org.dreambot.api.input.mouse.algorithm.MouseAlgorithm;
import org.dreambot.api.input.mouse.destination.AbstractMouseDestination;

import java.awt.Point;

public class GoldenMouse implements MouseAlgorithm {
    public static Point[] goldenRatioTrajectory(Point start, Point end, int steps) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end cannot be null");
        }
        if (steps < 2) {
            throw new IllegalArgumentException("steps must be at least 2");
        }

        final double phi = (1.0 + Math.sqrt(5.0)) / 2.0;

        Point[] points = new Point[steps];

        double dx = end.x - start.x;
        double dy = end.y - start.y;

        // Perpendicular vector for the curve bulge
        double length = Math.sqrt(dx * dx + dy * dy);
        double nx = -dy / length;
        double ny = dx / length;

        // Curve height based on golden ratio
        double amplitude = length / (phi * phi);

        for (int i = 0; i < steps; i++) {
            double t = (double) i / (steps - 1);

            // Linear interpolation from start to end
            double x = start.x + dx * t;
            double y = start.y + dy * t;
            // Golden-ratio-shaped arc offset
            double goldenCurve = Math.sin(Math.PI * t) * Math.pow(t, 1.0 / phi);
            x += nx * amplitude * goldenCurve;
            y += ny * amplitude * goldenCurve;

            points[i] = new Point((int) Math.round(x), (int) Math.round(y));
        }

        points[0] = new Point(start);
        points[steps - 1] = new Point(end);
        return points;
    }

    @Override
    public boolean handleMovement(AbstractMouseDestination abstractMouseDestination) {
        return false;
    }

    @Override
    public boolean handleClick(MouseButton mouseButton) {
        return false;
    }
}
