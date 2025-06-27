package com.lowes.dto.response;

import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class PhaseResponse {

    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;


    private PhaseType phaseType;

    private Integer totalPhaseCost;
    private Integer vendorCost;
    private PhaseStatus phaseStatus;
}
