package com.lowes.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lowes.Exception.AccessDeniedException;
import com.lowes.Exception.NotFoundException;
import com.lowes.config.CurrentUser;
import com.lowes.dto.request.ProjectRequest;
import com.lowes.dto.response.ProjectResponse;
import com.lowes.entity.User;
import com.lowes.service.JwtService;
import com.lowes.service.ProjectService;


import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/projects") 
@RequiredArgsConstructor
public class ProjectController {
    
private final ProjectService projectService;
@Autowired
    private JwtService jwtService;

@GetMapping("/user")
public ResponseEntity<List<ProjectResponse>> getUserProjects(@CurrentUser User user) {
      System.out.println("Current user ID in controller: " + user.getId());
    System.out.println("Current user email: " + user.getEmail());
    
    List<ProjectResponse> projects = projectService.getProjectsByUser(user);
    System.out.println("Returning " + projects.size() + " projects");
    
    return ResponseEntity.ok(projects);
}

  @PostMapping
public ResponseEntity<?> createProject(
    @RequestBody ProjectRequest request,
    @CurrentUser User user) {
    
    try {
        System.out.println("API Request received: " + request);
        ProjectResponse response = projectService.createProject(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
        System.err.println("Controller error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "message", "Failed to create project",
                "error", e.getMessage()
            ));
    }
}

      @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
        @PathVariable UUID projectId,
        @CurrentUser User user) {
        
        try {
            System.out.println("Fetching project " + projectId + " for user: " + user.getEmail());
            return ResponseEntity.ok(projectService.getProject(projectId, user));
        } catch (AccessDeniedException e) {
            System.err.println("Access denied for user: " + user.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


@GetMapping("/{projectId}/cost")
public ResponseEntity<Double> getProjectCost(
        @PathVariable UUID projectId,
        @RequestHeader("Authorization") String token) {
    
    UUID userId = jwtService.extractUserId(token.substring(7));
    return ResponseEntity.ok(projectService.calculateProjectCost(projectId, userId));
}

}
