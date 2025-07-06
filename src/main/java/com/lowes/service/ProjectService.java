package com.lowes.service;


import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;

@Service
public class ProjectService {


    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    @PreAuthorize("#ownerId == authentication.principal.id")
    public Project createProject(ProjectRequestDTO dto, long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ElementNotFoundException("User not found"));
        
        Project project = Project.builder()
                .name(dto.getName())
                .serviceType(dto.getServiceType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .estimatedBudget(dto.getEstimatedBudget())
                .owner(owner)
                .build();
        
        return projectRepository.save(project);
    }

    @PreAuthorize("@projectSecurity.isProjectOwner(#id, authentication.principal.id)")
    public Project updateProject(UUID id, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
        
        project.setName(dto.getName());
        project.setServiceType(dto.getServiceType());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setEstimatedBudget(dto.getEstimatedBudget());
        
        return projectRepository.save(project);
    }

    @PreAuthorize("@projectSecurity.isProjectOwner(#id, authentication.principal.id)")
    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
    }

    @PreAuthorize("#userId == authentication.principal.id")
    public List<Project> getProjectsByUser(long userId) {
        return projectRepository.findByOwnerId(userId);
    }

    @PreAuthorize("@projectSecurity.isProjectOwner(#id, authentication.principal.id)")
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
    }
}

