package com.lowes.controller;

import com.lowes.DTO.ProjectRequestDTO;
import com.lowes.DTO.ProjectResponseDTO;
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectResponseDTO createProject(@RequestBody ProjectRequestDTO dto) {
        return projectService.createProject(dto);
    }
}
