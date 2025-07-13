package com.lowes.controller;

import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.dto.response.ProjectResponseDTO;
import com.lowes.entity.User;
import com.lowes.mapper.ProjectMapper;
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('CUSTOMER')")
    public ProjectResponseDTO createProject(
            @RequestBody ProjectRequestDTO dto,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ProjectMapper.toDTO(projectService.createProject(dto, user.getExposedId()));
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ProjectResponseDTO> getUserProjects(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return projectService.getProjectsByUser(user.getId()).stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{exposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@projectSecurity.isProjectOwner(#exposedId, authentication.principal.exposedId)")
    public ProjectResponseDTO updateProject(
            @PathVariable UUID exposedId,
            @RequestBody ProjectRequestDTO dto
    ) {
        return ProjectMapper.toDTO(projectService.updateProject(exposedId, dto));
    }

    @GetMapping("/{exposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@projectSecurity.isProjectOwner(#exposedId, authentication.principal.exposedId)")
    public ProjectResponseDTO getProject(@PathVariable UUID exposedId) {
        return ProjectMapper.toDTO(projectService.getProjectById(exposedId));
    }

    @DeleteMapping("/{exposedId}")
    @PreAuthorize("hasRole('CUSTOMER') and " +
            "@projectSecurity.isProjectOwner(#exposedId, authentication.principal.exposedId)")
    public void deleteProject(@PathVariable UUID exposedId) {
        projectService.deleteProject(exposedId);
    }
}