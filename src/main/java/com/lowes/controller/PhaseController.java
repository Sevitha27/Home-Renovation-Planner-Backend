package com.lowes.controller;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Material;
import com.lowes.entity.Phase;
import com.lowes.entity.Room;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.repository.PhaseRepository;
import com.lowes.service.PhaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/phase")
@RequiredArgsConstructor
public class PhaseController {

    @Autowired
    PhaseService phaseService;

    @Autowired
    PhaseRepository phaseRepository;
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
    @PostMapping("/vendor/{vendorId}/phase/{phaseId}/cost")
    public String setVendorCost(@PathVariable UUID vendorId, @PathVariable UUID phaseId, @RequestParam Integer cost) {
        phaseService.setVendorCostForPhase(vendorId, phaseId, cost);
        return "Cost updated successfully";
    }

    //working

    @PutMapping("/{id}")
    public Phase updatePhase(@PathVariable UUID id, @RequestBody PhaseRequestDTO updatedPhaseRequestDTO) {
        return phaseService.updatePhase(id, updatedPhaseRequestDTO);
    }

    //working

    @GetMapping("/room/{roomExposedId}")
    public List<PhaseResponseDTO> getPhasesByRoom(@PathVariable UUID roomExposedId) {
        return phaseService.getPhasesByRoomExposedId(roomExposedId);
    }
    //working
    @GetMapping("/{id}/total-cost")
    public Integer calculatePhaseTotalCost(@PathVariable UUID id) {
        return phaseService.calculateTotalCost(id);
    }



    @GetMapping("/materials")
    public List<PhaseMaterialUserResponse> getMaterilsById(@RequestParam UUID id){
        return phaseService.getAllPhaseMaterialsByPhaseId(id);
    }


    @GetMapping("/phases/by-renovation-type/{type}")
    public List<PhaseType> getPhasesByRenovationType(@PathVariable RenovationType type) {
        List<PhaseType> phases = phaseService.getPhasesByRenovationType(type);
        if (phases == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No phases found for renovation type: " + type);
        }
        return phases;
    }


    @GetMapping("/phase/exists")
    public boolean doesPhaseExist(@RequestParam UUID roomId, @RequestParam PhaseType phaseType) {


        return phaseRepository.existsByRoomIdAndPhaseType(roomId, phaseType);
    }

    @DeleteMapping("delete/{id}")
    public void deletePhase(@PathVariable UUID id){
        phaseService.deletePhase(id);
    }

}
