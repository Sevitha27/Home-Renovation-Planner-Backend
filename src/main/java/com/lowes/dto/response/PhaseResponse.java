package com.lowes.dto.response;

import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseResponse {

    private UUID id;

    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;


    private PhaseType phaseType;
    private PhaseStatus phaseStatus;
}
