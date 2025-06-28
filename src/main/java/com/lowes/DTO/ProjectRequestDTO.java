package com.lowes.DTO;
import lombok.*;
import com.lowes.enums.ServiceType;
import java.time.LocalDate;

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
    private Long ownerId;


}

