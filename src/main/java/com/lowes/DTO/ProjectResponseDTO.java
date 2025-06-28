package com.lowes.DTO;
import com.lowes.enums.ServiceType;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private ServiceType serviceType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double estimatedBudget;
    private Long ownerId;
    private List<String> roomNames;


}
