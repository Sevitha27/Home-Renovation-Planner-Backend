package com.lowes.convertor;


import com.lowes.dto.request.PhaseMaterialUserRequest;
import com.lowes.dto.response.MaterialUserResponse;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;

import java.util.UUID;


@UtilityClass
public class PhaseMaterialConvertor {

    public static PhaseMaterialUserResponse phaseMaterialToPhaseMaterialUserResponse(PhaseMaterial phaseMaterial){

        PhaseMaterialUserResponse phaseMaterialUserResponse = PhaseMaterialUserResponse.builder()
                .exposedId(phaseMaterial.getExposedId())
                .phaseType(phaseMaterial.getPhaseType())
                .quantity(phaseMaterial.getQuantity())
                .name(phaseMaterial.getName())
                .unit(phaseMaterial.getUnit())
                .pricePerQuantity(phaseMaterial.getPricePerQuantity())
                .totalPrice(phaseMaterial.getTotalPrice())
                .materialExposedId(phaseMaterial.getMaterial().getExposedId())
                .phaseId(phaseMaterial.getPhase().getId())
                .build();

        return phaseMaterialUserResponse;
    }

    public static PhaseMaterial phaseMaterialUserRequestToPhaseMaterial(Phase phase, PhaseMaterialUserRequest phaseMaterialUserRequest){

        PhaseMaterial phaseMaterial = PhaseMaterial.builder()
                .quantity(phaseMaterialUserRequest.getQuantity())
                .phase(phase)
                .exposedId(UUID.randomUUID())
                .build();

        return phaseMaterial;
    }
}
