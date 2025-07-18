package com.lowes.mapper;

import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Phase;

public class PhaseMapper {
    public static PhaseResponseDTO toDTO(Phase phase) {
        return PhaseResponseDTO.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName())
                .phaseType(phase.getPhaseType())
                .phaseStatus(phase.getPhaseStatus())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .totalPhaseCost(phase.getTotalPhaseCost())
                .build();
    }

}
