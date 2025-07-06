package com.lowes.controller;

import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User; // IMPORT ADDED
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

   private final ProjectService projectService;

    @PostMapping
    public Project createProject(@RequestBody ProjectRequestDTO dto, Authentication authentication) {
        // FIXED: Correct casting and variable name
        long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.createProject(dto, userId);
    }

    @GetMapping("/user")
    public List<Project> getUserProjects(Authentication authentication) {
        // FIXED: Correct casting and variable name
        long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.getProjectsByUser(userId);
    }


    @PutMapping("/{id}")
    public Project updateProject(@PathVariable UUID id, @RequestBody ProjectRequestDTO dto) {
        return projectService.updateProject(id, dto);
    }

    @GetMapping("/{id}")
    public Project getProject(@PathVariable UUID id) {
        return projectService.getProjectById(id);
    }

 

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
    }
}