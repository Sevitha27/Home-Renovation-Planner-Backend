package com.lowes.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowes.Exception.AccessDeniedException;
import com.lowes.Exception.NotFoundException;
import com.lowes.dto.request.ProjectRequest;
import com.lowes.dto.response.ProjectResponse;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import com.lowes.mapper.ProjectMapper;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.RoomRepository;

@Service
public class ProjectService {
    
    private final ProjectRepository projectRepo;
//  private final RoomService roomService;
   @Autowired
    private RoomRepository roomRepo;


    public ProjectService(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }


    public ProjectResponse createProject(ProjectRequest request, User owner) {
        Project project = new Project();
        project.setName(request.name());
        project.setEstimate(request.estimate());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());
        project.setOwner(owner);

        return ProjectMapper.toDTO(projectRepo.save(project));
    }

    public ProjectResponse getProject(UUID projectId, User user) {
        Project project = projectRepo.findById(projectId)
            .orElseThrow(() -> new NotFoundException("Project not found"));
        
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You don't own this project!");
        }
        
        return ProjectMapper.toDTO(project);
    }

    public List<ProjectResponse> getProjectsByUser(User user) {
        return projectRepo.findByOwnerId(user.getId())
            .stream()
            .map(ProjectMapper::toDTO)
            .collect(Collectors.toList());
    }
      public Project getProjectWithOwner(UUID projectId, UUID userId) {
        return projectRepo.findByIdWithOwner(projectId)
            .filter(project -> project.getOwner().getId().equals(userId))
            .orElseThrow(() -> new AccessDeniedException("Project not found or access denied"));
    }



public double calculateProjectCost(UUID projectId, UUID userId) {
    // Step 1: Verify project ownership
    Project project = projectRepo.findById(projectId)
        .orElseThrow(() -> new NotFoundException("Project not found"));
    
    if (!project.getOwner().getId().equals(userId)) {
        throw new AccessDeniedException("Not authorized!");
    }
    
    // Step 2: Sum costs of all rooms
    double totalCost = roomRepo.findByProjectId(projectId).stream()
            .mapToDouble(room -> room.getTotalRoomCost() != null ? room.getTotalRoomCost() : 0)
            .sum();
    
    // Step 3: Update project cost
    project.setTotalProjectCost(totalCost);
    projectRepo.save(project);
    
    return totalCost;
}
}