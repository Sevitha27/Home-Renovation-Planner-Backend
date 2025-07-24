package com.lowes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BudgetOverviewResponseDTO {
    private UUID projectId;
    private String projectName;
    private double estimatedBudget;
    private double totalActualCost;
    private List<RoomCostDTO> rooms;
    private List<PhaseCostDTO> phases;
}
