package com.lowes.dto.response;


import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Unit;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseMaterialUserResponse {

    String name;

    Unit unit;

    double pricePerQuantity;

    RenovationType renovationType;

    int quantity;

    double totalPrice;

    MaterialUserResponse materialUserResponse;

    PhaseResponse phaseResponse;

}
