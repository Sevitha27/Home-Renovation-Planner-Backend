package com.lowes.mapper;

import com.lowes.dto.response.ProjectResponse;
import com.lowes.entity.Project;

public class ProjectMapper {
    
public static ProjectResponse toDTO(Project project) {
    return new ProjectResponse(
        project.getId(),
        project.getName(),
        project.getEstimate(),       // Add these new fields
        project.getStartDate(),
        project.getEndDate(),
        project.getOwner().getId(),
  project.getRooms().stream().map(RoomMapper::toDTO).toList(),
project.getTotalProjectCost()

    );
}
}
