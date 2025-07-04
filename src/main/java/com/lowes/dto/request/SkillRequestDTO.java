package com.lowes.dto.request;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillRequestDTO {

    @NonNull
    private String skillName;
    
    @NonNull
    private Double basePrice;
}
