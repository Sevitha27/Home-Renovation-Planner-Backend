package com.lowes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BudgetOverviewResponseDTO {
    private Double estimatedBudget;
    private Integer totalVendorCost;
    private Integer totalMaterialCost;
    private Integer totalActualCost;
    private Double percentageSpent;
    private Double remainingBudget;
    private Boolean isOverBudget;
    private String budgetStatusColor; // "RED" or "GREEN"
}