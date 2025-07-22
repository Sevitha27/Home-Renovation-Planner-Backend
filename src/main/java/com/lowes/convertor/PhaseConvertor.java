package com.lowes.convertor;

import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponse;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PhaseConvertor {

    public static PhaseResponse phaseToPhaseResponse(Phase phase){
        PhaseResponse phaseResponse = PhaseResponse.builder()
                .phaseName(phase.getPhaseName())
                .description(phase.getDescription())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .phaseType(phase.getPhaseType())
                .phaseStatus(phase.getPhaseStatus())
                .totalPhaseCost(phase.getTotalPhaseCost())
                .id(phase.getId())
                .build();


        List<PhaseMaterialUserResponse> phaseMaterialUserResponseList = new ArrayList<>();

        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();

        if(!phaseMaterialList.isEmpty()){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                phaseMaterialUserResponseList.add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));
            }
        }

        phaseResponse.setPhaseMaterialUserResponseList(phaseMaterialUserResponseList);

        return phaseResponse;
    }
}
