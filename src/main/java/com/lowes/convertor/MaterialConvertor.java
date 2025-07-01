package com.lowes.convertor;


import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.Material;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MaterialConvertor {

    public static MaterialUserResponse materialToMaterialUserResponse(Material material){
        return MaterialUserResponse.builder()
                .exposedId(material.getExposedId())
                .name(material.getName())
                .unit(material.getUnit())
                .phaseType(material.getPhaseType())
                .pricePerQuantity(material.getPricePerQuantity())
                .build();
    }
}
