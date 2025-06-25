package com.lowes.dto.response;


import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Unit;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhaseMaterialResponse {

    String name;

    Unit unit;

    double pricePerQuantity;

    RenovationType renovationType;

    int quantity;

    double totalPrice;

    MaterialResponse materialResponse;

    PhaseResponse phaseResponse;

}
