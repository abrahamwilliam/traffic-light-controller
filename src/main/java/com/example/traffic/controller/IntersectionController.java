package com.example.traffic.controller;

import com.example.traffic.model.*;
import com.example.traffic.service.IntersectionService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/intersections")
public class IntersectionController {

    private final IntersectionService service;

    public IntersectionController(IntersectionService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    public Intersection create(@PathVariable String id) {
        return service.createIntersection(id);
    }

    @GetMapping("/{id}")
    public Intersection get(@PathVariable String id) {
        return service.get(id);
    }

    @PostMapping("/{id}/pause")
    public String pause(@PathVariable String id) {
        service.pause(id);
        return "PAUSED";
    }

    @PostMapping("/{id}/resume")
    public String resume(@PathVariable String id) {
        service.resume(id);
        return "RUNNING";
    }

    @PostMapping("/{id}/override")
    public String override(@PathVariable String id,
                           @RequestBody Map<DirectionGroup, LightColor> newState) {
        service.override(id, newState);
        return "Override successful";
    }

    @GetMapping("/{id}/history")
    public Object history(@PathVariable String id) {
        return service.get(id).getHistory();
    }
}