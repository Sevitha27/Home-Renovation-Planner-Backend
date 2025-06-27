package com.lowes.convertor;

import com.lowes.dto.response.PhaseResponse;
import com.lowes.entity.Phase;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PhaseConvertor {

    public static PhaseResponse phaseToPhaseResponse(Phase phase){
        PhaseResponse phaseResponse = PhaseResponse.builder()
                .build();

        return phaseResponse;
    }
}
