package com.lowes.convertor;


import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;


@UtilityClass
public class PhaseMaterialConvertor {

    public static PhaseMaterialUserResponse phaseMaterialToPhaseMaterialUserResponse(PhaseMaterial phaseMaterial){

        PhaseMaterialUserResponse phaseMaterialUserResponse = PhaseMaterialUserResponse.builder()
                .renovationType(phaseMaterial.getRenovationType())
                .quantity(phaseMaterial.getQuantity())
                .name(phaseMaterial.getName())
                .unit(phaseMaterial.getUnit())
                .pricePerQuantity(phaseMaterial.getPricePerQuantity())
                .totalPrice(phaseMaterial.getTotalPrice())
                .phaseResponse(PhaseConvertor.PhaseToPhaseResponse(phaseMaterial.getPhase()))
                .materialUserResponse(MaterialConvertor.materialToMaterialUserResponse(phaseMaterial.getMaterial()))
                .build();

        return phaseMaterialUserResponse;
    }

    public static PhaseMaterial phaseMaterialUserRequestToPhaseMaterial(Phase phase, PhaseMaterialUserRequest phaseMaterialUserRequest){

        PhaseMaterial phaseMaterial = PhaseMaterial.builder()
                .quantity(phaseMaterialUserRequest.getQuantity())
                .phase(phase)
                .build();

        return phaseMaterial;
    }
}
