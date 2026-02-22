package com.example.traffic.model;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Intersection {

    private final String id;
    private IntersectionStatus status = IntersectionStatus.RUNNING;

    private Map<DirectionGroup, LightColor> currentState = new HashMap<>();
    private Instant lastChangedAt = Instant.now();

    private final List<HistoryEntry> history = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();

    public Intersection(String id) {
        this.id = id;
        currentState.put(DirectionGroup.NORTH_SOUTH, LightColor.GREEN);
        currentState.put(DirectionGroup.EAST_WEST, LightColor.RED);
    }

    public String getId() { return id; }

    public IntersectionStatus getStatus() { return status; }

    public void setStatus(IntersectionStatus status) { this.status = status; }

    public Map<DirectionGroup, LightColor> getCurrentState() { return currentState; }

    public List<HistoryEntry> getHistory() { return history; }

    public ReentrantLock getLock() { return lock; }

    public void updateState(Map<DirectionGroup, LightColor> newState) {
        long duration = Instant.now().getEpochSecond() - lastChangedAt.getEpochSecond();
        history.add(new HistoryEntry(lastChangedAt,
                new HashMap<>(currentState),
                duration));

        currentState = new HashMap<>(newState);
        lastChangedAt = Instant.now();
    }
}