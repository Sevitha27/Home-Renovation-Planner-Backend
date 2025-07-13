package com.lowes.mapper;

import com.lowes.dto.response.ProjectResponseDTO;
import com.lowes.entity.Project;

public class ProjectMapper {

    public static ProjectResponseDTO toDTO(Project project) {
        ProjectResponseDTO response = new ProjectResponseDTO();
        response.setExposedId(project.getExposedId().toString());
        response.setName(project.getName());
        response.setServiceType(project.getServiceType());
        response.setEstimatedBudget(project.getEstimatedBudget());
        response.setTotalCost(project.getTotalCost());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        
        if (project.getOwner() != null) {
            response.setOwnerId(project.getOwner().getExposedId().toString());
            response.setOwnerName(project.getOwner().getName());
        }
        
        if (project.getRooms() != null) {
            response.setRooms(project.getRooms().stream().map(RoomMapper::toDTO).toList());
        }
        
        return response;
    }
}