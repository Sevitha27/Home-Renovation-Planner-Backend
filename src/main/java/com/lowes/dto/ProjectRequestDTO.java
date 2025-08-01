package com.lowes.dto;
import lombok.*;
import com.lowes.entity.enums.ServiceType;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDTO {
    private String name;
    private ServiceType serviceType;
    private Double estimatedBudget;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID ownerId;


}

