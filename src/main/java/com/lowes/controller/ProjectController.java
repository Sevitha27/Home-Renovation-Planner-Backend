package com.lowes.controller;

import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.dto.response.ProjectResponse;
import com.lowes.entity.User;
import com.lowes.mapper.ProjectMapper;
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@RequestBody ProjectRequestDTO dto, Authentication authentication) {
  UUID exposedId = ((User) authentication.getPrincipal()).getExposedId();
        return ProjectMapper.toDTO(projectService.createProject(dto, exposedId));
    }

    @GetMapping("/user")
    public List<ProjectResponse> getUserProjects(Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        return projectService.getProjectsByUser(userId).stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{exposedId}")
    public ProjectResponse updateProject(
            @PathVariable UUID exposedId,
            @RequestBody ProjectRequestDTO dto
    ) {
        return ProjectMapper.toDTO(projectService.updateProject(exposedId, dto));
    }

    @GetMapping("/{exposedId}")
    public ProjectResponse getProject(@PathVariable UUID exposedId) {
        return ProjectMapper.toDTO(projectService.getProjectById(exposedId));
    }

    @DeleteMapping("/{exposedId}")
    public void deleteProject(@PathVariable UUID exposedId) {
        projectService.deleteProject(exposedId);
    }
}