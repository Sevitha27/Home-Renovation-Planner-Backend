package com.lowes.mapper;

import com.lowes.dto.ProjectRequestDTO;
import com.lowes.dto.ProjectResponseDTO;
import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.User;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static Project toEntity(ProjectRequestDTO dto, User owner) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setServiceType(dto.getServiceType());
        project.setEstimatedBudget(dto.getEstimatedBudget());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setOwner(owner);
        return project;
    }

    public static ProjectResponseDTO toDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .serviceType(project.getServiceType())
                .estimatedBudget(project.getEstimatedBudget())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .ownerId(project.getOwner() != null ? project.getOwner().getId() : null)

                .roomNames(project.getRooms() != null
                        ? project.getRooms().stream().map(Room::getName).toList()
                        : null)
                .build();
    }
}