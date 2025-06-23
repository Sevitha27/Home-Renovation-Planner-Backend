package com.lowes.convertor;

import com.example.Home_Renovation.dto.request.PhaseMaterialRequest;
import com.example.Home_Renovation.dto.response.PhaseMaterialResponse;
import com.example.Home_Renovation.entity.Phase;
import com.example.Home_Renovation.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;


@UtilityClass
public class PhaseMaterialConvertor {

    public static PhaseMaterialResponse phaseMaterialToPhaseMaterialResponse(PhaseMaterial phaseMaterial){

        PhaseMaterialResponse phaseMaterialResponse = PhaseMaterialResponse.builder()
                .renovationType(phaseMaterial.getRenovationType())
                .quantity(phaseMaterial.getQuantity())
                .name(phaseMaterial.getName())
                .unit(phaseMaterial.getUnit())
                .pricePerQuantity(phaseMaterial.getPricePerQuantity())
                .totalPrice(phaseMaterial.getTotalPrice())
                .phaseResponse(PhaseConvertor.PhaseToPhaseResponse(phaseMaterial.getPhase()))
                .materialResponse(MaterialConvertor.materialToMaterialResponse(phaseMaterial.getMaterial()))
                .build();

        return phaseMaterialResponse;
    }

    public static PhaseMaterial phaseMaterialRequestToPhaseMaterial(Phase phase, PhaseMaterialRequest phaseMaterialRequest){

        PhaseMaterial phaseMaterial = PhaseMaterial.builder()
                .quantity(phaseMaterialRequest.getQuantity())
                .phase(phase)
                .build();

        return phaseMaterial;
    }
}
