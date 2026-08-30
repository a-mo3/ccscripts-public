package com.ccscripts.listener.mouse;

import com.ccscripts.model.EntityWrapper;
import com.ccscripts.model.TimestampedPoint;
import lombok.Getter;
import lombok.Setter;
import org.dreambot.api.Client;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.widget.Widgets;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class MouseMovement {
    // sampled 50ms implicitly
    private ArrayList<TimestampedPoint> pointHistory = new ArrayList<>();
    // start time is made at initialization, first point may happen much later
    // start time is not actually used nvm

    @Setter
    private List<EntityWrapper> hovered;
    // context
    final int cameraPitch;
    final int cameraYaw;
    final int cameraZoom;
    final int clientHeight;
    final int clientWidth;
    final boolean isWidgetOpen;

    public MouseMovement(Point startPoint) {
//        this.startTime = System.currentTimeMillis();
        this.pointHistory = new ArrayList<>();
        pointHistory.add(new TimestampedPoint(startPoint));

        this.cameraPitch = Camera.getPitch();
        this.cameraYaw = Camera.getYaw();
        this.cameraZoom = Camera.getZoom();

        this.clientHeight = Client.getViewportHeight();
        this.clientWidth = Client.getViewportWidth();
        this.isWidgetOpen = Widgets.isOpen();
    }

    public MouseMovement append(Point point) {
        pointHistory.add(new TimestampedPoint(point));
        return this;
    }

    public Point getLastPoint() {
        if (pointHistory == null || pointHistory.isEmpty()) return null;
        return pointHistory.get(pointHistory.size() - 1).getPoint();
    }

    @Override
    public String toString() {
        return "Hovered? " + (hovered != null) + " " + pointHistory.toString();
    }

    /**
     *
     * @param timestamp timestamp milli
     * @return true if happened between the first and last mouse movement
     */
    public boolean happensWithin(long timestamp) {
        return timestamp > pointHistory.get(1).getTimestamp() && timestamp < pointHistory.get(pointHistory.size() - 1).getTimestamp();
    }
}
