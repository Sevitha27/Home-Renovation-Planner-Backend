package com.lowes.security;

import com.lowes.entity.Project;
import com.lowes.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectSecurity {

    private final ProjectRepository projectRepository;

    public boolean isProjectOwner(UUID projectExposedId, Long userId) {
        return projectRepository.findByExposedIdAndOwnerId(projectExposedId, userId).isPresent();
    }
}