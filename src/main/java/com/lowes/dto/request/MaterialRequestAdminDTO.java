package com.lowes.dto.request;


import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialRequestAdminDTO {

    String name;

    Unit unit;

    PhaseType phaseType;

    int pricePerQuantity;


}
