package com.lowes.service;

import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.Project;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lowes.entity.enums.PhaseType.*;

@Service
public class PhaseService {

    @Autowired
    PhaseRepository phaseRepository;


    public void createPhase(PhaseRequestDTO phaseRequestDTO) {
        try {
            Phase phase = new Phase();
            phase.setPhaseType(phaseRequestDTO.getPhaseType());
            phase.setDescription(phaseRequestDTO.getDescription());
            phase.setStartDate(phaseRequestDTO.getStart_date());
            phase.setEndDate(phaseRequestDTO.getEnd_date());
            phase.setVendor(phaseRequestDTO.getVendor());
            phase.setProject(phaseRequestDTO.getProject());
            phase.setPhaseName(phaseRequestDTO.getPhaseName());
            phaseRepository.save(phase);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }

    }

    public Phase getPhaseById(Long id) {
        return phaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Phase not found"));
    }

    public List<Phase> getPhasesByProject(Long projectId) {
        return phaseRepository.findByProject_Id(projectId);
    }

    public Phase updatePhase(Long id, PhaseRequestDTO updatedPhase) {

        Phase phase = phaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Phase not found"));

        if (updatedPhase.getDescription() != null)
            phase.setDescription(updatedPhase.getDescription());

        if (updatedPhase.getPhaseStatus() != null)
            phase.setPhaseStatus(updatedPhase.getPhaseStatus()
            );

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

    public void deletePhase(Long id) {
        phaseRepository.delete(getPhaseById(id));
    }

    public void setVendorCostForPhase(Long vendorId, Long phaseId, Integer cost) {
        Phase phase = phaseRepository.findById(phaseId).orElseThrow(() -> new RuntimeException("Phase not found"));

        if (phase.getVendor() == null || !phase.getVendor().getId().equals(vendorId)) {
            throw new RuntimeException("Unauthorized: Vendor mismatch");
        }
        phase.setVendorCost(cost);

        phaseRepository.save(phase);
    }

    public Integer calculateTotalCost(Long id) {
        Phase phase = phaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Phase not found"));
        int materialCost = 0;
        if (phase.getPhaseMaterialList() != null) {
            materialCost = phase.getPhaseMaterialList().stream()
                    .mapToInt(pm -> pm.getCost() != null ? pm.getCost() : 0)
                    .sum();
        }
        phase.setTotalPhaseCost(phase.getVendorCost() + materialCost);
        phaseRepository.save(phase);
        return phase.getTotalPhaseCost();
    }



    private Map<RenovationType, List<PhaseType>> renovationPhaseMap = new HashMap<>();

    public List<PhaseType> getPhasesByRenovationType(RenovationType renovationType) {
        return renovationPhaseMap.getOrDefault(renovationType, List.of());
    }
    @PostConstruct
    public void initRenovationPhaseMap() {

        renovationPhaseMap.put(RenovationType.KITCHEN_RENOVATION, List.of(
                PLANNING_AND_DESIGN, PLUMBING, HVAC_INSTALLATION,
                ELECTRICITY, FLOORING, CABINET_INSTALLATION, DOORS_AND_WINDOWS_INSTALLATION,
                PAINTING, CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.BATHROOM_RENOVATION, List.of(
                PLANNING_AND_DESIGN, PLUMBING, DOORS_AND_WINDOWS_INSTALLATION,
                ELECTRICITY, FLOORING, CABINET_INSTALLATION, TILING,
                PAINTING, CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.BEDROOM_RENOVATION, List.of(
                PLANNING_AND_DESIGN, ELECTRICITY, DOORS_AND_WINDOWS_INSTALLATION, FLOORING,
                PAINTING, STRUCTURAL_WORK, HVAC_INSTALLATION, TILING,CARPENTRY

        ));
        renovationPhaseMap.put(RenovationType.FULL_HOME_RENOVATION, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK, HVAC_INSTALLATION,
                PLUMBING, ELECTRICITY, FLOORING, TILING, CARPENTRY,
                CABINET_INSTALLATION, DOORS_AND_WINDOWS_INSTALLATION, PAINTING,
                CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.EXTERIOR_RENOVATION, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK,
                DOORS_AND_WINDOWS_INSTALLATION, PAINTING,
                CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.GARAGE_RENOVATION, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK,
                ELECTRICITY, FLOORING, TILING, CARPENTRY,
                DOORS_AND_WINDOWS_INSTALLATION, PAINTING, CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.ATTIC_CONVERSION, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK, HVAC_INSTALLATION,
                ELECTRICITY, FLOORING, TILING, CARPENTRY,
                DOORS_AND_WINDOWS_INSTALLATION, PAINTING,
                CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.BASEMENT_FINISHING, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK, HVAC_INSTALLATION,
                ELECTRICITY, FLOORING, TILING, CARPENTRY, PAINTING,
                CLEANING_AND_WASTE_REMOVAL
        ));

        renovationPhaseMap.put(RenovationType.LIVING_ROOM_REMODEL, List.of(
                PLANNING_AND_DESIGN, STRUCTURAL_WORK, HVAC_INSTALLATION,
                ELECTRICITY, FLOORING, TILING, CARPENTRY,
                DOORS_AND_WINDOWS_INSTALLATION, PAINTING
        ));

    }
}