package com.example.traffic.model;

import java.time.Instant;
import java.util.Map;

public class HistoryEntry {

    private Instant timestamp;
    private Map<DirectionGroup, LightColor> state;
    private long durationSeconds;

    public HistoryEntry(Instant timestamp,
                        Map<DirectionGroup, LightColor> state,
                        long durationSeconds) {
        this.timestamp = timestamp;
        this.state = state;
        this.durationSeconds = durationSeconds;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Map<DirectionGroup, LightColor> getState() {
        return state;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }
}