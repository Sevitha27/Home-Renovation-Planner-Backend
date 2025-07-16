package com.lowes.dto.request;

import com.lowes.entity.enums.ServiceType;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {
    private String name;
    private ServiceType serviceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer estimatedBudget;
}