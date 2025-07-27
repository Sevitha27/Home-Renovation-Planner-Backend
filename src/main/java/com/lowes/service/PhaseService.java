package com.lowes.service;

import com.lowes.convertor.PhaseConvertor;
import com.lowes.convertor.PhaseMaterialConvertor;
import com.lowes.dto.request.PhaseRequestDTO;
import com.lowes.dto.response.PhaseMaterialUserResponse;
import com.lowes.dto.response.PhaseResponseDTO;
import com.lowes.entity.Phase;
import com.lowes.entity.PhaseMaterial;
import com.lowes.entity.Room;
import com.lowes.entity.Vendor;
import com.lowes.entity.enums.PhaseType;
import com.lowes.entity.enums.RenovationType;
import com.lowes.mapper.PhaseMapper;
import com.lowes.repository.PhaseRepository;
import com.lowes.repository.RoomRepository;
import com.lowes.repository.VendorRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.lowes.dto.response.PhaseResponse;

import java.util.*;
import java.util.stream.Collectors;

import static com.lowes.entity.enums.PhaseType.*;

@Service
public class PhaseService {

    @Autowired
    PhaseRepository phaseRepository;

    @Autowired
    RoomRepository roomRepository;
    @Autowired
    VendorRepository vendorRepository;


    private final Map<RenovationType, List<PhaseType>> renovationPhaseMap = new HashMap<>();

    public void createPhase(PhaseRequestDTO phaseRequestDTO) {

        Room room = roomRepository.findByExposedId(phaseRequestDTO.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));


        Vendor vendor = vendorRepository.findByExposedId(phaseRequestDTO.getVendorId());


        if (phaseRepository.existsByRoomExposedIdAndPhaseType(phaseRequestDTO.getRoomId(),  phaseRequestDTO.getPhaseType())) {
            throw new IllegalArgumentException("Phase of type " + phaseRequestDTO.getPhaseType() + " already exists for this room");
        }

        Phase phase = new Phase();
        phase.setPhaseType(phaseRequestDTO.getPhaseType());
        phase.setDescription(phaseRequestDTO.getDescription());
        phase.setStartDate(phaseRequestDTO.getStartDate());
        phase.setEndDate(phaseRequestDTO.getEndDate());
        phase.setRoom(room);
        phase.setVendor(vendor);
        phase.setPhaseName(phaseRequestDTO.getPhaseName());
        phase.setPhaseStatus(phaseRequestDTO.getPhaseStatus());

