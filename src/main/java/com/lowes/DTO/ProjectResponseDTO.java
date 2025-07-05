package com.lowes.dto;
import com.lowes.entity.enums.ServiceType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {
    private UUID id;
    private String name;
    private ServiceType serviceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private Long ownerId;
    private List<String> roomNames;


}
