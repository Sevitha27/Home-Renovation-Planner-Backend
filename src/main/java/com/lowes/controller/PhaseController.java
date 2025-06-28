package com.lowes.controller;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.service.PhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/phase")
@RequiredArgsConstructor
public class PhaseController {

    @Autowired
    PhaseService phaseService;

    //working
    @PostMapping
    public String createPhase(@RequestBody PhaseRequestDTO phaseRequestDTO) {
        try {
            phaseService.createPhase(phaseRequestDTO);
            return "Phase created successfully";
        }
        catch (Exception e){
            return "exception occured"+e.getMessage();
        }
    }

    //working
    @GetMapping("/{id}")
    public Phase getPhaseById(@PathVariable UUID id) {
        return phaseService.getPhaseById(id);
    }

    //working
    @GetMapping("/project/{projectId}")
    public List<Phase> getPhasesByProject(@PathVariable UUID projectId) {
        return phaseService.getPhasesByProject(projectId);
    }

    //working
    @PutMapping("/{id}")
    public Phase updatePhase(@PathVariable UUID id, @RequestBody PhaseRequestDTO updatedPhaseRequestDTO) {
        return phaseService.updatePhase(id, updatedPhaseRequestDTO);
    }

    //working
    @PostMapping("/vendor/{vendorId}/phase/{phaseId}/cost")
    public String setVendorCost(@PathVariable UUID vendorId, @PathVariable UUID phaseId, @RequestParam Integer cost) {
        phaseService.setVendorCostForPhase(vendorId, phaseId, cost);
        return "Cost updated successfully";
    }

    //working
//    @GetMapping("/{id}/total-cost")
//    public Integer calculatePhaseTotalCost(@PathVariable UUID id) {
//        return phaseService.calculateTotalCost(id);
//    }

    //working
    @DeleteMapping("/{id}")
    public String deletePhase(@PathVariable UUID id) {
        phaseService.deletePhase(id);
        return "Phase deleted successfully";
    }

    //working
    @GetMapping("/phases/by-renovation-type/{type}")
    public List<PhaseType> getPhasesByRenovationType(@PathVariable RenovationType type) {
        return phaseService.getPhasesByRenovationType(type);
    }


}
