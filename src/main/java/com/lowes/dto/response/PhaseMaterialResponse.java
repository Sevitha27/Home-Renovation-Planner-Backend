package com.lowes.dto.response;

import com.example.Home_Renovation.entity.enums.RenovationType;
import com.example.Home_Renovation.entity.enums.Unit;
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
