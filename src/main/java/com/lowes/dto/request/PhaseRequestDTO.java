package com.lowes.dto.request;

import com.lowes.entity.Project;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PhaseRequestDTO {


    private Vendor vendor;
    private Project project;
    private String phaseName;
    private String description;

    private LocalDate start_date;
    private LocalDate end_date;

    private PhaseType phaseType;
    private PhaseStatus phaseStatus;


}
