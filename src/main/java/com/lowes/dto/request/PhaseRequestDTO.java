package com.lowes.dto.request;

import com.lowes.entity.Project;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseStatus;
import com.lowes.entity.enums.PhaseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PhaseRequestDTO {


    private Vendor vendor;
    private Room room;
    private String phaseName;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
    private PhaseType phaseType;
    private PhaseStatus phaseStatus;


}