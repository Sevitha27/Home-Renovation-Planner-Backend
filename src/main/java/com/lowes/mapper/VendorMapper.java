package com.lowes.mapper;

import com.lowes.dto.response.vendor.PhaseMaterialDTO;
import com.lowes.dto.response.vendor.PhaseResponseDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendorMapper {
    public PhaseResponseDTO toPhaseResponseDTO(Phase phase) {
        return PhaseResponseDTO.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName())
                .description(phase.getDescription())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .phaseType(phase.getPhaseType())
                .phaseStatus(phase.getPhaseStatus())
                .vendorCost(phase.getVendorCost())
                .materials(toPhaseMaterialDTOList(phase.getPhaseMaterialList()))
                .build();
    }

    public List<PhaseResponseDTO> toPhaseResponseDTOList(List<Phase> phases) {
        return phases.stream()
                .map(this::toPhaseResponseDTO)
                .collect(Collectors.toList());
    }

    public PhaseMaterialDTO toPhaseMaterialDTO(PhaseMaterial material) {
        return PhaseMaterialDTO.builder()
                .exposedId(material.getExposedId())
                .name(material.getName())
                .unit(material.getUnit())
                .phaseType(material.getPhaseType())
                .quantity(material.getQuantity())
                .pricePerQuantity(material.getPricePerQuantity())
                .totalPrice(material.getTotalPrice())
                .build();
    }

    public List<PhaseMaterialDTO> toPhaseMaterialDTOList(List<PhaseMaterial> materialList) {
        return materialList.stream()
                .map(this::toPhaseMaterialDTO)
                .collect(Collectors.toList());
    }
}