        phaseRepository.save(phase);
    }

    @Transactional(readOnly = true)
    public PhaseResponse getPhaseById(UUID id) {
        Phase phase=phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phase not found"));

        return PhaseConvertor.phaseToPhaseResponse(phase);
        //return PhaseMapper.toDTO(phase);
    }

    @Transactional(readOnly = true)
    public List<PhaseResponseDTO> getPhasesByRoom(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new EntityNotFoundException("Room not found with ID: " + roomId);
        }

        List<Phase> phases = phaseRepository.findAllByRoom_Id(roomId);
        return phases.stream()
                .map(PhaseResponseDTO::new)
                .collect(Collectors.toList());
    }


    public Phase updatePhase(UUID id, PhaseRequestDTO updatedPhase) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + id));

        if (updatedPhase.getDescription() != null)
            phase.setDescription(updatedPhase.getDescription());

        if (updatedPhase.getPhaseStatus() != null)
            phase.setPhaseStatus(updatedPhase.getPhaseStatus());

        if (updatedPhase.getStartDate() != null)
            phase.setStartDate(updatedPhase.getStartDate());

        if (updatedPhase.getEndDate() != null)
            phase.setEndDate(updatedPhase.getEndDate());

        if (updatedPhase.getPhaseType() != null)
            phase.setPhaseType(updatedPhase.getPhaseType());

        if (updatedPhase.getPhaseName() != null)
            phase.setPhaseName(updatedPhase.getPhaseName());

        return phaseRepository.save(phase);
    }

    @Transactional
    public void deletePhase(UUID id) {
        // Get the entity first to ensure it exists
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + id));

        phaseRepository.delete(phase);
    }

    @Transactional
    public PhaseResponse setVendorCostForPhase(UUID vendorId, UUID phaseId, Integer cost) {
        // Get the phase entity
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + phaseId));

        // Verify vendor exists and matches
        if (phase.getVendor() == null || !phase.getVendor().getExposedId().equals(vendorId)) {
            throw new RuntimeException("Unauthorized: Vendor mismatch or vendor not assigned");
        }

        // Set the cost and save
        phase.setVendorCost(cost);
        Phase updatedPhase = phaseRepository.save(phase);

        // Return the updated phase as DTO
        return PhaseMapper.toDTO(updatedPhase);
    }

    @Transactional(readOnly = true)
    public List<PhaseMaterialUserResponse> getAllPhaseMaterialsByPhaseId(UUID id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phase not found with id: " + id));

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

    @Transactional
    public int calculateTotalCost(UUID id) {
        Optional<Phase> optionalPhase = phaseRepository.findById(id);
        if (optionalPhase.isEmpty()) {
            throw new RuntimeException("Phase Not Found To Calculate Total Cost");
        }

        Phase phase = optionalPhase.get();
        List<PhaseMaterial> phaseMaterialList = phase.getPhaseMaterialList();
        System.out.println(phaseMaterialList.size());
        int materialCost = 0;
//        if (phaseMaterialList != null && !phaseMaterialList.isEmpty()) {
//            materialCost = phaseMaterialList.stream()
//                    .mapToInt(pm -> Objects.nonNull(pm.getTotalPrice()) ? pm.getTotalPrice() : 0)
//                    .sum();
//        }

        if(!phaseMaterialList.isEmpty()){
            for (PhaseMaterial phaseMaterial : phaseMaterialList) {
                System.out.println(phaseMaterial.getName()+phaseMaterial.getTotalPrice());
                materialCost+=phaseMaterial.getTotalPrice();
            }
        }

        phase.setTotalPhaseMaterialCost(materialCost);
        System.out.println(materialCost);
        int vendorCost = phase.getVendorCost() != null ? phase.getVendorCost() : 0;
        int totalCost = vendorCost + materialCost;
        phase.setTotalPhaseCost(totalCost);
        System.out.println(totalCost);
        phaseRepository.save(phase);

        return totalCost;
    }


    public List<PhaseType> getPhasesByRenovationType(RenovationType renovationType) {
        return renovationPhaseMap.getOrDefault(renovationType, List.of());
    }

    @PostConstruct
    public void initRenovationPhaseMap() {
        renovationPhaseMap.put(RenovationType.KITCHEN_RENOVATION, List.of(
                values()
        ));

        renovationPhaseMap.put(RenovationType.BATHROOM_RENOVATION, List.of(
                PLUMBING, ELECTRICAL, TILING, PAINTING,CIVIL
        ));

        renovationPhaseMap.put(RenovationType.BEDROOM_RENOVATION, List.of(
                ELECTRICAL, PAINTING, CIVIL,
                TILING, CARPENTRY
        ));

        renovationPhaseMap.put(RenovationType.FULL_HOME_RENOVATION, List.of(
                values()
        ));

        renovationPhaseMap.put(RenovationType.EXTERIOR_RENOVATION, List.of(
                CIVIL, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.GARAGE_RENOVATION, List.of(
                values()
        ));

        renovationPhaseMap.put(RenovationType.ATTIC_CONVERSION, List.of(
                CIVIL, ELECTRICAL, TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.BASEMENT_FINISHING, List.of(
                CIVIL, ELECTRICAL,TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.LIVING_ROOM_REMODEL, List.of(
                CIVIL, ELECTRICAL, TILING, CARPENTRY, PAINTING
        ));

        renovationPhaseMap.put(RenovationType.BALCONY_RENOVATION, List.of(
                CIVIL, ELECTRICAL, TILING, PAINTING
        ));
    }

    public List<PhaseResponse> getPhasesByRoomExposedId(UUID exposedId) {
        List<Phase> phases = phaseRepository.findAllByRoom_ExposedId(exposedId);
        return phases.stream()
                .map(PhaseMapper::toDTO) // Uses the PhaseMapper
                .collect(Collectors.toList());
    }

}