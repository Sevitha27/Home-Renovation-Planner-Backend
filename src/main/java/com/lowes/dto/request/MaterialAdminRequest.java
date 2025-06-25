package com.lowes.dto.request;


import com.lowes.entity.enums.RenovationType;
import com.lowes.entity.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialAdminRequest {

    String name;

    Unit unit;

    RenovationType renovationType;

    double pricePerQuantity;


}
