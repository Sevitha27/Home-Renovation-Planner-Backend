package com.lowes.convertor;

import com.example.Home_Renovation.dto.request.MaterialRequest;
import com.example.Home_Renovation.dto.response.MaterialResponse;
import com.example.Home_Renovation.entity.Material;
import com.example.Home_Renovation.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MaterialConvertor {

    public static MaterialResponse materialToMaterialResponse(Material material){
        MaterialResponse materialResponse =  MaterialResponse.builder()
                                            .name(material.getName())
                                            .unit(material.getUnit())
                                            .renovationType(material.getRenovationType())
                                            .pricePerQuantity(material.getPricePerQuantity())
                                            .build();

        List<PhaseMaterial> phaseMaterialList = material.getPhaseMaterial();

        if(phaseMaterialList!=null){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                materialResponse.getPhaseMaterialResponseList().add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialResponse(phaseMaterial));
            }
        }


        return materialResponse;
    }

    public static Material materialRequestToMaterial(MaterialRequest materialRequest){
        Material material = Material.builder()
                .name(materialRequest.getName())
                .unit(materialRequest.getUnit())
                .renovationType(materialRequest.getRenovationType())
                .pricePerQuantity(materialRequest.getPricePerQuantity())
                .build();

        return  material;
    }
}
