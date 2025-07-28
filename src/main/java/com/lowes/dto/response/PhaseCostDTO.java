package com.lowes.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PhaseCostDTO {
    private UUID phaseId;
    private String phaseName;
    private String phaseType;
    private int vendorCost;
    private int materialCost;
    private int totalPhaseCost;
}

