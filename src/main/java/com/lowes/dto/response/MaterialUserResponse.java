package com.lowes.dto.response;


import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Unit;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialUserResponse {

    String name;

    Unit unit;

    RenovationType renovationType;

    double pricePerQuantity;

    List<PhaseMaterialUserResponse> phaseMaterialUserResponseList;
}
