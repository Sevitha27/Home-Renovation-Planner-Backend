package com.lowes.dto.response;


import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhaseMaterialUserResponse {

    String name;

    Unit unit;

    int pricePerQuantity;

    PhaseType phaseType;

    int quantity;

    int totalPrice;

    PhaseResponse phaseResponse;

}
