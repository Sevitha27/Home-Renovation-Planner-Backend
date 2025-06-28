package com.lowes.service;

import com.lowes.controller.ProjectResponseDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.lowes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    private final UserRepository userRepository;

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow();
        Project project = ProjectMapper.toEntity(dto, owner);
        Project saved = projectRepository.save(project);
        return ProjectMapper.toDTO(saved);}

        public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }
}