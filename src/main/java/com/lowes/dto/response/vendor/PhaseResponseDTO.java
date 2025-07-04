package com.lowes.dto.response.vendor;

import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhaseResponseDTO {
    private UUID id;
    private String phaseName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private PhaseType phaseType;
    private PhaseStatus phaseStatus;
    private Integer vendorCost;
    private List<PhaseMaterialDTO> materials;
}
