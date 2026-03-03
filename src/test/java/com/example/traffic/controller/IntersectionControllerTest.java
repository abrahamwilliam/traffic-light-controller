package com.example.traffic.controller;

import com.example.traffic.model.DirectionGroup;
import com.example.traffic.model.Intersection;
import com.example.traffic.model.LightColor;
import com.example.traffic.service.IntersectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IntersectionController.class)
class IntersectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntersectionService service;

    @Test
    void testCreateIntersection() throws Exception {

        Intersection intersection = new Intersection("A1");
        when(service.createIntersection("A1")).thenReturn(intersection);

        mockMvc.perform(post("/api/v1/intersections/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A1"));

        verify(service).createIntersection("A1");
    }

    @Test
    void testGetIntersection() throws Exception {

        Intersection intersection = new Intersection("A2");
        when(service.get("A2")).thenReturn(intersection);

        mockMvc.perform(get("/api/v1/intersections/A2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("A2"));

        verify(service).get("A2");
    }

    @Test
    void testPause() throws Exception {

        mockMvc.perform(post("/api/v1/intersections/A3/pause"))
                .andExpect(status().isOk())
                .andExpect(content().string("PAUSED"));

        verify(service).pause("A3");
    }

    @Test
    void testResume() throws Exception {

        mockMvc.perform(post("/api/v1/intersections/A4/resume"))
                .andExpect(status().isOk())
                .andExpect(content().string("RUNNING"));

        verify(service).resume("A4");
    }

    @Test
    void testOverride() throws Exception {

        String requestBody = """
                {
                    "NORTH_SOUTH": "RED",
                    "EAST_WEST": "GREEN"
                }
                """;

        mockMvc.perform(post("/api/v1/intersections/A5/override")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Override successful"));

        verify(service).override(eq("A5"), any(Map.class));
    }

    @Test
    void testHistory() throws Exception {

        Intersection intersection = new Intersection("A6");
        when(service.get("A6")).thenReturn(intersection);

        mockMvc.perform(get("/api/v1/intersections/A6/history"))
                .andExpect(status().isOk());

        verify(service).get("A6");
    }
}