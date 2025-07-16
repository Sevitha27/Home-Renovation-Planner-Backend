package com.lowes.service;

import com.lowes.dto.request.ProjectRequestDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.ProjectRepository;

import com.lowes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;


    public Project createProject(ProjectRequestDTO dto,  UUID ownerId) {
        User owner = userRepository.findByExposedId(ownerId);
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


    public Project updateProject(UUID exposedId, ProjectRequestDTO dto) {
        Project project = projectRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));

        project.setName(dto.getName());
        project.setServiceType(dto.getServiceType());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setEstimatedBudget(dto.getEstimatedBudget());

        return projectRepository.save(project);
    }


    public Project getProjectById(UUID exposedId) {
        return projectRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
    }


    public List<Project> getProjectsByUser(Long userId) {

        return projectRepository.findByOwnerId(userId);
    }


    public void deleteProject(UUID exposedId) {
        Project project = projectRepository.findByExposedId(exposedId)
                .orElseThrow(() -> new ElementNotFoundException("Project not found"));
        projectRepository.delete(project);
    }
}