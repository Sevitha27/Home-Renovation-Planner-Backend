package com.lowes.dto.response;

import com.lowes.dto.VendorDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.Vendor;
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
    private VendorDTO vendor;

    public PhaseResponseDTO(Phase phase) {
        this.description=phase.getDescription();
        this.phaseName=phase.getPhaseName();
        this.startDate=phase.getStartDate();
        this.endDate=phase.getEndDate();
        this.phaseType=phase.getPhaseType();
        this.phaseStatus=phase.getPhaseStatus();
        this.totalPhaseCost=phase.getTotalPhaseCost();
        this.id=phase.getId();
        if (phase.getVendor() != null) {
            this.vendor = new VendorDTO(phase.getVendor());
        } else {
            this.vendor = null;
        }
    }

    public static PhaseResponseDTO toDTO(Phase phase) {
        return new PhaseResponseDTO(phase);
    }
}