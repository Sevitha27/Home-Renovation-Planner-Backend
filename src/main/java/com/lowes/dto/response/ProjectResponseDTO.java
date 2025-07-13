package com.lowes.dto.response;

import com.lowes.entity.enums.ServiceType;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectResponseDTO {
    private String exposedId;
    private String name;
    private ServiceType serviceType;
    private Integer estimatedBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String ownerId; // User's exposed ID
    private String ownerName;
    private Integer totalCost;
    private List<RoomResponseDTO> rooms;
}