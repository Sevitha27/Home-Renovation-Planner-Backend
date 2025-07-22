package com.lowes.dto.response;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.lowes.dto.VendorDTO;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private Integer totalPhaseCost;
    private PhaseType phaseType;
    private PhaseStatus phaseStatus;
    private VendorDTO vendor;
    private UUID vendorId;


    private List<PhaseMaterialUserResponse> phaseMaterialUserResponseList;
}
