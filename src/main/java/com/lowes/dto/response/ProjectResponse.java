package com.lowes.dto.response;

import com.lowes.entity.enums.ServiceType;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProjectResponse {
    private String exposedId;        // String representation of UUID
    private String name;
    private ServiceType serviceType;
    private Integer estimatedBudget; // Changed from Double to Integer
    private LocalDate startDate;
    private LocalDate endDate;
    private Long ownerId;            // long type
    private String ownerName;
    private Integer totalCost;       // Changed from Double to Integer
    private List<RoomResponse> rooms;
}