package com.lowes.dto.response.vendor;

import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import lombok.*;


import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhaseMaterialDTO {
    private UUID exposedId;
    private String name;
    private Unit unit;
    private PhaseType phaseType;
    private int quantity;
    private int pricePerQuantity;
    private int totalPrice;
}
