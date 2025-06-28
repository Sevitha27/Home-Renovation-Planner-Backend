package com.lowes.dto.response;


import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.Unit;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialAdminResponse {

    String name;

    Unit unit;

    PhaseType phaseType;

    int pricePerQuantity;

    boolean deleted;
}
