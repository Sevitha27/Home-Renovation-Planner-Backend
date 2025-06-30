package com.lowes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.NonNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SkillRequestDTO {

    @NonNull
    private String skillName;
    
    @NonNull
    private Double basePrice;
}
