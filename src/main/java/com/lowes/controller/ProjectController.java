package com.lowes.controller;

import com.lowes.DTO.ProjectRequestDTO;
import com.lowes.DTO.ProjectResponseDTO;
import com.lowes.dto.response.BudgetOverviewResponseDTO;
import com.lowes.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ProjectResponseDTO createProject(@RequestBody ProjectRequestDTO dto) {
        return projectService.createProject(dto);
    }
    @GetMapping("/{projectId}/budget-overview")
    public ResponseEntity<BudgetOverviewResponseDTO> getBudgetOverview(@PathVariable UUID projectId) {
        BudgetOverviewResponseDTO overview = projectService.getBudgetOverview(projectId);
        return ResponseEntity.ok(overview);
    }
}
