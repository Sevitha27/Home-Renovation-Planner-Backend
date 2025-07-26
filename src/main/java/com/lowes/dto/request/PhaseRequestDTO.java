package com.lowes.dto.request;

import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PhaseRequestDTO {


    private UUID vendorId;
    private UUID roomId;

    @NonNull
    private String phaseName;
    private String description;

    @NonNull
    private LocalDate startDate;

    @NonNull
    private LocalDate endDate;

    @NonNull
    private PhaseType phaseType;

    @NonNull
    private PhaseStatus phaseStatus;


}

