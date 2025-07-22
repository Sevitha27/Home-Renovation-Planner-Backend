package com.lowes.dto.response;

import com.lowes.entity.Phase;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseResponseDTO {

    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private UUID id;
    private PhaseType phaseType;
    private PhaseStatus phaseStatus;

    private Integer totalPhaseCost;

    public PhaseResponseDTO(Phase phase) {
        this.description = phase.getDescription();
        this.phaseName = phase.getPhaseName();
        this.startDate = phase.getStartDate();
        this.endDate = phase.getEndDate();
        this.phaseType = phase.getPhaseType();
        this.phaseStatus = phase.getPhaseStatus();
        this.totalPhaseCost = phase.getTotalPhaseCost();
        this.id = phase.getId();
    }

    public static PhaseResponseDTO toDTO(Phase phase) {
        return new PhaseResponseDTO(phase);
    }
}