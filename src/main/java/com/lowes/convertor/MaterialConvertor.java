package com.lowes.convertor;


import com.lowes.dto.request.MaterialAdminRequest;
import com.lowes.dto.response.MaterialAdminResponse;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.entity.Material;
import com.lowes.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class MaterialConvertor {

    public static MaterialUserResponse materialToMaterialUserResponse(Material material){
        MaterialUserResponse materialUserResponse =  MaterialUserResponse.builder()
                                            .name(material.getName())
                                            .unit(material.getUnit())
                                            .phaseType(material.getPhaseType())
                                            .pricePerQuantity(material.getPricePerQuantity())
                                            .build();

        List<PhaseMaterial> phaseMaterialList = material.getPhaseMaterialList();

        if(phaseMaterialList!=null){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                materialUserResponse.getPhaseMaterialUserResponseList().add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));
            }
        }


        return materialUserResponse;
    }

    public static MaterialAdminResponse materialToMaterialAdminResponse(Material material){
        MaterialAdminResponse materialAdminResponse =  MaterialAdminResponse.builder()
                .name(material.getName())
                .unit(material.getUnit())
                .phaseType(material.getPhaseType())
                .pricePerQuantity(material.getPricePerQuantity())
                .deleted(material.isDeleted())
                .build();

        List<PhaseMaterial> phaseMaterialList = material.getPhaseMaterialList();

        if(!phaseMaterialList.isEmpty()){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                materialAdminResponse.getPhaseMaterialUserResponseList().add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));
            }
        }


        return materialAdminResponse;
    }


    public static Material materialAdminRequestToMaterial(MaterialAdminRequest materialAdminRequest){
        Material material = Material.builder()
                .name(materialAdminRequest.getName())
                .unit(materialAdminRequest.getUnit())
                .phaseType(materialAdminRequest.getPhaseType())
                .pricePerQuantity(materialAdminRequest.getPricePerQuantity())
                .build();

        return  material;
    }
}
