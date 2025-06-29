package com.lowes.service;

import com.lowes.convertor.PhaseMaterialConvertor;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.PhaseRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.lowes.entity.enums.PhaseType.*;

@Service
public class PhaseService {

    @Autowired
    private PhaseRepository phaseRepository;

    private final Map<RenovationType, List<PhaseType>> renovationPhaseMap = new HashMap<>();

    public void createPhase(PhaseRequestDTO phaseRequestDTO) {
        boolean exists = phaseRepository.existsByProjectAndPhaseType(
                phaseRequestDTO.getProject(), phaseRequestDTO.getPhaseType());

        if (exists) {
            throw new RuntimeException("Phase of this type already exists for the project");
        }

        Phase phase = new Phase();
        phase.setPhaseType(phaseRequestDTO.getPhaseType());
        phase.setDescription(phaseRequestDTO.getDescription());
        phase.setStartDate(phaseRequestDTO.getStart_date());
        phase.setEndDate(phaseRequestDTO.getEnd_date());
        phase.setVendor(phaseRequestDTO.getVendor());
        phase.setProject(phaseRequestDTO.getProject());
        phase.setPhaseName(phaseRequestDTO.getPhaseName());

        phaseRepository.save(phase);
    }

    public Phase getPhaseById(UUID id) {
        return phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phase not found"));
    }

    public List<Phase> getPhasesByProject(UUID projectId) {
        return phaseRepository.findByProject_Id(projectId);
    }

    public Phase updatePhase(UUID id, PhaseRequestDTO updatedPhase) {
        Phase phase = getPhaseById(id);

        if (updatedPhase.getDescription() != null)
            phase.setDescription(updatedPhase.getDescription());

        if (updatedPhase.getPhaseStatus() != null)
            phase.setPhaseStatus(updatedPhase.getPhaseStatus());

        if (updatedPhase.getStart_date() != null)
            phase.setStartDate(updatedPhase.getStart_date());

        if (updatedPhase.getEnd_date() != null)
            phase.setEndDate(updatedPhase.getEnd_date());

        if (updatedPhase.getPhaseType() != null)
            phase.setPhaseType(updatedPhase.getPhaseType());

        if (updatedPhase.getVendor() != null)
            phase.setVendor(updatedPhase.getVendor());

        if (updatedPhase.getProject() != null)
            phase.setProject(updatedPhase.getProject());

        if (updatedPhase.getPhaseName() != null)
            phase.setPhaseName(updatedPhase.getPhaseName());

        return phaseRepository.save(phase);
    }

    public void deletePhase(UUID id) {
        phaseRepository.delete(getPhaseById(id));
    }

    public void setVendorCostForPhase(UUID vendorId, UUID phaseId, Integer cost) {
        Phase phase = getPhaseById(phaseId);
        if (phase.getVendor() == null || !phase.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Unauthorized: Vendor mismatch");
        }
        phase.setVendorCost(cost);
        phaseRepository.save(phase);
    }

    public List<PhaseMaterialUserResponse> getAllPhaseMaterialsByPhaseId(UUID id) {
        Phase phase = getPhaseById(id);

        List<PhaseMaterialUserResponse> responseList = new ArrayList<>();
        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();

        if (phaseMaterialList != null && !phaseMaterialList.isEmpty()) {
            for (PhaseMaterial phaseMaterial : phaseMaterialList) {
                responseList.add(PhaseMaterialConvertor
                        .phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));
            }
        }
        return responseList;
    }

    public int calculateTotalCost(UUID id) {
        Phase phase = getPhaseById(id);
        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();
        int materialCost = 0;

        if (phaseMaterialList != null && !phaseMaterialList.isEmpty()) {
            materialCost = phaseMaterialList.stream()
                    .mapToInt(pm -> Objects.nonNull(pm.getTotalPrice()) ? pm.getTotalPrice() : 0)
                    .sum();
        }

        int vendorCost = phase.getVendorCost() != null ? phase.getVendorCost() : 0;
        int totalCost = vendorCost + materialCost;

        phase.setTotalPhaseCost(totalCost);
        phaseRepository.save(phase);

        return totalCost;
    }

    public List<PhaseType> getPhasesByRenovationType(RenovationType renovationType) {
        return renovationPhaseMap.getOrDefault(renovationType, List.of());
    }

    @PostConstruct
    public void initRenovationPhaseMap() {
        renovationPhaseMap.put(RenovationType.KITCHEN_RENOVATION, List.of(
                PhaseType.values()
        ));

        renovationPhaseMap.put(RenovationType.BATHROOM_RENOVATION, List.of(
                PLUMBING, ELECTRICITY, TILING, PAINTING,STRUCTURAL_WORK
        ));

        renovationPhaseMap.put(RenovationType.BEDROOM_RENOVATION, List.of(
                ELECTRICITY, PAINTING, STRUCTURAL_WORK,
                TILING, CARPENTRY
        ));

        renovationPhaseMap.put(RenovationType.FULL_HOME_RENOVATION, List.of(
                PhaseType.values()
        ));

        renovationPhaseMap.put(RenovationType.EXTERIOR_RENOVATION, List.of(
                STRUCTURAL_WORK, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.GARAGE_RENOVATION, List.of(
                PhaseType.values()
        ));

        renovationPhaseMap.put(RenovationType.ATTIC_CONVERSION, List.of(
                STRUCTURAL_WORK, ELECTRICITY, TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.BASEMENT_FINISHING, List.of(
                STRUCTURAL_WORK, ELECTRICITY,TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.LIVING_ROOM_REMODEL, List.of(
                STRUCTURAL_WORK, ELECTRICITY, TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.BALCONY_RENOVATION, List.of(
                STRUCTURAL_WORK, ELECTRICITY, TILING, PAINTING
        ));
    }
}
