package com.example.traffic.service;

import com.example.traffic.model.DirectionGroup;
import com.example.traffic.model.Intersection;
import com.example.traffic.model.LightColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionServiceTest {

    private IntersectionService service;

    @BeforeEach
    void setup() {
        service = new IntersectionService();
    }

    @Test
    void testInitialState() {
        Intersection intersection = service.createIntersection("A1");

        Map<DirectionGroup, LightColor> state = intersection.getCurrentState();

        assertEquals(LightColor.GREEN, state.get(DirectionGroup.NORTH_SOUTH));
        assertEquals(LightColor.RED, state.get(DirectionGroup.EAST_WEST));
    }

    @Test
    void testGreenToYellowTransition() {
        Intersection intersection = service.createIntersection("A2");

        service.runCycle(intersection);

        Map<DirectionGroup, LightColor> state = intersection.getCurrentState();

        assertEquals(LightColor.YELLOW, state.get(DirectionGroup.NORTH_SOUTH));
        assertEquals(LightColor.RED, state.get(DirectionGroup.EAST_WEST));
    }

    @Test
    void testFullCycleNorthSouthToEastWest() {
        Intersection intersection = service.createIntersection("A3");

        // GREEN -> YELLOW
        service.runCycle(intersection);

        // YELLOW -> EAST_WEST GREEN
        service.runCycle(intersection);

        Map<DirectionGroup, LightColor> state = intersection.getCurrentState();

        assertEquals(LightColor.RED, state.get(DirectionGroup.NORTH_SOUTH));
        assertEquals(LightColor.GREEN, state.get(DirectionGroup.EAST_WEST));
    }

    @Test
    void testEastWestGreenToYellow() {
        Intersection intersection = service.createIntersection("A4");

        // Move to EAST_WEST GREEN
        service.runCycle(intersection); // NS YELLOW
        service.runCycle(intersection); // EW GREEN

        // Now EW GREEN -> YELLOW
        service.runCycle(intersection);

        Map<DirectionGroup, LightColor> state = intersection.getCurrentState();

        assertEquals(LightColor.RED, state.get(DirectionGroup.NORTH_SOUTH));
        assertEquals(LightColor.YELLOW, state.get(DirectionGroup.EAST_WEST));
    }

    @Test
    void testPauseStopsTransition() {
        Intersection intersection = service.createIntersection("A5");

        service.pause("A5");

        Map<DirectionGroup, LightColor> before = intersection.getCurrentState();

        service.runCycle(intersection);

        Map<DirectionGroup, LightColor> after = intersection.getCurrentState();

        assertEquals(before, after);
    }

    @Test
    void testConflictingGreenOverride() {
        service.createIntersection("A6");

        Map<DirectionGroup, LightColor> invalidState = Map.of(
                DirectionGroup.NORTH_SOUTH, LightColor.GREEN,
                DirectionGroup.EAST_WEST, LightColor.GREEN
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.override("A6", invalidState));
    }

    @Test
    void testConflictingYellowOverride() {
        service.createIntersection("A7");

        Map<DirectionGroup, LightColor> invalidState = Map.of(
                DirectionGroup.NORTH_SOUTH, LightColor.YELLOW,
                DirectionGroup.EAST_WEST, LightColor.YELLOW
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.override("A7", invalidState));
    }

    @Test
    void testHistoryRecordedOnStateChange() {
        Intersection intersection = service.createIntersection("A8");

        service.runCycle(intersection);

        assertFalse(intersection.getHistory().isEmpty());
        assertEquals(1, intersection.getHistory().size());
    }
}