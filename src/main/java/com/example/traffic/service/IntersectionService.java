package com.example.traffic.service;

import com.example.traffic.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class IntersectionService {

    private final Map<String, Intersection> intersections = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private static final int GREEN_DURATION = 30;
    private static final int YELLOW_DURATION = 5;

    public Intersection createIntersection(String id) {
        Intersection intersection = new Intersection(id);
        intersections.put(id, intersection);
        startWorker(intersection);
        return intersection;
    }

    public Intersection get(String id) {
        return intersections.get(id);
    }

    public void pause(String id) {
        intersections.get(id).setStatus(IntersectionStatus.PAUSED);
    }

    public void resume(String id) {
        intersections.get(id).setStatus(IntersectionStatus.RUNNING);
    }

    public void override(String id, Map<DirectionGroup, LightColor> newState) {
        validateConflict(newState);
        Intersection intersection = intersections.get(id);

        intersection.getLock().lock();
        try {
            intersection.updateState(newState);
        } finally {
            intersection.getLock().unlock();
        }
    }

    private void startWorker(Intersection intersection) {
        scheduler.scheduleAtFixedRate(() -> runCycle(intersection),
                GREEN_DURATION,
                GREEN_DURATION,
                TimeUnit.SECONDS);
    }

    private void runCycle(Intersection intersection) {

        if (intersection.getStatus() == IntersectionStatus.PAUSED)
            return;

        intersection.getLock().lock();
        try {
            Map<DirectionGroup, LightColor> current = intersection.getCurrentState();
            Map<DirectionGroup, LightColor> next = new HashMap<>();

            LightColor ns = current.get(DirectionGroup.NORTH_SOUTH);
            LightColor ew = current.get(DirectionGroup.EAST_WEST);

            // NORTH_SOUTH GREEN -> YELLOW
            if (ns == LightColor.GREEN) {
                next.put(DirectionGroup.NORTH_SOUTH, LightColor.YELLOW);
                next.put(DirectionGroup.EAST_WEST, LightColor.RED);
            }
            // NORTH_SOUTH YELLOW -> RED + EAST_WEST GREEN
            else if (ns == LightColor.YELLOW) {
                next.put(DirectionGroup.NORTH_SOUTH, LightColor.RED);
                next.put(DirectionGroup.EAST_WEST, LightColor.GREEN);
            }
            // EAST_WEST GREEN -> YELLOW
            else if (ew == LightColor.GREEN) {
                next.put(DirectionGroup.NORTH_SOUTH, LightColor.RED);
                next.put(DirectionGroup.EAST_WEST, LightColor.YELLOW);
            }
            // EAST_WEST YELLOW -> RED + NORTH_SOUTH GREEN
            else if (ew == LightColor.YELLOW) {
                next.put(DirectionGroup.NORTH_SOUTH, LightColor.GREEN);
                next.put(DirectionGroup.EAST_WEST, LightColor.RED);
            }

            validateConflict(next);
            intersection.updateState(next);

        } finally {
            intersection.getLock().unlock();
        }
    }
    

    private void validateConflict(Map<DirectionGroup, LightColor> state) {

        LightColor ns = state.get(DirectionGroup.NORTH_SOUTH);
        LightColor ew = state.get(DirectionGroup.EAST_WEST);

        // Cannot both be GREEN
        if (ns == LightColor.GREEN && ew == LightColor.GREEN) {
            throw new IllegalArgumentException("Conflicting GREEN lights detected.");
        }

        // Cannot both be YELLOW
        if (ns == LightColor.YELLOW && ew == LightColor.YELLOW) {
            throw new IllegalArgumentException("Conflicting YELLOW lights detected.");
        }

        // GREEN and YELLOW cannot be active in opposite directions
        if ((ns == LightColor.GREEN && ew == LightColor.YELLOW) ||
            (ns == LightColor.YELLOW && ew == LightColor.GREEN)) {
            throw new IllegalArgumentException("Unsafe GREEN/YELLOW conflict detected.");
        }
    }
    
}