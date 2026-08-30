package com.ccscripts.listener.camera;

import com.ccscripts.model.TimestampedPoint;
import lombok.Getter;
import lombok.ToString;

import java.awt.*;
import java.util.ArrayList;

@Getter
public class CameraMovement {
    // points here are camera angle, pitch, yaw
    private ArrayList<TimestampedPoint> pointHistory = new ArrayList<>();
    private final long startTime;
    final int zoom;

    public CameraMovement(Point startPoint, int zoom) {
        this.zoom = zoom;
        this.startTime = System.currentTimeMillis();
        this.pointHistory = new ArrayList<>();
        pointHistory.add(new TimestampedPoint(startPoint));
    }

    public CameraMovement append(Point point) {
        pointHistory.add(new TimestampedPoint(point));
        return this;
    }

    public Point getLastPoint() {
        if (pointHistory == null || pointHistory.isEmpty()) return null;
        return pointHistory.get(pointHistory.size() - 1).getPoint();
    }

    @Override
    public String toString() {
        return pointHistory.toString();
    }
}
