package com.lowes.convertor;

import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Phase;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PhaseConvertor {

    public static PhaseResponseDTO phaseToPhaseResponse(Phase phase){
        PhaseResponseDTO phaseResponseDTO = PhaseResponseDTO.builder()
                .phaseName(phase.getPhaseName())
                .description(phase.getDescription())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .phaseType(phase.getPhaseType())
                .phaseStatus(phase.getPhaseStatus())
                .Id(phase.getId())
                .build();
        return phaseResponseDTO;
    }
}
