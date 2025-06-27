package com.lowes.service;
import com.lowes.DTO.ProjectRequestDTO;
import com.lowes.DTO.ProjectResponseDTO;
import com.lowes.entity.Project;
import com.lowes.entity.User;
import com.lowes.mapper.ProjectMapper;
import com.lowes.repository.ProjectRepository;
import com.lowes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        User owner = userRepository.findById(dto.getOwnerId()).orElseThrow();
        Project project = ProjectMapper.toEntity(dto, owner);
        Project saved = projectRepository.save(project);
        return ProjectMapper.toDTO(saved);
    }
}