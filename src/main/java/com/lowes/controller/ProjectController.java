package com.lowes.controller;

import com.lowes.DTO.ProjectRequestDTO;
import com.lowes.DTO.ProjectResponseDTO;
import com.lowes.entity.Project;
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    @Autowired
    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectResponseDTO createProject(@RequestBody ProjectRequestDTO dto) {
        return projectService.createProject(dto);
        }
    @GetMapping("/{id}")
    public Project getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }
}
