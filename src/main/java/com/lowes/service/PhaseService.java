package com.lowes.service;

import com.lowes.convertor.PhaseMaterialConvertor;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.exception.ElementNotFoundException;
import com.lowes.repository.PhaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PhaseService {

    @Autowired
    PhaseRepository phaseRepository;

    public void createPhase(PhaseRequestDTO phaseRequestDTO) {
        try {
            Phase phase = new Phase();
            phase.setPhaseType(phaseRequestDTO.getPhaseType());
            phase.setDescription(phaseRequestDTO.getDescription());
            phase.setStartDate(phaseRequestDTO.getStartDate());
            phase.setEndDate(phaseRequestDTO.getEndDate());
            phase.setVendor(phaseRequestDTO.getVendor());
            phase.setProject(phaseRequestDTO.getProject());
            phase.setPhaseName(phaseRequestDTO.getPhaseName());
            phaseRepository.save(phase);
        } catch (Exception e) {
            System.out.println("Error:"+e.getMessage());
        }

    }

    public Phase getPhaseById(UUID id) {
        return phaseRepository.findById(id).orElseThrow(()->new RuntimeException("Phase not found"));
    }

    public List<Phase> getPhasesByProject(UUID projectId) {
        return phaseRepository.findByProject_Id(projectId);
    }

    public Phase updatePhase(UUID id, PhaseRequestDTO updatedPhase) {
        Phase phase=phaseRepository.findById(id).orElseThrow(()->new RuntimeException("Phase not found"));

        if (updatedPhase.getDescription() != null)
            phase.setDescription(updatedPhase.getDescription());

        if (updatedPhase.getPhaseStatus() != null)
            phase.setPhaseStatus(updatedPhase.getPhaseStatus()
            );

        if (updatedPhase.getStartDate() != null)
            phase.setStartDate(updatedPhase.getStartDate());

        if (updatedPhase.getEndDate() != null)
            phase.setEndDate(updatedPhase.getEndDate());

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


    public void setVendorCostForPhase(UUID vendorId, UUID phaseId, Integer cost) {
        Phase phase=phaseRepository.findById(phaseId).orElseThrow(()->new RuntimeException("Phase not found"));

        if (phase.getVendor() == null || !phase.getVendor().getExposedId().equals(vendorId)) {
            throw new RuntimeException("Unauthorized: Vendor mismatch");
        }
        phase.setVendorCost(cost);

        phaseRepository.save(phase);
    }

//    public Integer calculateTotalCost(UUID id) {
//        Phase phase=phaseRepository.findById(id).orElseThrow(()-> new RuntimeException("Phase not found"));
//        int materialCost=0;
//        if (phase.getPhaseMaterialList()!=null)
//        {
//            materialCost = phase.getPhaseMaterialList().stream()
//                    .mapToInt(pm -> pm.getCost() != null ? pm.getCost() : 0)
//                    .sum();
//        }
//        phase.setTotalPhaseCost(phase.getVendorCost()+materialCost);
//        phaseRepository.save(phase);
//        return phase.getTotalPhaseCost();
//    }

    public List<PhaseMaterialUserResponse> getAllPhaseMaterialsByPhaseId(UUID id){
        Optional<Phase> optionalPhase = phaseRepository.findById(id);
        if(optionalPhase.isEmpty()){
            throw new ElementNotFoundException("Phase Not Found To Fetch Phase Materials");
        }
        Phase phase = optionalPhase.get();

        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();
        List<PhaseMaterialUserResponse> phaseMaterialUserResponseList = new ArrayList<>();
        if(!phaseMaterialList.isEmpty()){
            for(PhaseMaterial phaseMaterial : phaseMaterialList){
                phaseMaterialUserResponseList.add(PhaseMaterialConvertor.phaseMaterialToPhaseMaterialUserResponse(phaseMaterial));
            }
        }

        return phaseMaterialUserResponseList;
    }

    @Transactional
    public int updateTotalCost(UUID id){
        Optional<Phase> optionalPhase = phaseRepository.findById(id);
        if(optionalPhase.isEmpty()){
            throw new ElementNotFoundException("Phase Not Found To Fetch Phase Materials");
        }
        System.out.println('a');
        Phase phase = optionalPhase.get();

        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();

        int totalCost = 0;
        for(PhaseMaterial phaseMaterial : phaseMaterialList){
            totalCost+=phaseMaterial.getTotalPrice();
        }
        phase.setTotalPhaseMaterialCost(totalCost);
        phaseRepository.save(phase);
        return totalCost;
    }
}
