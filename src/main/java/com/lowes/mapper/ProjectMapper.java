package com.lowes.mapper;

import com.lowes.dto.response.ProjectResponse;
import com.lowes.entity.Project;

public class ProjectMapper {

    public static ProjectResponse toDTO(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setExposedId(project.getExposedId().toString());
        response.setName(project.getName());
        response.setServiceType(project.getServiceType());
        
        // Use Integer values directly
        response.setEstimatedBudget(project.getEstimatedBudget());
        response.setTotalCost(project.getTotalCost());
        
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setOwnerId(project.getOwner().getId());
        response.setOwnerName(project.getOwner().getName());
        
        if (project.getRooms() != null) {
            response.setRooms(project.getRooms().stream().map(RoomMapper::toDTO).toList());
        }
        
        return response;
    }
}