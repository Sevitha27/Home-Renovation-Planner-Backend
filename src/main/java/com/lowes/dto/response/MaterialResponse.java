package com.lowes.dto.response;

import com.example.Home_Renovation.entity.enums.RenovationType;
import com.example.Home_Renovation.entity.enums.Unit;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {

    String name;

    Unit unit;

    RenovationType renovationType;

    double pricePerQuantity;

    List<PhaseMaterialResponse> phaseMaterialResponseList;
}
