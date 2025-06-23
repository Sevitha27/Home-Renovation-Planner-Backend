package com.lowes.dto.request;

import com.example.Home_Renovation.entity.enums.RenovationType;
import com.example.Home_Renovation.entity.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialRequest {

    String name;

    Unit unit;

    RenovationType renovationType;

    double pricePerQuantity;


}
