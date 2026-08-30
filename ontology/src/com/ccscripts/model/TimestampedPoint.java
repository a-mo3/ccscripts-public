package com.ccscripts.model;

import lombok.Getter;

import java.awt.*;

/**
 * 2d point with a timestamp
 */
public class TimestampedPoint {
    @Getter
    final long timestamp;
    final int x;
    final int y;

    public TimestampedPoint(Point p) {
        this.timestamp = System.currentTimeMillis();
        this.x = p.x;
        this.y = p.y;
    }

    public TimestampedPoint(int x, int y) {
        this.timestamp = System.currentTimeMillis();
        this.x = x;
        this.y = y;
    }

    public Point getPoint() {
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public double distance(Point p) {
        return Math.sqrt((x - p.x) ^ 2 + (y - p.y) ^ 2);
    }
}
