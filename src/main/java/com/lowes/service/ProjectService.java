package com.lowes.service;

import com.lowes.entity.Project;
import com.lowes.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    public Project getProjectById(UUID id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

}